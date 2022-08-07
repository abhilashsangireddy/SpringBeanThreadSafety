package com.experiment.SpringBeanThreadSafety.controllers;

import com.experiment.SpringBeanThreadSafety.entities.PurchaseProductDto;
import com.experiment.SpringBeanThreadSafety.services.PurchaseService;
import com.experiment.SpringBeanThreadSafety.services.PurchaseServiceWithObjectFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/purchase")
public class PurchaseController {
    private final PurchaseService purchaseService;

    private final PurchaseServiceWithObjectFactory purchaseServiceWithObjectFactory;

    public PurchaseController(PurchaseService purchaseService, PurchaseServiceWithObjectFactory purchaseServiceWithObjectFactory) {
        this.purchaseService = purchaseService;
        this.purchaseServiceWithObjectFactory = purchaseServiceWithObjectFactory;
    }

    @PostMapping
    public int purchase(@RequestBody PurchaseProductDto dto) {
        return purchaseService.purchase(dto);
    }

    @PostMapping("/with-proxy")
    public int purchaseWithProxy(@RequestBody PurchaseProductDto dto) {
        return purchaseService.purchaseWithProxy(dto);
    }

    @PostMapping("/with-object-factory")
    public int purchaseWithObjectFactory(@RequestBody PurchaseProductDto dto) {
        return purchaseServiceWithObjectFactory.purchase(dto);
    }
}
