package com.leonp967.sweexpress.desafioJava.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

public class FileReader {

    private static Logger LOGGER = LoggerFactory.getLogger(FileReader.class);

    public Observable<List<String>> readFileLines(Path path) {
        return Observable.create(subscriber -> {
            try {
                LOGGER.info("Reading file on path: {}", path);
                AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);
                ByteBuffer buffer = ByteBuffer.allocate(1024);

                Future<Integer> readOperation = fileChannel.read(buffer, 0);
                readOperation.get();

                String fileContent = new String(buffer.array(), StandardCharsets.UTF_8).trim();
                subscriber.onNext(Arrays.asList(fileContent.split("\\n")));
                subscriber.onCompleted();
                buffer.clear();
            } catch (Exception ex) {
                LOGGER.error("Error while reading file with path: {}", path, ex);
                subscriber.onNext(new ArrayList<>());
                subscriber.onCompleted();
            }
        });
    }
}
