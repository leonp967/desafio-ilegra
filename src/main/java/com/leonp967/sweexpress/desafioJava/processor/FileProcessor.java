package com.leonp967.sweexpress.desafioJava.processor;

import com.leonp967.sweexpress.desafioJava.command.DataProcessingCommand;
import com.leonp967.sweexpress.desafioJava.command.ResultsProcessingCommand;
import com.leonp967.sweexpress.desafioJava.model.*;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileProcessor implements Runnable {

    private final File fileToProcess;
    private List<Salesman> salesmanList;
    private List<Customer> customerList;
    private List<Sale> saleList;
    private final String OUT_DIRECTORY;
    private final String ATTRIBUTE_DELIMITER;
    private ApplicationContext applicationContext;
    private Logger logger = LoggerFactory.getLogger(FileProcessor.class);

    @Autowired
    public FileProcessor(String filePath, String outDirectory, String attributeDelimiter, ApplicationContext applicationContext){
        fileToProcess = new File(filePath);
        salesmanList = new ArrayList<>();
        customerList = new ArrayList<>();
        saleList = new ArrayList<>();
        OUT_DIRECTORY = outDirectory;
        ATTRIBUTE_DELIMITER = attributeDelimiter;
        this.applicationContext = applicationContext;
    }

    @Override
    public void run() {
        String fileName = FilenameUtils.getBaseName(fileToProcess.getName());
        if(processFile())
            generateResults(fileName);
    }

    private void generateResults(String fileName) {
        ResultsProcessingCommand resultsProcessingCommand = (ResultsProcessingCommand) applicationContext.getBean("resultsProcessingCommand", salesmanList, customerList, saleList);
        CompletableFuture<Result> futureResult = resultsProcessingCommand.execute();

        try {
            Result result = futureResult.get();
            writeReportToFile(fileName, result.getClientsAmount(), result.getSalesmenAmount(), result.getMostExpensiveSale(), result.getWorstSalesman());
        } catch (Exception e) {
            logger.error("Exception generating result of file %s", fileName);
            e.printStackTrace();
        }
    }

    private void writeReportToFile(String fileName, int amountClients, int amountSalesman, int idBiggestSale, String worstSalesman){
        String outputString = String.format("Amount of Clients: %d\nAmount of Salesmen: %d\nID of most expensive sale: %d\nWorst Salesman ever: %s", amountClients, amountSalesman, idBiggestSale, worstSalesman);
        File outputDirectory = new File(OUT_DIRECTORY);

        if(!outputDirectory.exists())
            outputDirectory.mkdirs();

        File fileOutput = new File(OUT_DIRECTORY + fileName + ".done.dat");
        try {
            fileOutput.createNewFile();
            Files.write(fileOutput.toPath(), outputString.getBytes());
        } catch (IOException e) {
            logger.error("Exception writing report of file %s", fileName);
            e.printStackTrace();
        }
    }

    private boolean processFile() {
        try {
            while(!fileToProcess.renameTo(fileToProcess)) {
                Thread.sleep(10);
            }

            Stream<String> lines = Files.lines(fileToProcess.toPath());
            List<CompletableFuture<FileDataModel>> commandFutures = lines.map(line -> {
                List<String> attributes = Arrays.asList(line.split(ATTRIBUTE_DELIMITER));
                DataProcessingCommand dataProcessingCommand = (DataProcessingCommand) applicationContext.getBean("dataProcessingCommand", attributes);
                return dataProcessingCommand.execute();
            }).collect(Collectors.toList());

            CompletableFuture<Void> allFutures = CompletableFuture.allOf(commandFutures.toArray(new CompletableFuture[0]));
            CompletableFuture<List<FileDataModel>> joinedFutures = allFutures.thenApply(future ->
                    commandFutures.stream()
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList()));

            List<FileDataModel> fileDataModels = joinedFutures.get();

            fileDataModels.forEach(dataModel -> {
                if(dataModel instanceof Salesman)
                    salesmanList.add((Salesman) dataModel);
                else if(dataModel instanceof Customer)
                    customerList.add((Customer) dataModel);
                else if(dataModel instanceof Sale)
                    saleList.add((Sale) dataModel);
            });
        } catch (Exception e) {
            logger.error("Exception processing file %s", fileToProcess.toPath());
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
