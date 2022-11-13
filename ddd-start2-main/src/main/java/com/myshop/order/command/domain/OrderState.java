package com.myshop.order.command.domain;

public enum OrderState {
    PAYMENT_WAITING{
        public boolean isShippingChangeable() {
            return true;
        }
    },
    PREPARING{
        public boolean isShippingChangeable(){
            return true;
        }
    },
    SHIPPED, DELIVERING, DELIVERY_COMPLETED, CANCELED;

    public boolean isShippingChangeable(){
        return false;
    }
}
