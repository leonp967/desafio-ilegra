package com.leonp967.sweexpress.desafioJava.model;

import java.util.Objects;
import java.util.StringJoiner;

public class SaleItem implements FileDataModel{
    private int id;
    private int quantity;
    private double price;

    public SaleItem(int id, int quantity, double price) {
        this.id = id;
        this.quantity = quantity;
        this.price = price;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SaleItem that = (SaleItem) o;

        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.price, that.price) &&
                Objects.equals(this.quantity, that.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, price, quantity);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
                .add("id = " + id)
                .add("price = " + price)
                .add("quantity = " + quantity)
                .toString();
    }

    public static final class Builder {
        private int id;
        private int quantity;
        private double price;

        private Builder() {
        }

        public static Builder aSaleItem() {
            return new Builder();
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder price(double price) {
            this.price = price;
            return this;
        }

        public SaleItem build() {
            return new SaleItem(id, quantity, price);
        }
    }
}
