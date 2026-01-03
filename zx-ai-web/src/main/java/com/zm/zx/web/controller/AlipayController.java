package com.zm.zx.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zm.zx.common.annotation.ApiOperationLog;
import com.zm.zx.common.exception.BizException;
import com.zm.zx.common.model.withdraw.po.WithdrawOrder;
import com.zm.zx.common.response.Response;
import com.zm.zx.web.domain.po.RechargeOrder;
import com.zm.zx.web.domain.po.VipOrder;
import com.zm.zx.web.mapper.RechargeOrderMapper;
import com.zm.zx.web.mapper.VipOrderMapper;
import com.zm.zx.web.model.po.CourseOrder;
import com.zm.zx.web.service.CourseOrderService;
import com.zm.zx.pay.service.AlipayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * 支付宝支付 Controller
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/web/alipay")
public class AlipayController {
    
    private final AlipayService alipayService;
    private final VipOrderMapper vipOrderMapper;
    private final RechargeOrderMapper rechargeOrderMapper;
    private final com.zm.zx.web.mapper.WebWithdrawOrderMapper withdrawOrderMapper;
    private final com.zm.zx.web.mapper.CourseOrderMapper courseOrderMapper;
    private final CourseOrderService courseOrderService;
    private final com.zm.zx.web.service.RechargeService rechargeService;
    
    @Value("${frontend.url}")
    private String frontendUrl;
    
    // ==================== 公共辅助方法 ====================
    
    /**
     * 输出支付表单响应
     */
    private void writePaymentResponse(String content, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(content);
        response.getWriter().flush();
    }
    
    /**
     * 输出错误响应
     */
    private void writeErrorResponse(String errorMsg, HttpServletResponse response) throws IOException {
        String html = String.format(
            "<html><body><h3>支付订单创建失败</h3><p>%s</p></body></html>", 
            errorMsg
        );
        writePaymentResponse(html, response);
    }
    
    /**
     * 通用订单查询方法（泛型）
     */
    private <T> T queryOrderByNo(String orderNo, Class<T> clazz, 
                                  com.baomidou.mybatisplus.core.mapper.BaseMapper<T> mapper,
                                  java.util.function.Function<T, String> getOrderNoFunc) {
        LambdaQueryWrapper<T> queryWrapper = new LambdaQueryWrapper<>();
        // 使用反射或函数式接口无法直接实现，保持原有方式
        return null; // 此方法暂不使用，保持原有查询逻辑
    }
    
    // ==================== 接口方法 ====================
    
    /**
     * 创建支付订单（电脑网站支付）
     * 注意：使用GET方法，因为需要在浏览器新窗口中打开支付页面
     * 不需要 @SaCheckLogin，因为创建订单时已验证过身份，这里只需验证订单有效性
     */
    @ApiOperationLog(description = "创建支付宝支付订单")
    @GetMapping("/pay")
    public void createPay(@RequestParam("orderNo") String orderNo, HttpServletResponse response) throws IOException {
        log.info("创建支付宝支付订单，orderNo: {}", orderNo);
        
        // 查询订单
        LambdaQueryWrapper<VipOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VipOrder::getOrderNo, orderNo);
        VipOrder order = vipOrderMapper.selectOne(queryWrapper);
        
        if (order == null) {
            throw new BizException("订单不存在");
        }
        
        if (order.getStatus() != 0) {
            throw new BizException("订单状态不正确");
        }
        
        // 创建支付订单
        String subject = order.getPackageName();
        String body = "购买VIP会员 - " + order.getPackageName();
        
        try {
            String form = alipayService.createPagePay(orderNo, subject, order.getPrice(), body);
            log.info("支付宝支付表单生成成功，orderNo: {}", orderNo);
            log.debug("支付表单内容: {}", form);
            writePaymentResponse(form, response);
        } catch (Exception e) {
            log.error("创建支付宝支付订单失败，orderNo: {}", orderNo, e);
            writeErrorResponse(e.getMessage(), response);
        }
    }
    
    /**
     * 支付宝异步通知
     */
    @ApiOperationLog(description = "支付宝异步通知")
    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {
        log.info("收到支付宝异步通知");
        Response result = alipayService.handleNotify(request);
        // 返回success给支付宝，表示已接收通知
        return result.isSuccess() ? "success" : "fail";
    }
    
    /**
     * 支付宝同步返回
     * 注意：本地开发环境支付宝无法回调异步通知接口，所以在同步返回时也处理订单状态更新
     */
    @ApiOperationLog(description = "支付宝同步返回")
    @GetMapping("/return")
    public void returnUrl(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("收到支付宝同步返回");
        
        try {
            // 处理同步返回，验证签名并获取订单号
            String orderNo = alipayService.handleReturn(request);
            log.info("同步返回验签成功，orderNo: {}", orderNo);
            
            // 主动查询订单支付状态并更新（因为本地环境收不到异步通知）
            try {
                Response updateResult = alipayService.queryAndUpdateOrder(orderNo);
                if (updateResult.isSuccess()) {
                    log.info("订单状态更新成功，orderNo: {}", orderNo);
                } else {
                    log.warn("订单状态更新失败，orderNo: {}, msg: {}", orderNo, updateResult.getMessage());
                }
            } catch (Exception e) {
                log.error("更新订单状态异常，orderNo: {}", orderNo, e);
            }
            
            // 重定向到支付成功提示页面
            response.sendRedirect(frontendUrl + "/payment-success?type=vip&orderNo=" + orderNo);
        } catch (Exception e) {
            log.error("处理支付宝同步返回异常", e);
            response.sendRedirect(frontendUrl + "/payment-success?error=1");
        }
    }
    
    /**
     * 查询订单支付状态
     */
    @SaCheckLogin
    @ApiOperationLog(description = "查询订单支付状态")
    @GetMapping("/query/{orderNo}")
    public Response queryOrderStatus(@PathVariable String orderNo) {
        log.info("查询订单支付状态，orderNo: {}", orderNo);
        return alipayService.queryOrderStatus(orderNo);
    }
    
    /**
     * 创建充值支付订单（电脑网站支付）
     */
    @ApiOperationLog(description = "创建充值支付宝支付订单")
    @GetMapping("/recharge/pay")
    public void createRechargePay(@RequestParam("orderNo") String orderNo, HttpServletResponse response) throws IOException {
        log.info("创建充值支付宝支付订单，orderNo: {}", orderNo);
        
        // 查询充值订单
        LambdaQueryWrapper<RechargeOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RechargeOrder::getOrderNo, orderNo);
        RechargeOrder order = rechargeOrderMapper.selectOne(queryWrapper);
        
        if (order == null) {
            throw new BizException("充值订单不存在");
        }
        
        if (order.getStatus() != 0) {
            throw new BizException("充值订单状态不正确");
        }
        
        try {
            String form = alipayService.createRechargePay(orderNo, order.getAmount());
            log.info("充值支付宝支付表单生成成功，orderNo: {}", orderNo);
            log.debug("支付表单内容: {}", form);
            writePaymentResponse(form, response);
        } catch (Exception e) {
            log.error("创建充值支付宝支付订单失败，orderNo: {}", orderNo, e);
            writeErrorResponse(e.getMessage(), response);
        }
    }
    
    /**
     * 充值支付宝异步通知
     */
    @ApiOperationLog(description = "充值支付宝异步通知")
    @PostMapping("/recharge/notify")
    public String rechargeNotify(HttpServletRequest request) {
        log.info("收到充值支付宝异步通知");
        
        try {
            // 1. 验证签名
            Response result = alipayService.handleRechargeNotify(request);
            if (!result.isSuccess()) {
                log.error("充值异步通知验签失败");
                return "fail";
            }
            
            @SuppressWarnings("unchecked")
            java.util.Map<String, String> params = (java.util.Map<String, String>) result.getData();
            String orderNo = params.get("out_trade_no");
            String tradeNo = params.get("trade_no");
            String tradeStatus = params.get("trade_status");
            
            log.info("充值异步通知验签成功，orderNo: {}, tradeNo: {}, tradeStatus: {}", orderNo, tradeNo, tradeStatus);
            
            // 2. 如果支付成功，更新充值订单并增加钱包余额
            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                rechargeService.handleRechargeSuccess(orderNo, tradeNo);
                log.info("充值异步通知处理完成，orderNo: {}", orderNo);
            }
            
            return "success";
        } catch (Exception e) {
            log.error("处理充值异步通知异常", e);
            return "fail";
        }
    }
    
    /**
     * 充值支付宝同步返回
     */
    @ApiOperationLog(description = "充值支付宝同步返回")
    @GetMapping("/recharge/return")
    public void rechargeReturnUrl(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("收到充值支付宝同步返回");
        
        try {
            // 处理同步返回，验证签名并获取订单号
            String orderNo = alipayService.handleReturn(request);
            log.info("充值同步返回验签成功，orderNo: {}", orderNo);
            
            // 主动查询订单支付状态并更新（因为本地环境收不到异步通知）
            try {
                // 1. 查询支付宝订单状态
                Response queryResult = alipayService.queryAndUpdateRechargeOrder(orderNo);
                if (queryResult.isSuccess()) {
                    @SuppressWarnings("unchecked")
                    java.util.Map<String, Object> resultData = (java.util.Map<String, Object>) queryResult.getData();
                    String tradeStatus = (String) resultData.get("tradeStatus");
                    String tradeNo = (String) resultData.get("tradeNo");
                    
                    // 2. 如果支付成功，更新充值订单并增加钱包余额
                    if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                        rechargeService.handleRechargeSuccess(orderNo, tradeNo);
                        log.info("充值订单状态更新成功，orderNo: {}", orderNo);
                    } else {
                        log.warn("充值订单支付未成功，orderNo: {}, tradeStatus: {}", orderNo, tradeStatus);
                    }
                } else {
                    log.warn("充值订单状态更新失败，orderNo: {}, msg: {}", orderNo, queryResult.getMessage());
                }
            } catch (Exception e) {
                log.error("更新充值订单状态异常，orderNo: {}", orderNo, e);
            }
            
            // 重定向到支付成功提示页面
            response.sendRedirect(frontendUrl + "/payment-success?type=recharge&orderNo=" + orderNo);
        } catch (Exception e) {
            log.error("处理充值支付宝同步返回异常", e);
            response.sendRedirect(frontendUrl + "/payment-success?error=1");
        }
    }
    
    /**
     * 转账支付宝异步通知
     */
    @ApiOperationLog(description = "转账支付宝异步通知")
    @PostMapping("/transfer/notify")
    public String transferNotify(HttpServletRequest request) {
        log.info("收到转账支付宝异步通知");
        
        try {
            // 获取支付宝POST过来反馈信息
            java.util.Map<String, String> params = new java.util.HashMap<>();
            java.util.Map<String, String[]> requestParams = request.getParameterMap();
            
            for (String name : requestParams.keySet()) {
                String[] values = requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                }
                params.put(name, valueStr);
            }
            
            // 验证签名
            boolean signVerified = com.alipay.api.internal.util.AlipaySignature.rsaCheckV1(
                    params,
                    alipayService.getAlipayPublicKey(),
                    "UTF-8",
                    "RSA2"
            );
            
            if (!signVerified) {
                log.error("转账支付宝异步通知签名验证失败");
                return "fail";
            }
            
            // 获取订单号和交易状态
            String outTradeNo = params.get("out_trade_no");
            String tradeStatus = params.get("trade_status");
            
            log.info("转账支付宝异步通知，orderNo: {}, tradeStatus: {}", outTradeNo, tradeStatus);
            
            // 如果支付成功，更新提现订单状态
            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                // TODO: 调用提现服务更新订单状态
                log.info("转账成功，需要更新提现订单状态，orderNo: {}", outTradeNo);
            }
            
            return "success";
        } catch (Exception e) {
            log.error("处理转账支付宝异步通知异常", e);
            return "fail";
        }
    }
    
    /**
     * 转账支付宝同步返回
     */
    @ApiOperationLog(description = "转账支付宝同步返回")
    @GetMapping("/transfer/return")
    public void transferReturnUrl(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("收到转账支付宝同步返回");
        
        try {
            // 处理同步返回，验证签名
            String orderNo = alipayService.handleReturn(request);
            log.info("转账同步返回验签成功，orderNo: {}", orderNo);
            
            // 更新提现订单状态为已完成
            updateWithdrawOrderStatus(orderNo);
            
            // 重定向到支付成功提示页面
            response.sendRedirect(frontendUrl + "/payment-success?type=transfer&orderNo=" + orderNo);
        } catch (Exception e) {
            log.error("处理转账支付宝同步返回异常", e);
            response.sendRedirect(frontendUrl + "/payment-success?error=1");
        }
    }
    
    /**
     * 更新提现订单状态为已完成
     */
    private void updateWithdrawOrderStatus(String orderNo) {
        try {
            // 查询提现订单
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<WithdrawOrder> queryWrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
            queryWrapper.eq(WithdrawOrder::getWithdrawNo, orderNo);
            
         WithdrawOrder withdrawOrder = withdrawOrderMapper.selectOne(queryWrapper);
            
            if (withdrawOrder != null && withdrawOrder.getStatus() == 0) {
                // 更新为已完成
                withdrawOrder.setStatus(1);
                withdrawOrder.setProcessTime(java.time.LocalDateTime.now());
                withdrawOrderMapper.updateById(withdrawOrder);
                
                log.info("提现订单状态已更新为已完成，orderNo: {}，收款账号: {}", orderNo, withdrawOrder.getAccountInfo());
            } else {
                log.warn("提现订单不存在或状态不是待处理，orderNo: {}", orderNo);
            }
        } catch (Exception e) {
            log.error("更新提现订单状态失败，orderNo: {}", orderNo, e);
        }
    }
    
    /**
     * 创建课程订单支付（电脑网站支付）
     */
    @ApiOperationLog(description = "创建课程订单支付宝支付")
    @GetMapping("/course/pay")
    public void createCoursePay(@RequestParam("orderNo") String orderNo, HttpServletResponse response) throws IOException {
        log.info("创建课程订单支付宝支付，orderNo: {}", orderNo);
        
        // 查询课程订单
        LambdaQueryWrapper<CourseOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseOrder::getOrderNo, orderNo);
        CourseOrder order = courseOrderMapper.selectOne(queryWrapper);
        
        if (order == null) {
            throw new BizException("课程订单不存在");
        }
        
        if (order.getStatus() != 0) {
            throw new BizException("课程订单状态不正确");
        }
        
        try {
            String subject = "购买课程 - " + order.getCourseTitle();
            String body = "购买课程：" + order.getCourseTitle();
            String form = alipayService.createCourseOrderPay(orderNo, subject, order.getPayAmount(), body);
            log.info("课程订单支付宝支付表单生成成功，orderNo: {}", orderNo);
            log.debug("支付表单内容: {}", form);
            writePaymentResponse(form, response);
        } catch (Exception e) {
            log.error("创建课程订单支付宝支付失败，orderNo: {}", orderNo, e);
            writeErrorResponse(e.getMessage(), response);
        }
    }
    
    /**
     * 课程订单支付宝异步通知
     */
    @ApiOperationLog(description = "课程订单支付宝异步通知")
    @PostMapping("/course/notify")
    public String courseNotify(HttpServletRequest request) {
        log.info("收到课程订单支付宝异步通知");
        Response result = alipayService.handleCourseOrderNotify(request);
        // 返回success给支付宝，表示已接收通知
        return result.isSuccess() ? "success" : "fail";
    }
    
    /**
     * 课程订单支付宝同步返回
     */
    @ApiOperationLog(description = "课程订单支付宝同步返回")
    @GetMapping("/course/return")
    public void courseReturnUrl(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("收到课程订单支付宝同步返回");
        
        try {
            // 处理同步返回，验证签名并获取订单号
            String orderNo = alipayService.handleReturn(request);
            log.info("课程订单同步返回验签成功，orderNo: {}", orderNo);
            
            // 主动查询订单支付状态并更新（因为本地环境收不到异步通知）
            try {
                // 1. 查询支付宝订单状态
                Response queryResult = alipayService.queryOrderStatus(orderNo);
                if (queryResult.isSuccess()) {
                    @SuppressWarnings("unchecked")
                    java.util.Map<String, Object> resultData = (java.util.Map<String, Object>) queryResult.getData();
                    String tradeStatus = (String) resultData.get("tradeStatus");
                    String tradeNo = (String) resultData.get("tradeNo");
                    
                    // 2. 如果支付成功，更新课程订单状态
                    if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                        courseOrderService.alipayCallback(orderNo, tradeNo);
                        log.info("课程订单状态更新成功，orderNo: {}", orderNo);
                    } else {
                        log.warn("课程订单支付未成功，orderNo: {}, tradeStatus: {}", orderNo, tradeStatus);
                    }
                }
            } catch (Exception e) {
                log.error("更新课程订单状态异常，orderNo: {}", orderNo, e);
            }
            
            // 重定向到支付成功提示页面
            response.sendRedirect(frontendUrl + "/payment-success?type=course&orderNo=" + orderNo);
        } catch (Exception e) {
            log.error("处理课程订单支付宝同步返回异常", e);
            response.sendRedirect(frontendUrl + "/payment-success?error=1");
        }
    }
}

