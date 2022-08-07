package com.experiment.SpringBeanThreadSafety.services;

import com.experiment.SpringBeanThreadSafety.entities.PurchaseProductDto;
import com.experiment.SpringBeanThreadSafety.events.ProductPurchasedEvent;
import com.experiment.SpringBeanThreadSafety.events.ProductPurchasedEventProxied;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PurchaseService {
    private ProductPurchasedEvent productPurchasedEvent;

    private ProductPurchasedEventProxied productPurchasedEventProxied;

    @Autowired
    public PurchaseService(ProductPurchasedEvent productPurchasedEvent, ProductPurchasedEventProxied productPurchasedEventProxied) {
        this.productPurchasedEvent = productPurchasedEvent;
        this.productPurchasedEventProxied = productPurchasedEventProxied;
    }

    public int purchase(PurchaseProductDto dto) {
        System.out.println("Thread: " + Thread.currentThread().getName() + "product: " + dto.getProduct() + " purchased successfully");
        productPurchasedEvent.setProduct(dto.getProduct());
        productPurchasedEvent.setBuyer(dto.getBuyer());
        return productPurchasedEvent.fire();
    }

    public int purchaseWithProxy(PurchaseProductDto dto) {
        System.out.println("Thread: " + Thread.currentThread().getName() + "product: " + dto.getProduct() + " purchased successfully");
        productPurchasedEventProxied.setProduct(dto.getProduct());
        productPurchasedEventProxied.setBuyer(dto.getBuyer());
        return productPurchasedEventProxied.fire();
    }
}