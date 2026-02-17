package com.farmer.dto;

import java.util.List;

public class CartOrderRequest {

    private Long retailerId;
    private List<CartItemRequest> items;

    public Long getRetailerId() {
        return retailerId;
    }

    public void setRetailerId(Long retailerId) {
        this.retailerId = retailerId;
    }

    public List<CartItemRequest> getItems() {
        return items;
    }

    public void setItems(List<CartItemRequest> items) {
        this.items = items;
    }
}
