package com.leonp967.sweexpress.desafioJava;

import com.leonp967.sweexpress.desafioJava.config.AppConfig;
import com.leonp967.sweexpress.desafioJava.processor.FileProcessor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;

import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public class WatcherInitializer {

    private final Integer THREAD_POOL_THRESHOLD;

    @Autowired
    private Path inDirectoryPath;
    @Autowired
    private WatchService fileWatcher;
    @Autowired
    private ExecutorService executor;
    @Autowired
    private Function<File, FileProcessor> fileProcessorFactory;

    public WatcherInitializer(Integer threadPoolThreshold){
        THREAD_POOL_THRESHOLD = threadPoolThreshold;
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        WatcherInitializer watcherInitializer = applicationContext.getBean(WatcherInitializer.class);
        watcherInitializer.watchFiles();
        applicationContext.close();
    }

    @SuppressWarnings("unchecked")
    private <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

    private void watchFiles() {
        while(true){
            WatchKey newFileKey;
            try {
                newFileKey = fileWatcher.take();
            } catch (InterruptedException e) {
                continue;
            }

            for (WatchEvent<?> event: newFileKey.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                if (kind == OVERFLOW) {
                    continue;
                }

                WatchEvent<Path> pathEvent = cast(event);
                Path fileName = pathEvent.context();

                Path child = inDirectoryPath.resolve(fileName);
                if (!FilenameUtils.getExtension(child.toString()).equals("dat")) {
                    System.err.format("New file '%s' is not a valid .dat file.\n", fileName);
                    continue;
                }

                if(((ThreadPoolExecutor)executor).getMaximumPoolSize() - ((ThreadPoolExecutor)executor).getActiveCount() < THREAD_POOL_THRESHOLD){
                    int poolSize = ((ThreadPoolExecutor)executor).getPoolSize();
                    poolSize *= 2;
                    ((ThreadPoolExecutor)executor).setMaximumPoolSize(poolSize);
                    ((ThreadPoolExecutor)executor).setCorePoolSize(poolSize);
                }
                executor.execute(fileProcessorFactory.apply(fileName.toFile()));
                break;
            }

            boolean valid = newFileKey.reset();
            if (!valid) {
                break;
            }
        }
    }
}
