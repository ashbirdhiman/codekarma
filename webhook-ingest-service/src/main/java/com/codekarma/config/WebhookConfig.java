package com.codekarma.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "webhook")
public class WebhookConfig {
    /** GitHub webhook secret (X-Hub-Signature-256) */
    private String secret;


    public String getSecret() {
        return secret;
    }


    public void setSecret(String secret) {
        this.secret = secret;
    }
}