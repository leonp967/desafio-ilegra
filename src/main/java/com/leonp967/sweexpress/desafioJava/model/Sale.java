package com.leonp967.sweexpress.desafioJava.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class Sale implements FileDataModel {
    private int id;
    private String salesmanName;
    private List<SaleItem> itemsList;

    public Sale(int id, String salesmanName, List<SaleItem> itemsList) {
        this.id = id;
        this.salesmanName = salesmanName;
        this.itemsList = itemsList;
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

    public String getSalesmanName() {
        return salesmanName;
    }

    public void setSalesmanName(String salesmanName) {
        this.salesmanName = salesmanName;
    }

    public List<SaleItem> getItemsList() {
        return itemsList;
    }

    public void setItemsList(List<SaleItem> itemsList) {
        this.itemsList = itemsList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sale that = (Sale) o;

        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.itemsList, that.itemsList) &&
                Objects.equals(this.salesmanName, that.salesmanName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, itemsList, salesmanName);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
                .add("id = " + id)
                .add("itemsList = " + itemsList)
                .add("salesmanName = " + salesmanName)
                .toString();
    }

    public static final class Builder {
        private int id;
        private String salesmanName;
        private List<SaleItem> itemsList = new ArrayList<>();

        private Builder() {
        }

        public static Builder aSale() {
            return new Builder();
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder salesmanName(String salesmanName) {
            this.salesmanName = salesmanName;
            return this;
        }

        public Builder itemsList(List<SaleItem> itemsList) {
            this.itemsList = itemsList;
            return this;
        }

        public Sale build() {
            return new Sale(id, salesmanName, itemsList);
        }
    }
}
