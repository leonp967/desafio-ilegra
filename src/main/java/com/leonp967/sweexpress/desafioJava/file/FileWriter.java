package com.leonp967.sweexpress.desafioJava.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.util.concurrent.Future;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

public class FileWriter {

    private static Logger LOGGER = LoggerFactory.getLogger(FileWriter.class);

    public Observable<Void> writeResults(Path path, String content) {
        return Observable.create(subscriber -> {
            try {
                LOGGER.info("Writing report to file {}", path);
                AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, WRITE, CREATE);
                ByteBuffer buffer = ByteBuffer.allocate(1024);

                buffer.put(content.getBytes());
                buffer.flip();

                Future<Integer> operation = fileChannel.write(buffer, 0);
                buffer.clear();
                operation.get();
                LOGGER.info("Report successfully generated on file {}", path);
            } catch (Exception ex) {
                LOGGER.error("Error while writing report to file: {}", path, ex);
            }
        });
    }
}
