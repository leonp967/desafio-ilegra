import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.file.StandardWatchEventKinds.*;

public class Main {

    private static WatchService fileWatcher;
    private static Path inDirectoryPath;
    private static ExecutorService executor;

    /**
     * Registra o servico que fica monitorando a criacao de novos arquivos e inicializa o executor de threads
     * @param args
     */
    public static void main(String[] args){
        File rootFolder = new File(Constants.IN_DIRECTORY);
        if(!rootFolder.exists())
            rootFolder.mkdirs();

        try {
            fileWatcher = FileSystems.getDefault().newWatchService();
            inDirectoryPath = rootFolder.toPath();
            inDirectoryPath.register(fileWatcher, ENTRY_CREATE);
            executor = Executors.newFixedThreadPool(Constants.THREAD_POOL_SIZE);
            watchFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

    /**
     * Trata os eventos de criacao de arquivos e cria um objeto da classe DataProcessor para que eles sejam processados pelo executor das threads
     */
    private static void watchFiles() {
        while(true){
            WatchKey newFileKey;
            try {
                newFileKey = fileWatcher.take();
            } catch (InterruptedException x) {
                return;
            }

            for (WatchEvent<?> event: newFileKey.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                if (kind == OVERFLOW) {
                    continue;
                }

                WatchEvent<Path> ev = cast(event);
                Path fileName = ev.context();

                Path child = inDirectoryPath.resolve(fileName);
                if (!FilenameUtils.getExtension(child.toString()).equals("dat")) {
                    System.err.format("New file '%s' is not a valid .dat file.%n", fileName);
                    continue;
                }

                executor.execute(new DataProcessor(fileName.toFile()));
                break;
            }

            boolean valid = newFileKey.reset();
            if (!valid) {
                break;
            }
        }
    }
}
