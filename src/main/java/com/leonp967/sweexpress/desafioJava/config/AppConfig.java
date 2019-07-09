package com.leonp967.sweexpress.desafioJava.config;

import com.leonp967.sweexpress.desafioJava.WatcherInitializer;
import com.leonp967.sweexpress.desafioJava.command.DataProcessingCommand;
import com.leonp967.sweexpress.desafioJava.command.ResultsProcessingCommand;
import com.leonp967.sweexpress.desafioJava.model.*;
import com.leonp967.sweexpress.desafioJava.processor.CustomerDataProcessor;
import com.leonp967.sweexpress.desafioJava.processor.FileProcessor;
import com.leonp967.sweexpress.desafioJava.processor.SalesDataProcessor;
import com.leonp967.sweexpress.desafioJava.processor.SalesmanDataProcessor;
import com.netflix.config.ConfigurationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

@Configuration
@PropertySource(value = "classpath:application.properties", encoding = "UTF-8")
public class AppConfig {

    @Value("${data.input.directory}")
    private String IN_DIRECTORY_PATH;

    @Value("${data.output.directory}")
    private String OUT_DIRECTORY_PATH;

    @Value("${thread.pool.size}")
    private Integer THREAD_POOL_SIZE;

    @Value("${thread.pool.threshold}")
    private Integer THREAD_POOL_THRESHOLD;

    @Value("${hystrix.data.command.group}")
    private String HYSTRIX_DATA_COMMAND_GROUP;

    @Value("${hystrix.results.command.group}")
    private String HYSTRIX_RESULTS_COMMAND_GROUP;

    @Value("${attribute.delimiter}")
    private String ATTRIBUTE_DELIMITER;

    @Value("${item.delimiter}")
    private String ITEM_DELIMITER;

    @Value("${item.attributes.delimiter}")
    private String ITEM_ATTRIBUTES_DELIMITER;

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() throws IOException {
        ConfigurationManager.loadPropertiesFromResources("application.properties");
    }

    @Bean
    public Path inDirectoryPath(WatchService fileWatcher) throws IOException {
        File rootFolder = new File(IN_DIRECTORY_PATH);
        if(!rootFolder.exists())
            rootFolder.mkdirs();
        Path inDirectoryPath = rootFolder.toPath();
        inDirectoryPath.register(fileWatcher, ENTRY_CREATE);
        return inDirectoryPath;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ExecutorService executorService(){
        return Executors.newCachedThreadPool();
    }

    @Bean
    public WatchService fileWatcher() throws IOException {
        return FileSystems.getDefault().newWatchService();
    }

    @Bean
    public WatcherInitializer watcherInitializer(WatchService fileWatcher, Path inDirectoryPath, ExecutorService executor){
        return new WatcherInitializer(fileWatcher, inDirectoryPath, executor, applicationContext, THREAD_POOL_THRESHOLD);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public FileProcessor fileProcessor(File fileName){
        String filePath = IN_DIRECTORY_PATH + fileName;
        return new FileProcessor(filePath, OUT_DIRECTORY_PATH, ATTRIBUTE_DELIMITER, applicationContext);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public SalesmanDataProcessor dataProcessor001(){
        return new SalesmanDataProcessor(applicationContext);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public CustomerDataProcessor dataProcessor002(){
        return new CustomerDataProcessor(applicationContext);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public SalesDataProcessor dataProcessor003(){
        return new SalesDataProcessor(applicationContext, ITEM_ATTRIBUTES_DELIMITER, ITEM_DELIMITER);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public DataProcessingCommand dataProcessingCommand(List<String> attributes){
        return new DataProcessingCommand(HYSTRIX_DATA_COMMAND_GROUP, applicationContext, attributes, executorService());
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ResultsProcessingCommand resultsProcessingCommand(List<Salesman> salesmen, List<Customer> customers, List<Sale> sales){
        return new ResultsProcessingCommand(applicationContext, HYSTRIX_RESULTS_COMMAND_GROUP, salesmen, customers, sales, executorService());
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Salesman salesman(long cpf, String name, double salary){
        return new Salesman(cpf, name, salary);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Sale sale(int id, String salesmanName, List<SaleItem> items){
        return new Sale(id, salesmanName, items);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public SaleItem saleItem(int id, int quantity, double price){
        return new SaleItem(id, quantity, price);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Customer customer(long cnpj, String name, String businessArea){
        return new Customer(cnpj, name, businessArea);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Result result(int clientsAmount, int salesmenAmount, int idBiggestSale, String worstSalesman){
        return new Result(clientsAmount, salesmenAmount, idBiggestSale, worstSalesman);
    }
}
