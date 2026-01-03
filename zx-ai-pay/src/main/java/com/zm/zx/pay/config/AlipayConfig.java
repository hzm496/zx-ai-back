package com.zm.zx.pay.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 支付宝配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "alipay")
public class AlipayConfig {
    
    /**
     * 应用ID（从支付宝开放平台获取）
     */
    private String appId;
    
    /**
     * 应用私钥（RSA2）
     */
    private String privateKey;
    
    /**
     * 支付宝公钥
     */
    private String publicKey;
    
    /**
     * 支付宝网关地址（沙箱环境）
     */
    private String gatewayUrl = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    
    /**
     * 异步通知地址
     */
    private String notifyUrl;
    
    /**
     * 同步返回地址
     */
    private String returnUrl;
    
    /**
     * 签名方式
     */
    private String signType = "RSA2";
    
    /**
     * 字符编码
     */
    private String charset = "UTF-8";
    
    /**
     * 返回格式
     */
    private String format = "json";
}

