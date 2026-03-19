package Data;

import java.io.FileWriter;
import java.io.IOException;

public class ActionLogWriter {
    public static void saveToFile(String path, String text) throws IOException {
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(text);
        }
    }
}