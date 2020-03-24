package com.leonp967.sweexpress.desafioJava.config;

import com.leonp967.sweexpress.desafioJava.WatcherInitializer;
import com.leonp967.sweexpress.desafioJava.bo.DataProcessingBO;
import com.leonp967.sweexpress.desafioJava.command.DataProcessingCommand;
import com.leonp967.sweexpress.desafioJava.command.ResultsProcessingCommand;
import com.leonp967.sweexpress.desafioJava.factory.ProcessorFactory;
import com.leonp967.sweexpress.desafioJava.file.FileReader;
import com.leonp967.sweexpress.desafioJava.file.FileWriter;
import com.leonp967.sweexpress.desafioJava.processor.FileProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

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

    @Autowired
    private ApplicationContext applicationContext;

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
    public ExecutorService executorService(){
        return Executors.newCachedThreadPool();
    }

    @Bean
    public WatchService fileWatcher() throws IOException {
        return FileSystems.getDefault().newWatchService();
    }

    @Bean
    public WatcherInitializer watcherInitializer(){
        return new WatcherInitializer(THREAD_POOL_THRESHOLD);
    }

    @Bean
    public ProcessorFactory processorFactory() {
        return new ProcessorFactory();
    }

    @Bean
    public Function<DataProcessingBO, ResultsProcessingCommand> resultCommandFactory() {
        return this::resultProcessingCommand;
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ResultsProcessingCommand resultProcessingCommand(DataProcessingBO dataProcessingBO) {
        return new ResultsProcessingCommand(HYSTRIX_RESULTS_COMMAND_GROUP, dataProcessingBO);
    }

    @Bean
    public Function<List<String>, DataProcessingCommand> dataCommandFactory() {
        return this::dataProcessingCommand;
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public DataProcessingCommand dataProcessingCommand(List<String> attributes) {
        return new DataProcessingCommand(HYSTRIX_DATA_COMMAND_GROUP, attributes);
    }

    @Bean
    public Function<File, FileProcessor> fileProcessorFactory() {
        return this::fileProcessor;
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public FileProcessor fileProcessor(File fileName) {
        String filePath = IN_DIRECTORY_PATH + fileName;
        return new FileProcessor(filePath, OUT_DIRECTORY_PATH, ATTRIBUTE_DELIMITER);
    }

    @Bean
    public FileReader fileReader() {
        return new FileReader();
    }

    @Bean
    public FileWriter fileWriter() {
        return new FileWriter();
    }
}
