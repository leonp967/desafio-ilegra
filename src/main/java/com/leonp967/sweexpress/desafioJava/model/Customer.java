package com.leonp967.sweexpress.desafioJava.model;

import java.util.Objects;
import java.util.StringJoiner;

public class Customer implements FileDataModel{
    private long cnpj;
    private String name;
    private String businessArea;

    public Customer(long cnpj, String name, String businessArea) {
        this.cnpj = cnpj;
        this.name = name;
        this.businessArea = businessArea;
    }

    public static Builder builder() {
        return new Builder();
    }

    public long getCnpj() {
        return cnpj;
    }

    public void setCnpj(long cnpj) {
        this.cnpj = cnpj;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBusinessArea() {
        return businessArea;
    }

    public void setBusinessArea(String businessArea) {
        this.businessArea = businessArea;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer that = (Customer) o;

        return Objects.equals(this.businessArea, that.businessArea) &&
                Objects.equals(this.cnpj, that.cnpj) &&
                Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(businessArea, cnpj, name);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
                .add("businessArea = " + businessArea)
                .add("cnpj = " + cnpj)
                .add("name = " + name)
                .toString();
    }

    public static final class Builder {
        private long cnpj;
        private String name;
        private String businessArea;

        private Builder() {
        }

        public static Builder aCustomer() {
            return new Builder();
        }

        public Builder cnpj(long cnpj) {
            this.cnpj = cnpj;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder businessArea(String businessArea) {
            this.businessArea = businessArea;
            return this;
        }

        public Customer build() {
            return new Customer(cnpj, name, businessArea);
        }
    }
}
