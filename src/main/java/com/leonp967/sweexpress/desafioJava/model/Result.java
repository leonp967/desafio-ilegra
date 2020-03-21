package com.leonp967.sweexpress.desafioJava.model;

import java.util.Objects;
import java.util.StringJoiner;

public class Result {

    private int clientsAmount;
    private int salesmenAmount;
    private int mostExpensiveSale;
    private String worstSalesman;

    public Result(int clientsAmount, int salesmenAmount, int mostExpensiveSale, String worstSalesman) {
        this.clientsAmount = clientsAmount;
        this.salesmenAmount = salesmenAmount;
        this.mostExpensiveSale = mostExpensiveSale;
        this.worstSalesman = worstSalesman;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getClientsAmount() {
        return clientsAmount;
    }

    public void setClientsAmount(int clientsAmount) {
        this.clientsAmount = clientsAmount;
    }

    public int getSalesmenAmount() {
        return salesmenAmount;
    }

    public void setSalesmenAmount(int salesmenAmount) {
        this.salesmenAmount = salesmenAmount;
    }

    public int getMostExpensiveSale() {
        return mostExpensiveSale;
    }

    public void setMostExpensiveSale(int mostExpensiveSale) {
        this.mostExpensiveSale = mostExpensiveSale;
    }

    public String getWorstSalesman() {
        return worstSalesman;
    }

    public void setWorstSalesman(String worstSalesman) {
        this.worstSalesman = worstSalesman;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Result that = (Result) o;

        return Objects.equals(this.clientsAmount, that.clientsAmount) &&
                Objects.equals(this.mostExpensiveSale, that.mostExpensiveSale) &&
                Objects.equals(this.salesmenAmount, that.salesmenAmount) &&
                Objects.equals(this.worstSalesman, that.worstSalesman);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientsAmount, mostExpensiveSale, salesmenAmount, worstSalesman);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
                .add("clientsAmount = " + clientsAmount)
                .add("mostExpensiveSale = " + mostExpensiveSale)
                .add("salesmenAmount = " + salesmenAmount)
                .add("worstSalesman = " + worstSalesman)
                .toString();
    }

    public static final class Builder {
        private int clientsAmount;
        private int salesmenAmount;
        private int mostExpensiveSale;
        private String worstSalesman;

        private Builder() {
        }

        public static Builder aResult() {
            return new Builder();
        }

        public Builder clientsAmount(int clientsAmount) {
            this.clientsAmount = clientsAmount;
            return this;
        }

        public Builder salesmenAmount(int salesmenAmount) {
            this.salesmenAmount = salesmenAmount;
            return this;
        }

        public Builder mostExpensiveSale(int mostExpensiveSale) {
            this.mostExpensiveSale = mostExpensiveSale;
            return this;
        }

        public Builder worstSalesman(String worstSalesman) {
            this.worstSalesman = worstSalesman;
            return this;
        }

        public Result build() {
            return new Result(clientsAmount, salesmenAmount, mostExpensiveSale, worstSalesman);
        }
    }
}
