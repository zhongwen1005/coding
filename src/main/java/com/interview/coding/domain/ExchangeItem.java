package com.interview.coding.domain;

/**
 * @author Zhongwen Zhao
 * @version 1.0
 * @date 4/22/2020 5:21 PM
 */
import java.math.BigDecimal;
import java.util.Objects;


public class ExchangeItem {
    private BigDecimal price;
    private String orderId;
    /**
     * 0 - New
     * 1 - Cancel
     * 2 - Modify
     */
    private OpEnum op;
    /**
     * 0 - sell
     * 1 - buy
     */
    private TypeEnum type;
    private Integer quantity;

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        ExchangeItem that = (ExchangeItem) o;
        return price.equals(that.price) &&
                orderId.equals(that.orderId) &&
                op == that.op &&
                type == that.type &&
                quantity.equals(that.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(price, orderId, op, type, quantity);
    }

    public ExchangeItem(BigDecimal price, String orderId, OpEnum op, TypeEnum type, Integer quantity) {
        this.price = price;
        this.orderId = orderId;
        this.op = op;
        this.type = type;
        this.quantity = quantity;
    }

    public ExchangeItem(ExchangeItem old) {
        this.price = old.price;
        this.orderId = old.orderId;
        this.op = old.op;
        this.type = old.type;
        this.quantity = old.quantity;
    }

    @Override
    public String toString() {
        return String.format("ExchangeItem: %s %s %s %d, %s",
                op.getDesc(), orderId, type.getDesc(), quantity, price);
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public OpEnum getOp() {
        return op;
    }

    public void setOp(OpEnum op) {
        this.op = op;
    }

    public TypeEnum getType() {
        return type;
    }

    public void setType(TypeEnum type) {
        this.type = type;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
