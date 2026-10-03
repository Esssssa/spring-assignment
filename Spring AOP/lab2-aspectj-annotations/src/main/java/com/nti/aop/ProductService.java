package com.nti.aop;

import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Cacheable
    public String findProduct(int id) {
        System.out.println("Loading product " + id + " from the slow source");
        return "Product-" + id;
    }

    @Timed
    public void rebuildIndex() throws InterruptedException {
        Thread.sleep(120);
    }
}
