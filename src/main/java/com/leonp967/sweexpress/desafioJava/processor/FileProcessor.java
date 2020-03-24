package com.leonp967.sweexpress.desafioJava.processor;

import com.leonp967.sweexpress.desafioJava.bo.DataProcessingBO;
import com.leonp967.sweexpress.desafioJava.command.DataProcessingCommand;
import com.leonp967.sweexpress.desafioJava.command.ResultsProcessingCommand;
import com.leonp967.sweexpress.desafioJava.file.FileReader;
import com.leonp967.sweexpress.desafioJava.file.FileWriter;
import com.leonp967.sweexpress.desafioJava.model.Customer;
import com.leonp967.sweexpress.desafioJava.model.FileDataModel;
import com.leonp967.sweexpress.desafioJava.model.Sale;
import com.leonp967.sweexpress.desafioJava.model.Salesman;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import rx.Observable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileProcessor implements Runnable {

    private final File fileToProcess;
    private List<Salesman> salesmen;
    private List<Customer> customers;
    private List<Sale> sales;
    private final String OUT_DIRECTORY;
    private final String ATTRIBUTE_DELIMITER;
    private Logger LOGGER = LoggerFactory.getLogger(FileProcessor.class);

    @Autowired
    private Function<List<String>, DataProcessingCommand> dataCommandFactory;
    @Autowired
    private Function<DataProcessingBO, ResultsProcessingCommand> resultsCommandFactory;
    @Autowired
    private FileReader fileReader;
    @Autowired
    private FileWriter fileWriter;

    public FileProcessor(String filePath, String outDirectory, String attributeDelimiter){
        fileToProcess = new File(filePath);
        salesmen = new ArrayList<>();
        customers = new ArrayList<>();
        sales = new ArrayList<>();
        OUT_DIRECTORY = outDirectory;
        ATTRIBUTE_DELIMITER = attributeDelimiter;
    }

    @Override
    public void run() {
        String fileName = FilenameUtils.getBaseName(fileToProcess.getName());
        if(processFile())
            generateResults(fileName);
    }

    private void generateResults(String fileName) {
        LOGGER.info("Generating results for file {}", fileName);
        DataProcessingBO dataProcessingBO = DataProcessingBO.builder()
                .customers(customers)
                .sales(sales)
                .salesmen(salesmen)
                .build();

        ResultsProcessingCommand resultsProcessingCommand = resultsCommandFactory.apply(dataProcessingBO);
        resultsProcessingCommand.observe().subscribe(result ->
                writeReportToFile(fileName, result.getClientsAmount(), result.getSalesmenAmount(),
                        result.getMostExpensiveSale(), result.getWorstSalesman()));
    }

    private void writeReportToFile(String fileName, int amountClients, int amountSalesman, int idBiggestSale, String worstSalesman){
        String outputString = String.format("Amount of Clients: %d\nAmount of Salesmen: %d\nID of most expensive sale: %d\nWorst Salesman ever: %s",
                amountClients, amountSalesman, idBiggestSale, worstSalesman);

        Path path = Paths.get(OUT_DIRECTORY, fileName + ".done.dat");
        fileWriter.writeResults(path, outputString).subscribe();
    }

    private boolean processFile() {
        try {
            fileReader.readFileLines(fileToProcess.toPath()).subscribe(lines -> {
                List<Observable<FileDataModel>> commandObservables = lines.stream().map(line -> {
                    List<String> attributes = Arrays.asList(line.split(ATTRIBUTE_DELIMITER));
                    DataProcessingCommand dataProcessingCommand = dataCommandFactory.apply(attributes);
                    return dataProcessingCommand.observe();
                }).collect(Collectors.toList());

                Observable.zip(commandObservables, Arrays::asList)
                        .subscribe(models -> models.forEach(dataModel -> {
                            if(dataModel instanceof Salesman)
                                salesmen.add((Salesman) dataModel);
                            else if(dataModel instanceof Customer)
                                customers.add((Customer) dataModel);
                            else if(dataModel instanceof Sale)
                                sales.add((Sale) dataModel);
                        }));
            });
        } catch (Exception e) {
            LOGGER.error("Error processing file {}", fileToProcess.toPath(), e);
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
