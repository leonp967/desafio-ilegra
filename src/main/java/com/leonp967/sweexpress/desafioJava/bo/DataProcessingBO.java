package com.leonp967.sweexpress.desafioJava.bo;

import com.leonp967.sweexpress.desafioJava.model.Customer;
import com.leonp967.sweexpress.desafioJava.model.Sale;
import com.leonp967.sweexpress.desafioJava.model.Salesman;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class DataProcessingBO {

    private List<Salesman> salesmen;
    private List<Customer> customers;
    private List<Sale> sales;

    public static Builder builder() {
        return new Builder();
    }

    public List<Salesman> getSalesmen() {
        return salesmen;
    }

    public void setSalesmen(List<Salesman> salesmen) {
        this.salesmen = salesmen;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public List<Sale> getSales() {
        return sales;
    }

    public void setSales(List<Sale> sales) {
        this.sales = sales;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataProcessingBO that = (DataProcessingBO) o;

        return Objects.equals(this.customers, that.customers) &&
                Objects.equals(this.sales, that.sales) &&
                Objects.equals(this.salesmen, that.salesmen);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customers, sales, salesmen);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
                .add("customerList = " + customers)
                .add("saleList = " + sales)
                .add("salesmanList = " + salesmen)
                .toString();
    }

    public static final class Builder {
        private List<Salesman> salesmen = new ArrayList<>();
        private List<Customer> customers = new ArrayList<>();
        private List<Sale> sales = new ArrayList<>();

        private Builder() {
        }

        public static Builder aDataProcessingBO() {
            return new Builder();
        }

        public Builder salesmen(List<Salesman> salesmen) {
            this.salesmen = salesmen;
            return this;
        }

        public Builder customers(List<Customer> customers) {
            this.customers = customers;
            return this;
        }

        public Builder sales(List<Sale> sales) {
            this.sales = sales;
            return this;
        }

        public DataProcessingBO build() {
            DataProcessingBO dataProcessingBO = new DataProcessingBO();
            dataProcessingBO.setSalesmen(salesmen);
            dataProcessingBO.setCustomers(customers);
            dataProcessingBO.setSales(sales);
            return dataProcessingBO;
        }
    }
}
