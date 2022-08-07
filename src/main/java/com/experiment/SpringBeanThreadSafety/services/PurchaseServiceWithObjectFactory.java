package com.experiment.SpringBeanThreadSafety.services;

import com.experiment.SpringBeanThreadSafety.entities.PurchaseProductDto;
import com.experiment.SpringBeanThreadSafety.events.ProductPurchasedEvent;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Service
public class PurchaseServiceWithObjectFactory {
    private final ObjectProvider<ProductPurchasedEvent> productPurchasedEventObjectFactory;


    public PurchaseServiceWithObjectFactory(
            ObjectProvider<ProductPurchasedEvent> productPurchasedEventObjectFactory
    ) {
        this.productPurchasedEventObjectFactory = productPurchasedEventObjectFactory;
    }

    public int purchase(PurchaseProductDto dto) {
        System.out.println("Thread: " + Thread.currentThread().getName() + "product: " + dto.getProduct() + " purchased successfully");
        ProductPurchasedEvent productPurchasedEventInstance = productPurchasedEventObjectFactory.getObject();
        productPurchasedEventInstance.setProduct(dto.getProduct());
        productPurchasedEventInstance.setBuyer(dto.getBuyer());
        productPurchasedEventInstance.fire();
        return productPurchasedEventInstance.getEventId();
    }
}
