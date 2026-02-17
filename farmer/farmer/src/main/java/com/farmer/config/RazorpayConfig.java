package com.farmer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RazorpayConfig {

    @Value("${razorpay.key_id}")
    private String key;

    @Value("${razorpay.key_secret}")
    private String secret;

    public String getKey() {
        return key;
    }

    public String getSecret() {
        return secret;
    }
}


