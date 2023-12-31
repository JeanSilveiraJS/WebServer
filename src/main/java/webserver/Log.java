package webserver;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Log {
    private static OutputStream log;

    static {
        try {
            log = new FileOutputStream("log/log.txt", true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void write(String str){
        synchronized (log) {
            try {
                log.write(str.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}