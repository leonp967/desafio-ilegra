package entitys;

import java.util.ArrayList;
import java.util.List;

public class Salesman {
    private String name;
    private long cpf;
    private double salary;
    private List<Sale> salesList;

    public Salesman(String name, long cpf, double salary, List<Sale> salesList) {
        this.name = name;
        this.cpf = cpf;
        this.salary = salary;
        this.salesList = salesList;
    }

    public Salesman(List<String> attributes) throws NumberFormatException{
        cpf = Long.parseLong(attributes.get(0));
        name = attributes.get(1);
        salary = Double.parseDouble(attributes.get(2));
        salesList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCpf() {
        return cpf;
    }

    public void setCpf(long cpf) {
        this.cpf = cpf;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public List<Sale> getSalesList() {
        return salesList;
    }

    public void setSalesList(List<Sale> salesList) {
        this.salesList = salesList;
    }
}
