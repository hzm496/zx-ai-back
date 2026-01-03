package com.zm.zx.pay.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.*;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.zm.zx.common.exception.BizException;
import com.zm.zx.common.response.Response;
import com.zm.zx.pay.config.AlipayConfig;
import com.zm.zx.pay.factory.AlipayClientFactory;
import com.zm.zx.pay.service.AlipayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝支付服务实现（纯SDK封装，不包含业务逻辑）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlipayPaymentServiceImpl implements AlipayService {
    
    private final AlipayClientFactory alipayClientFactory;
    
    /**
     * 统一的支付创建方法（内部使用）
     * @param orderNo 订单号
     * @param subject 订单标题
     * @param totalAmount 支付金额
     * @param body 订单描述
     * @param paymentType 支付类型：空字符串(VIP)、"recharge"(充值)、"course"(课程)
     * @return 支付表单HTML
     */
    private String createUnifiedPagePay(String orderNo, String subject, BigDecimal totalAmount, String body, String paymentType) {
        log.info("创建支付宝支付订单，orderNo: {}, amount: {}, type: {}", orderNo, totalAmount, paymentType);
        
        try {
            AlipayClient alipayClient = alipayClientFactory.getClient();
            AlipayConfig config = alipayClientFactory.getConfig();
            
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            
            // 根据支付类型设置不同的回调地址
            String notifyUrl = config.getNotifyUrl();
            String returnUrl = config.getReturnUrl();
            
            if (notifyUrl == null || notifyUrl.isEmpty()) {
                throw new BizException("支付宝notify-url未配置，请检查application.yaml");
            }
            if (returnUrl == null || returnUrl.isEmpty()) {
                throw new BizException("支付宝return-url未配置，请检查application.yaml");
            }
            
            // 根据支付类型调整回调URL
            if (paymentType != null && !paymentType.isEmpty()) {
                notifyUrl = notifyUrl.replace("/notify", "/" + paymentType + "/notify");
                returnUrl = returnUrl.replace("/return", "/" + paymentType + "/return");
            }
            
            request.setNotifyUrl(notifyUrl);
            request.setReturnUrl(returnUrl);
            
            // 设置请求参数
            AlipayTradePagePayModel model = new AlipayTradePagePayModel();
            model.setOutTradeNo(orderNo);
            model.setSubject(subject);
            model.setTotalAmount(totalAmount.toString());
            model.setBody(body);
            model.setProductCode("FAST_INSTANT_TRADE_PAY");
            
            request.setBizModel(model);
            
            // 调用SDK生成表单
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            
            if (response.isSuccess()) {
                log.info("支付订单创建成功，orderNo: {}, type: {}", orderNo, paymentType);
                return response.getBody();
            } else {
                log.error("支付订单创建失败，orderNo: {}, type: {}, msg: {}", orderNo, paymentType, response.getMsg());
                throw new BizException("创建支付订单失败：" + response.getMsg());
            }
        } catch (AlipayApiException e) {
            log.error("调用支付宝API异常，orderNo: {}, type: {}", orderNo, paymentType, e);
            throw new BizException("创建支付订单失败：" + e.getMessage());
        }
    }
    
    @Override
    public String createPagePay(String orderNo, String subject, BigDecimal totalAmount, String body) {
        // 调用统一方法，支付类型为空字符串（VIP订单）
        return createUnifiedPagePay(orderNo, subject, totalAmount, body, "");
    }
    
    @Override
    public Response handleNotify(HttpServletRequest request) {
        log.info("收到支付宝异步通知");
        
        try {
            // 获取支付宝POST过来反馈信息
            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            
            for (String name : requestParams.keySet()) {
                String[] values = requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                }
                params.put(name, valueStr);
            }
            
            // 验证签名
            boolean signVerified = verifySignature(params);
            
            if (!signVerified) {
                log.error("支付宝异步通知签名验证失败");
                return Response.fail("签名验证失败");
            }
            
            // 返回参数（业务层需要这些参数）
            return Response.success(params);
        } catch (Exception e) {
            log.error("处理支付宝异步通知异常", e);
            return Response.fail("处理失败：" + e.getMessage());
        }
    }
    
    @Override
    public String handleReturn(HttpServletRequest request) {
        log.info("收到支付宝同步返回");
        
        try {
            // 获取支付宝GET过来反馈信息
            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            
            for (String name : requestParams.keySet()) {
                String[] values = requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                }
                params.put(name, valueStr);
            }
            
            // 验证签名
            boolean signVerified = verifySignature(params);
            
            if (!signVerified) {
                log.error("支付宝同步返回签名验证失败");
                throw new BizException("签名验证失败");
            }
            
            // 获取订单号
            String outTradeNo = params.get("out_trade_no");
            log.info("支付宝同步返回验签成功，orderNo: {}", outTradeNo);
            
            return outTradeNo;
        } catch (Exception e) {
            log.error("处理支付宝同步返回异常", e);
            throw new BizException("处理失败：" + e.getMessage());
        }
    }
    
    @Override
    public Response queryOrderStatus(String orderNo) {
        log.info("查询订单支付状态，orderNo: {}", orderNo);
        
        try {
            AlipayClient alipayClient = alipayClientFactory.getClient();
            
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            AlipayTradeQueryModel model = new AlipayTradeQueryModel();
            model.setOutTradeNo(orderNo);
            request.setBizModel(model);
            
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            
            if (response.isSuccess()) {
                Map<String, Object> result = new HashMap<>();
                result.put("tradeStatus", response.getTradeStatus());
                result.put("tradeNo", response.getTradeNo());
                result.put("totalAmount", response.getTotalAmount());
                result.put("buyerPayAmount", response.getBuyerPayAmount());
                
                log.info("订单状态查询成功，orderNo: {}, tradeStatus: {}", orderNo, response.getTradeStatus());
                return Response.success(result);
            } else {
                log.error("订单状态查询失败，orderNo: {}, msg: {}", orderNo, response.getMsg());
                return Response.fail("查询失败：" + response.getMsg());
            }
        } catch (AlipayApiException e) {
            log.error("查询订单状态异常，orderNo: {}", orderNo, e);
            return Response.fail("查询失败：" + e.getMessage());
        }
    }
    
    @Override
    public Response queryAndUpdateOrder(String orderNo) {
        // 这个方法包含业务逻辑，应该在业务层实现
        // 这里只提供查询功能
        return queryOrderStatus(orderNo);
    }
    
    @Override
    public String createRechargePay(String orderNo, BigDecimal totalAmount) {
        // 调用统一方法，支付类型为"recharge"
        String subject = "余额充值";
        String body = "钱包余额充值 - " + totalAmount + "元";
        return createUnifiedPagePay(orderNo, subject, totalAmount, body, "recharge");
    }
    
    @Override
    public Response handleRechargeNotify(HttpServletRequest request) {
        // 直接使用通用的通知处理方法，签名验证逻辑完全相同
        return handleNotify(request);
    }
    
    @Override
    public Response queryAndUpdateRechargeOrder(String orderNo) {
        // 这个方法包含业务逻辑，应该在业务层实现
        return queryOrderStatus(orderNo);
    }
    
    @Override
    public Response transfer(String outBizNo, String payeeAccount, BigDecimal amount, String payeeRealName, String remark) {
        // 废弃的旧方法，直接调用新的转账接口
        log.warn("调用了废弃的transfer方法，已自动转为使用transferToUser");
        return transferToUser(outBizNo, payeeAccount, amount, payeeRealName, remark);
    }
    
    @Override
    public Response transferToUser(String outBizNo, String payeeAccount, BigDecimal amount, String payeeRealName, String remark) {
        log.info("发起支付宝单笔转账，outBizNo: {}, payeeAccount: {}, amount: {}, payeeRealName: {}", 
                outBizNo, payeeAccount, amount, payeeRealName);
        
        try {
            AlipayClient alipayClient = alipayClientFactory.getClient();
            
            // 使用支付宝单笔转账接口
            AlipayFundTransUniTransferRequest request = new AlipayFundTransUniTransferRequest();
            
            // 设置转账参数
            AlipayFundTransUniTransferModel model = new AlipayFundTransUniTransferModel();
            
            model.setOutBizNo(outBizNo);
            model.setTransAmount(amount.toString());
            model.setProductCode("TRANS_ACCOUNT_NO_PWD");
            model.setBizScene("DIRECT_TRANSFER");
            model.setOrderTitle(remark);
            
            // 收款方信息
            Participant payeeInfo = new Participant();
            payeeInfo.setIdentity(payeeAccount);
            payeeInfo.setIdentityType("ALIPAY_LOGON_ID");
            payeeInfo.setName(payeeRealName);
            model.setPayeeInfo(payeeInfo);
            
            model.setRemark(remark);
            
            request.setBizModel(model);
            
            // 调用SDK执行转账
            AlipayFundTransUniTransferResponse response = alipayClient.execute(request);
            
            if (response.isSuccess()) {
                log.info("支付宝转账成功，outBizNo: {}, orderId: {}, transDate: {}", 
                        outBizNo, response.getOrderId(), response.getTransDate());
                
                Map<String, Object> result = new HashMap<>();
                result.put("orderId", response.getOrderId());
                result.put("outBizNo", response.getOutBizNo());
                result.put("transDate", response.getTransDate());
                result.put("status", response.getStatus());
                
                return Response.success(result);
            } else {
                log.error("支付宝转账失败，outBizNo: {}, code: {}, msg: {}, subCode: {}, subMsg: {}", 
                        outBizNo, response.getCode(), response.getMsg(), 
                        response.getSubCode(), response.getSubMsg());
                throw new BizException("转账失败：" + response.getSubMsg());
            }
        } catch (AlipayApiException e) {
            log.error("调用支付宝转账API异常，outBizNo: {}", outBizNo, e);
            throw new BizException("转账失败：" + e.getMessage());
        }
    }
    
    @Override
    public String createCourseOrderPay(String orderNo, String subject, BigDecimal totalAmount, String body) {
        // 调用统一方法，支付类型为"course"
        return createUnifiedPagePay(orderNo, subject, totalAmount, body, "course");
    }
    
    @Override
    public Response handleCourseOrderNotify(HttpServletRequest request) {
        // 课程订单通知的签名验证逻辑与普通支付相同
        return handleNotify(request);
    }
    
    @Override
    public Response queryAndUpdateCourseOrder(String orderNo) {
        // 这个方法包含业务逻辑，应该在业务层实现
        return queryOrderStatus(orderNo);
    }
    
    @Override
    public String getAlipayPublicKey() {
        return alipayClientFactory.getConfig().getPublicKey();
    }
    
    /**
     * 验证签名
     * @param params 参数
     * @return 验证结果
     */
    private boolean verifySignature(Map<String, String> params) {
        try {
            AlipayConfig config = alipayClientFactory.getConfig();
            return AlipaySignature.rsaCheckV1(
                    params,
                    config.getPublicKey(),
                    config.getCharset(),
                    config.getSignType()
            );
        } catch (AlipayApiException e) {
            log.error("验证签名异常", e);
            return false;
        }
    }
}

