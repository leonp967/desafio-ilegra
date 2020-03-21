package com.leonp967.sweexpress.desafioJava.model;

import java.util.Objects;
import java.util.StringJoiner;

public class Salesman implements FileDataModel{
    private String name;
    private long cpf;
    private double salary;

    public Salesman(long cpf, String name, double salary) {
        this.name = name;
        this.cpf = cpf;
        this.salary = salary;
    }

    public static Builder builder() {
        return new Builder();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Salesman that = (Salesman) o;

        return Objects.equals(this.cpf, that.cpf) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.salary, that.salary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpf, name, salary);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
                .add("cpf = " + cpf)
                .add("name = " + name)
                .add("salary = " + salary)
                .toString();
    }

    public static final class Builder {
        private String name;
        private long cpf;
        private double salary;

        private Builder() {
        }

        public static Builder aSalesman() {
            return new Builder();
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder cpf(long cpf) {
            this.cpf = cpf;
            return this;
        }

        public Builder salary(double salary) {
            this.salary = salary;
            return this;
        }

        public Salesman build() {
            return new Salesman(cpf, name, salary);
        }
    }
}
