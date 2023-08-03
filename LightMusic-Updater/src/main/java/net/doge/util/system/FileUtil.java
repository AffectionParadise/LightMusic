package net.doge.util.system;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtil {
    public static void unzip(Path zipFilePath, Path targetFolder) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipFilePath))) {
            ZipEntry entry = zipInputStream.getNextEntry();
            while (entry != null) {
                Path filePath = targetFolder.resolve(entry.getName());
                if (!entry.isDirectory()) {
                    try {
                        Files.copy(zipInputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        // 跳过该文件
//                        System.out.println("跳过文件的解压：" + e.getMessage());
                    }
                } else Files.createDirectories(filePath);
                zipInputStream.closeEntry();
                entry = zipInputStream.getNextEntry();
            }
        }
    }
}
