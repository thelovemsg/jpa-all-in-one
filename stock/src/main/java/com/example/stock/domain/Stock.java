package com.example.stock.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
public class Stock {

    @Id
    @GeneratedValue
    private Long id;
    private Long productId;
    private Long quantity;
    @Version
    private Long version;
    public Stock(){}

    public Stock(Long productId, Long quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void decrease(Long quantity){
        if(this.quantity - quantity < 0){
            throw new RuntimeException();
        }

        this.quantity = this.quantity - quantity;
    }
}
