package com.example.shop;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {

    private final ShopProperties properties;

    public InfoController(ShopProperties properties) {
        this.properties = properties;
    }

    @GetMapping("/api/info")
    public ShopProperties info() {
        return properties;
    }
}
