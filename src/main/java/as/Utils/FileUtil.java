package as.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;
import java.nio.file.FileAlreadyExistsException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

public class FileUtil {
    public static String randomFileName() {
        String uuid = UUID.randomUUID().toString();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        return uuid + sdf.format(cal.getTime());
    }

    public static void createDirectory(String path){
        boolean created = new File(path).mkdirs();
    }

    public static void deleteDirectory(String path){
        try {
            FileUtils.deleteDirectory(new File(path));
        } catch (IOException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void createFile(String path, String content){
        File f = new File(path);
        try {
            if (f.createNewFile()) {
                try (FileWriter writer = new FileWriter(path)) {
                    writer.write(content);
                } catch (IOException ex) {
                    Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
