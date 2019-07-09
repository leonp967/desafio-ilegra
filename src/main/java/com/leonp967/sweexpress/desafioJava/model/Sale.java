package com.leonp967.sweexpress.desafioJava.model;

import java.util.List;

public class Sale implements FileDataModel {
    private int id;
    private String salesmanName;
    private List<SaleItem> itemsList;

    public Sale(int id, String salesmanName, List<SaleItem> itemsList) {
        this.id = id;
        this.salesmanName = salesmanName;
        this.itemsList = itemsList;
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
}
