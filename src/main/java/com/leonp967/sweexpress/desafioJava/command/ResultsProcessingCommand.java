package com.leonp967.sweexpress.desafioJava.command;

import com.leonp967.sweexpress.desafioJava.model.Customer;
import com.leonp967.sweexpress.desafioJava.model.Result;
import com.leonp967.sweexpress.desafioJava.model.Sale;
import com.leonp967.sweexpress.desafioJava.model.Salesman;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingDouble;

public class ResultsProcessingCommand extends HystrixCommand<CompletableFuture<Result>> {

    private ExecutorService executorService;
    private ApplicationContext applicationContext;
    private List<Salesman> salesmen;
    private List<Customer> customers;
    private List<Sale> sales;
    private Logger logger = LoggerFactory.getLogger(ResultsProcessingCommand.class);

    public ResultsProcessingCommand(ApplicationContext applicationContext, String commandGroup, List<Salesman> salesmen, List<Customer> customers, List<Sale> sales, ExecutorService executorService){
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(commandGroup)));
        this.applicationContext = applicationContext;
        this.salesmen = salesmen;
        this.sales = sales;
        this.customers = customers;
        this.executorService = executorService;
    }

    @Override
    protected CompletableFuture<Result> run() {
        return CompletableFuture.supplyAsync(() -> {
            int amountClients = customers.size();
            int amountSalesman = salesmen.size();

            Map<Sale, Double> saleToSum = sales.stream().collect(groupingBy(sale -> sale,
                    summingDouble(sale -> sale.getItemsList().stream().mapToDouble(item -> item.getQuantity() * item.getPrice()).sum())));

            int idBiggestSale = saleToSum.entrySet().stream()
                    .max(comparingDouble(Map.Entry::getValue))
                    .map(entry -> entry.getKey().getId()).orElse(0);

            String worstSalesman = saleToSum.entrySet().stream()
                    .min(comparingDouble(Map.Entry::getValue))
                    .map(entry -> entry.getKey().getSalesmanName()).orElse("");

            return (Result) applicationContext.getBean("result", amountClients, amountSalesman, idBiggestSale, worstSalesman);
        }, executorService).exceptionally(throwable -> {
            try {
                return getFallback().get();
            } catch (Exception e) {
                return new Result(0, 0, 0, "");
            }
        });
    }

    @Override
    protected CompletableFuture<Result> getFallback() {
        logger.error("Error on ResultsProcessingCommand", getExecutionException());
        return CompletableFuture.completedFuture(new Result(0, 0, 0, ""));
    }
}
