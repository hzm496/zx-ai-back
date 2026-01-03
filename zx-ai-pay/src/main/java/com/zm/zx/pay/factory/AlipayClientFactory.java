package com.zm.zx.pay.factory;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.zm.zx.pay.config.AlipayConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 支付宝客户端工厂
 * 负责创建和管理 AlipayClient 实例
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AlipayClientFactory {
    
    private final AlipayConfig alipayConfig;
    private AlipayClient alipayClient;
    
    /**
     * 初始化支付宝客户端
     */
    @PostConstruct
    public void init() {
        log.info("初始化支付宝客户端，AppId: {}", alipayConfig.getAppId());
        this.alipayClient = new DefaultAlipayClient(
                alipayConfig.getGatewayUrl(),
                alipayConfig.getAppId(),
                alipayConfig.getPrivateKey(),
                alipayConfig.getFormat(),
                alipayConfig.getCharset(),
                alipayConfig.getPublicKey(),
                alipayConfig.getSignType()
        );
        log.info("支付宝客户端初始化成功");
    }
    
    /**
     * 获取支付宝客户端实例
     * @return AlipayClient
     */
    public AlipayClient getClient() {
        return alipayClient;
    }
    
    /**
     * 获取支付宝配置
     * @return AlipayConfig
     */
    public AlipayConfig getConfig() {
        return alipayConfig;
    }
}

