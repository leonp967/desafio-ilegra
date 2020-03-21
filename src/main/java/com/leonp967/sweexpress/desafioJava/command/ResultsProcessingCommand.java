package com.leonp967.sweexpress.desafioJava.command;

import com.leonp967.sweexpress.desafioJava.bo.DataProcessingBO;
import com.leonp967.sweexpress.desafioJava.model.*;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.util.List;
import java.util.Map;

import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingDouble;

public class ResultsProcessingCommand extends HystrixObservableCommand<Result> {

    private List<Salesman> salesmen;
    private List<Customer> customers;
    private List<Sale> sales;
    private Logger LOGGER = LoggerFactory.getLogger(ResultsProcessingCommand.class);

    public ResultsProcessingCommand(String commandGroup, DataProcessingBO dataProcessingBO){
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(commandGroup)));
        this.salesmen = dataProcessingBO.getSalesmen();
        this.sales = dataProcessingBO.getSales();
        this.customers = dataProcessingBO.getCustomers();
    }

    private double calculateSaleTotal(List<SaleItem> itemsList) {
        return itemsList.stream().mapToDouble(item -> item.getQuantity() * item.getPrice()).sum();
    }

    @Override
    protected Observable<Result> construct() {
        return Observable.create(subscriber -> {
            try {
                LOGGER.info("Processing results...");
                int amountClients = customers.size();
                int amountSalesman = salesmen.size();

                Map<Sale, Double> saleToSum = sales.stream().collect(groupingBy(sale -> sale,
                        summingDouble(sale -> calculateSaleTotal(sale.getItemsList()))));

                int idBiggestSale = saleToSum.entrySet().stream()
                        .max(comparingDouble(Map.Entry::getValue))
                        .map(entry -> entry.getKey().getId()).orElse(0);

                Map<String, Double> salesmanToSum = sales.stream().collect(groupingBy(Sale::getSalesmanName,
                        summingDouble(sale -> calculateSaleTotal(sale.getItemsList()))));

                String worstSalesman = salesmanToSum.entrySet().stream()
                        .min(comparingDouble(Map.Entry::getValue))
                        .map(Map.Entry::getKey).orElse("");

                Result result = Result.builder()
                        .clientsAmount(amountClients)
                        .salesmenAmount(amountSalesman)
                        .mostExpensiveSale(idBiggestSale)
                        .worstSalesman(worstSalesman)
                        .build();

                subscriber.onNext(result);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    @Override
    protected Observable<Result> resumeWithFallback() {
        LOGGER.error("Error while processing the results: {}", getExecutionException().getStackTrace(), getExecutionException());
        return Observable.just(Result.builder().build());
    }
}
