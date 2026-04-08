package com.codecart.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "codecart.jwt")
public class JwtProperties {

    private String secret;

    private Long expireSeconds;
}
