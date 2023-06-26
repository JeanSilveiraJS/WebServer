package webserver;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Log {
    private static final OutputStream log;

    static {
        try {
            log = new FileOutputStream("log.txt");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void write(String str){
        synchronized (log) {
            try {
                log.write(str.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}