package com.zm.zx.pay.service;

import com.zm.zx.common.response.Response;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

/**
 * 支付宝支付服务接口
 */
public interface AlipayService {
    
    /**
     * 创建支付订单（电脑网站支付）
     * 
     * @param orderNo 订单号
     * @param subject 订单标题
     * @param totalAmount 支付金额
     * @param body 订单描述
     * @return 支付表单HTML
     */
    String createPagePay(String orderNo, String subject, BigDecimal totalAmount, String body);
    
    /**
     * 处理支付宝异步通知
     * 
     * @param request HttpServletRequest
     * @return 处理结果
     */
    Response handleNotify(HttpServletRequest request);
    
    /**
     * 处理支付宝同步返回
     * 
     * @param request HttpServletRequest
     * @return 订单号
     */
    String handleReturn(HttpServletRequest request);
    
    /**
     * 查询订单支付状态
     * 
     * @param orderNo 订单号
     * @return 支付状态
     */
    Response queryOrderStatus(String orderNo);
    
    /**
     * 查询订单支付状态并更新订单
     * 
     * @param orderNo 订单号
     * @return 处理结果
     */
    Response queryAndUpdateOrder(String orderNo);
    
    /**
     * 创建充值支付订单（电脑网站支付）
     * 
     * @param orderNo 订单号
     * @param totalAmount 支付金额
     * @return 支付表单HTML
     */
    String createRechargePay(String orderNo, BigDecimal totalAmount);
    
    /**
     * 处理充值订单支付宝异步通知
     * 
     * @param request HttpServletRequest
     * @return 处理结果
     */
    Response handleRechargeNotify(HttpServletRequest request);
    
    /**
     * 查询充值订单支付状态并更新
     * 
     * @param orderNo 订单号
     * @return 处理结果
     */
    Response queryAndUpdateRechargeOrder(String orderNo);
    
    /**
     * 创建课程订单支付（电脑网站支付）
     * 
     * @param orderNo 订单号
     * @param subject 订单标题
     * @param totalAmount 支付金额
     * @param body 订单描述
     * @return 支付表单HTML
     */
    String createCourseOrderPay(String orderNo, String subject, BigDecimal totalAmount, String body);
    
    /**
     * 处理课程订单支付宝异步通知
     * 
     * @param request HttpServletRequest
     * @return 处理结果
     */
    Response handleCourseOrderNotify(HttpServletRequest request);
    
    /**
     * 查询课程订单支付状态并更新
     * 
     * @param orderNo 订单号
     * @return 处理结果
     */
    Response queryAndUpdateCourseOrder(String orderNo);
    
    /**
     * 支付宝转账（提现）- 旧接口（使用支付页面模拟）
     * 
     * @param outBizNo 商户转账唯一订单号
     * @param payeeAccount 收款方账户（支付宝账号或支付宝用户ID）
     * @param amount 转账金额
     * @param payeeRealName 收款方真实姓名
     * @param remark 转账备注
     * @return 转账结果
     */
    Response transfer(String outBizNo, String payeeAccount, BigDecimal amount, String payeeRealName, String remark);
    
    /**
     * 支付宝单笔转账接口（新）- 直接转账到用户账号
     * 
     * @param outBizNo 商户转账唯一订单号
     * @param payeeAccount 收款方账户（支付宝账号或支付宝用户ID）
     * @param amount 转账金额
     * @param payeeRealName 收款方真实姓名
     * @param remark 转账备注
     * @return 转账结果
     */
    Response transferToUser(String outBizNo, String payeeAccount, BigDecimal amount, String payeeRealName, String remark);
    
    /**
     * 获取支付宝公钥
     * @return 支付宝公钥
     */
    String getAlipayPublicKey();
}

