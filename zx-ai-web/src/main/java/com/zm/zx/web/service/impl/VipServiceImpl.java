package com.zm.zx.web.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zm.zx.common.exception.BizException;
import com.zm.zx.common.response.PageResponse;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.domain.dto.VipOrderCreateDTO;
import com.zm.zx.web.domain.dto.VipPaymentDTO;
import com.zm.zx.web.domain.po.User;
import com.zm.zx.web.domain.po.UserWallet;
import com.zm.zx.web.domain.po.VipOrder;
import com.zm.zx.web.domain.po.WalletTransaction;
import com.zm.zx.web.domain.vo.VipOrderVO;
import com.zm.zx.web.domain.vo.VipPackageVO;
import com.zm.zx.web.domain.vo.VipStatusVO;
import com.zm.zx.web.mapper.UserMapper;
import com.zm.zx.web.mapper.UserWalletMapper;
import com.zm.zx.web.mapper.VipOrderMapper;
import com.zm.zx.web.mapper.WalletTransactionMapper;
import com.zm.zx.web.service.VipService;
import com.zm.zx.web.service.WalletService;
import com.zm.zx.web.service.WebMembershipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.zm.zx.common.constant.RedisKey.VIP_PACKAGE_INFO;

/**
 * VIP Service 实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VipServiceImpl implements VipService {

    private final VipOrderMapper vipOrderMapper;
    private final UserMapper userMapper;
    private final UserWalletMapper walletMapper;
    private final WalletTransactionMapper transactionMapper;
    private final WalletService walletService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final WebMembershipService webMembershipService;

    @Override
    public Response getPackages() {
        log.info("获取VIP套餐列表 - 使用WebMembershipService（带Redis缓存）");
        // 直接调用WebMembershipService，它内部已经实现了Redis缓存逻辑
        return webMembershipService.getMembershipList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response createOrder(VipOrderCreateDTO dto) {
        log.info("创建VIP订单，packageType: {}, paymentMethod: {}", dto.getPackageType(), dto.getPaymentMethod());

        // 获取当前登录用户
        if (!StpUtil.isLogin()) {
            throw new BizException("请先登录");
        }
        Long userId = StpUtil.getLoginIdAsLong();

        // 验证套餐类型
        if (dto.getPackageType() < 1 || dto.getPackageType() > 3) {
            throw new BizException("套餐类型不正确");
        }

        // 验证支付方式
        if (dto.getPaymentMethod() < 1 || dto.getPaymentMethod() > 2) {
            throw new BizException("支付方式不正确");
        }

        // 获取套餐信息
        Map<Integer, VipPackageInfo> packageInfoMap = getPackageInfoMap();
        VipPackageInfo packageInfo = packageInfoMap.get(dto.getPackageType());

        // 生成订单号（时间戳 + 用户ID）
        String orderNo = "VIP" + System.currentTimeMillis() + userId;

        // 创建订单
        VipOrder order = VipOrder.builder()
                .orderNo(orderNo)
                .userId(userId)
                .packageType(dto.getPackageType())
                .packageName(packageInfo.getName())
                .duration(packageInfo.getDuration())
                .price(packageInfo.getPrice())
                .paymentMethod(dto.getPaymentMethod())
                .status(0) // 待支付
                .build();

        vipOrderMapper.insert(order);

        log.info("VIP订单创建成功，orderNo: {}, paymentMethod: {}", orderNo, dto.getPaymentMethod());

        // 转换为VO
        VipOrderVO orderVO = convertToVO(order);
        return Response.success(orderVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response payOrder(VipPaymentDTO dto) {
        log.info("支付VIP订单，orderNo: {}", dto.getOrderNo());

        // 获取当前登录用户
        if (!StpUtil.isLogin()) {
            throw new BizException("请先登录");
        }
        Long userId = StpUtil.getLoginIdAsLong();

        // 查询订单
        LambdaQueryWrapper<VipOrder> orderQuery = new LambdaQueryWrapper<>();
        orderQuery.eq(VipOrder::getOrderNo, dto.getOrderNo());
        VipOrder order = vipOrderMapper.selectOne(orderQuery);

        if (order == null) {
            throw new BizException("订单不存在");
        }

        if (!order.getUserId().equals(userId)) {
            throw new BizException("无权操作此订单");
        }

        if (order.getStatus() != 0) {
            throw new BizException("订单状态不正确");
        }

        // 验证支付密码
        if (!walletService.verifyPaymentPassword(dto.getPaymentPassword())) {
            throw new BizException("支付密码错误");
        }

        // 查询钱包
        LambdaQueryWrapper<UserWallet> walletQuery = new LambdaQueryWrapper<>();
        walletQuery.eq(UserWallet::getUserId, userId);
        UserWallet wallet = walletMapper.selectOne(walletQuery);

        if (wallet == null || wallet.getIsActivated() == 0) {
            throw new BizException("钱包未开通");
        }

        if (wallet.getStatus() != 1) {
            throw new BizException("钱包已冻结");
        }

        // 检查余额
        if (wallet.getBalance().compareTo(order.getPrice()) < 0) {
            throw new BizException("余额不足");
        }

        // 扣除余额
        BigDecimal balanceBefore = wallet.getBalance();
        BigDecimal balanceAfter = balanceBefore.subtract(order.getPrice());
        wallet.setBalance(balanceAfter);
        walletMapper.updateById(wallet);

        // 记录交易流水
        String transactionNo = "TXN" + System.currentTimeMillis() + userId;
        WalletTransaction transaction = WalletTransaction.builder()
                .userId(userId)
                .transactionNo(transactionNo)
                .type(2) // 消费
                .amount(order.getPrice())
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .businessType("VIP_PURCHASE")
                .businessId(order.getId())
                .remark("购买VIP会员：" + order.getPackageName())
                .build();
        transactionMapper.insert(transaction);

        // 更新订单状态
        order.setStatus(1); // 已支付
        order.setPayTime(LocalDateTime.now());
        vipOrderMapper.updateById(order);

        // 更新用户VIP状态
        User user = userMapper.selectById(userId);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime newExpireTime;

        if (user.getIsVip() == 1 && user.getVipExpireTime() != null && user.getVipExpireTime().isAfter(now)) {
            // 已是VIP且未过期，续期
            newExpireTime = user.getVipExpireTime().plusDays(order.getDuration());
        } else {
            // 新开通或已过期，从当前时间开始计算
            newExpireTime = now.plusDays(order.getDuration());
        }

        user.setIsVip(1);
        user.setVipExpireTime(newExpireTime);
        userMapper.updateById(user);

        log.info("VIP订单支付成功，userId: {}, orderNo: {}", userId, dto.getOrderNo());
        return Response.success("支付成功");
    }

    @Override
    public Response getOrderDetail(String orderNo) {
        log.info("获取订单详情，orderNo: {}", orderNo);

        // 获取当前登录用户
        if (!StpUtil.isLogin()) {
            throw new BizException("请先登录");
        }
        Long userId = StpUtil.getLoginIdAsLong();

        // 查询订单
        LambdaQueryWrapper<VipOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VipOrder::getOrderNo, orderNo);
        VipOrder order = vipOrderMapper.selectOne(queryWrapper);

        if (order == null) {
            throw new BizException("订单不存在");
        }

        if (!order.getUserId().equals(userId)) {
            throw new BizException("无权查看此订单");
        }

        VipOrderVO orderVO = convertToVO(order);
        return Response.success(orderVO);
    }

    @Override
    public Response getVipStatus() {
        log.info("获取VIP状态");

        // 获取当前登录用户
        if (!StpUtil.isLogin()) {
            throw new BizException("请先登录");
        }
        Long userId = StpUtil.getLoginIdAsLong();

        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }

        LocalDateTime now = LocalDateTime.now();
        boolean isVip = user.getIsVip() == 1 && user.getVipExpireTime() != null && user.getVipExpireTime().isAfter(now);

        VipStatusVO statusVO = VipStatusVO.builder()
                .isVip(isVip)
                .vipExpireTime(user.getVipExpireTime())
                .build();

        if (isVip) {
            long days = ChronoUnit.DAYS.between(now, user.getVipExpireTime());
            statusVO.setRemainingDays((int) days);
            statusVO.setStatusText("VIP会员");
        } else {
            statusVO.setRemainingDays(0);
            statusVO.setStatusText("非会员");
        }

        return Response.success(statusVO);
    }

    @Override
    public Response getVipOrders(Integer pageNo, Integer pageSize) {
        log.info("获取VIP订单列表，pageNo: {}, pageSize: {}", pageNo, pageSize);

        // 获取当前登录用户
        if (!StpUtil.isLogin()) {
            throw new BizException("请先登录");
        }
        Long userId = StpUtil.getLoginIdAsLong();

        // 分页查询订单
        Page<VipOrder> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<VipOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VipOrder::getUserId, userId);
        queryWrapper.eq(VipOrder::getStatus, 1); // 只查询已支付的订单
        queryWrapper.orderByDesc(VipOrder::getPayTime);

        Page<VipOrder> orderPage = vipOrderMapper.selectPage(page, queryWrapper);

        // 转换为VO
        List<VipOrderVO> orderVOList = orderPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResponse.success(orderVOList, pageNo, orderPage.getTotal(), pageSize);
    }

    /**
     * 获取套餐信息映射
     */
    private Map<Integer, VipPackageInfo> getPackageInfoMap() {
        Map<Integer, VipPackageInfo> map = new HashMap<>();
        map.put(1, new VipPackageInfo("月卡会员", 30, new BigDecimal("29.00")));
        map.put(2, new VipPackageInfo("季卡会员", 90, new BigDecimal("79.00")));
        map.put(3, new VipPackageInfo("年卡会员", 365, new BigDecimal("199.00")));
        return map;
    }

    /**
     * 转换为VO
     */
    private VipOrderVO convertToVO(VipOrder order) {
        Map<Integer, String> statusNameMap = new HashMap<>();
        statusNameMap.put(0, "待支付");
        statusNameMap.put(1, "已支付");
        statusNameMap.put(2, "已取消");
        statusNameMap.put(3, "已退款");

        Map<Integer, String> paymentMethodNameMap = new HashMap<>();
        paymentMethodNameMap.put(1, "余额支付");
        paymentMethodNameMap.put(2, "支付宝支付");

        return VipOrderVO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .userId(order.getUserId())
                .packageType(order.getPackageType())
                .packageName(order.getPackageName())
                .duration(order.getDuration())
                .price(order.getPrice())
                .paymentMethod(order.getPaymentMethod())
                .paymentMethodName(paymentMethodNameMap.getOrDefault(order.getPaymentMethod(), "未知"))
                .status(order.getStatus())
                .statusName(statusNameMap.getOrDefault(order.getStatus(), "未知"))
                .payTime(order.getPayTime())
                .createTime(order.getCreateTime())
                .build();
    }

    /**
     * 套餐信息内部类
     */
    private static class VipPackageInfo {
        private String name;
        private Integer duration;
        private BigDecimal price;

        public VipPackageInfo(String name, Integer duration, BigDecimal price) {
            this.name = name;
            this.duration = duration;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public Integer getDuration() {
            return duration;
        }

        public BigDecimal getPrice() {
            return price;
        }
    }
}

