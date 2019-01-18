import entitys.Customer;
import entitys.Sale;
import entitys.SaleItem;
import entitys.Salesman;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Classe responsavel por processar um arquivo de entrada e criar um arquivo de saida com um report
 */
public class DataProcessor implements Runnable {

    private final File fileToProcess;
    private List<Salesman> salesmanList;
    private List<Customer> customerList;

    public DataProcessor(File fileName){
        fileToProcess = new File(Constants.IN_DIRECTORY + fileName);
        salesmanList = new ArrayList<>();
        customerList = new ArrayList<>();
    }

    /**
     * Metodo chamado quando a thread comeca o processamento
     */
    @Override
    public void run() {
        String fileName = FilenameUtils.getBaseName(fileToProcess.getName());
        if(processFile())
            generateResults(fileName);
    }

    /**
     * Metodo responsavel por processar os dados em memoria e gerar os dados necessarios para o report
     * Recebe como parametro o nome do arquivo de entrada para este ser utilizado no nome do arquivo de saida
     * @param fileName
     */
    private void generateResults(String fileName) {
        int amountClients = customerList.size();
        int amountSalesman = salesmanList.size();

        double highestTotal = Double.MIN_VALUE;
        double lowestTotal = Double.MAX_VALUE;
        int idBiggestSale = 0;
        String worstSalesman = "";

        for(Salesman salesman : salesmanList){
            double salesmanTotal = 0;
            for(Sale sale : salesman.getSalesList()){
                double saleTotal = sale.getItemsList().stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
                salesmanTotal += saleTotal;
                if(saleTotal > highestTotal){
                    highestTotal = saleTotal;
                    idBiggestSale = sale.getId();
                }
            }
            if(salesmanTotal < lowestTotal){
                lowestTotal = salesmanTotal;
                worstSalesman = salesman.getName();
            }
        }

        writeReportToFile(fileName, amountClients, amountSalesman, idBiggestSale, worstSalesman);
    }

    /**
     * Metodo responsavel por gerar o report com os dados recebidos do metodo generateResults, e salvar o report em um arquivo
     * @param fileName
     * @param amountClients
     * @param amountSalesman
     * @param idBiggestSale
     * @param worstSalesman
     */
    private void writeReportToFile(String fileName, int amountClients, int amountSalesman, int idBiggestSale, String worstSalesman){
        String outputString = String.format("Amount of Clients: %d\nAmount of Salesman: %d\nID of most expensive sale: %d\nWorst Salesman ever: %s", amountClients, amountSalesman, idBiggestSale, worstSalesman);
        File outputDirectory = new File(Constants.OUT_DIRECTORY);

        if(!outputDirectory.exists())
            outputDirectory.mkdirs();

        File fileOutput = new File(Constants.OUT_DIRECTORY + fileName + ".done.dat");
        try {
            fileOutput.createNewFile();
            FileWriter fileWriter = new FileWriter(fileOutput);
            fileWriter.write(outputString);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo responsavel por processar o arquivo e criar as entidades relacionadas a cada conjunto de dados do arquivo
     */
    private boolean processFile() {
        try {
            // As vezes acontece uma FileNotFoundException dizendo que o arquivo esta sendo utilizado, mesmo que seja novo. Esse while serve para evitar que isso aconteca
            while(!fileToProcess.renameTo(fileToProcess)) {
                Thread.sleep(10);
            }

            FileReader fileReader = new FileReader(fileToProcess);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();

            while(line != null){
                String[] attributesArray = line.split("ç");
                List<String> attributesList = removeUnwantedSpaces(attributesArray);
                int currentIndex = 0;

                while(currentIndex < attributesList.size()){
                    String dataType = attributesList.get(currentIndex);
                    List<String> entityAttributeList = attributesList.subList(currentIndex+1, currentIndex+4);
                    if(dataType.equals("001")) {
                        Salesman salesman = new Salesman(entityAttributeList);
                        salesmanList.add(salesman);
                    }
                    else if(dataType.equals("002")){
                        Customer customer = new Customer(entityAttributeList);
                        customerList.add(customer);
                    }
                    else
                        processSales(entityAttributeList);

                    currentIndex += 4;
                }

                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            fileReader.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch(NumberFormatException numberException){
            System.err.format("Invalid file layout or data type on file '%s'.", fileToProcess.getName());
            return false;
        } catch(Exception ex){
            System.err.println("File '%s' is malformed.");
            return false;
        }
        return true;
    }

    /**
     * Metodo auxiliar responsavel por remover apenas os espacos indesejados de cada linha
     * Recebe como parametro um array com os dados de uma entidade, antes dos espacos terem sido retirados
     * @param attributesArray
     * @return
     */
    private List<String> removeUnwantedSpaces(String[] attributesArray) {
        List<String> attributesList = new ArrayList<>();
        for(String attribute : attributesArray) {
            int indexSpace = attribute.lastIndexOf(" ");
            if (indexSpace > 0){
                if((Character.isDigit(attribute.charAt(indexSpace - 1))) || (Character.isDigit(attribute.charAt(indexSpace + 1)))){
                    for(int i = 0; i < attribute.length(); i++){
                        if(attribute.charAt(i) == ' ' && Character.isDigit(attribute.charAt(i+1))){
                            attributesList.add(attribute.substring(0, i));
                            attributesList.add(attribute.substring(i+1));
                            break;
                        }
                    }
                } else
                    attributesList.add(attribute);
            } else
                attributesList.add(attribute);
        }
        return attributesList;
    }

    /**
     * Metodo responsavel por processar as partes do arquivo referentes as vendas dos vendedores
     * Toma como premissa que as vendas sempre serao descritas apos todos os vendedores terem sido descritos, e associa as vendas a seus respectivos vendedores
     * @param attributesList
     */
    private void processSales(List<String> attributesList) {
        String saleListString = attributesList.get(1);
        saleListString = saleListString.substring(1, saleListString.length()-1);
        String[] saleItemArray = saleListString.split(",");
        List<SaleItem> saleItemList = new ArrayList<>();

        for(String itemString : saleItemArray){
            String[] itemAttributes = itemString.split("-");
            SaleItem saleItem = new SaleItem(itemAttributes);
            saleItemList.add(saleItem);
        }

        int id = Integer.parseInt(attributesList.get(0));
        String salesmanName = attributesList.get(2);
        Sale sale = new Sale(id, salesmanName, saleItemList);
        Optional<Salesman> salesmanOptional = salesmanList.stream().filter(salesman -> salesman.getName().equals(salesmanName)).findFirst();
        salesmanOptional.ifPresent(salesman -> salesman.getSalesList().add(sale));
    }
}
