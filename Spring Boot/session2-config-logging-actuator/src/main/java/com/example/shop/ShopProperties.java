package com.example.shop;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "shop")
public record ShopProperties(
        String name,
        @DefaultValue("USD") String currency,
        @DefaultValue("20") int pageSize) {
}
