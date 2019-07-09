package com.leonp967.sweexpress.desafioJava.model;

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
}
