package entitys;

import java.util.List;

public class Customer {
    private long cnpj;
    private String name;
    private String businessArea;

    public Customer(long cnpj, String name, String businessArea) {
        this.cnpj = cnpj;
        this.name = name;
        this.businessArea = businessArea;
    }

    public Customer(List<String> attributes) throws NumberFormatException{
        cnpj = Long.parseLong(attributes.get(0));
        name = attributes.get(1);
        businessArea = attributes.get(2);
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
}
