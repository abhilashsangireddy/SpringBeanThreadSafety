package com.experiment.SpringBeanThreadSafety.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PurchaseProductDto {
    private Product product;
    private String buyer;
}
