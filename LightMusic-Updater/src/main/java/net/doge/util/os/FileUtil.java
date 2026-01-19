package net.doge.util.os;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtil {
    /**
     * 解压 zip
     *
     * @param zipFile
     * @param targetFolder
     * @throws IOException
     */
    public static void unzip(File zipFile, File targetFolder) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipFile.toPath()))) {
            ZipEntry entry = zipInputStream.getNextEntry();
            Path folderPath = targetFolder.toPath();
            while (entry != null) {
                Path filePath = folderPath.resolve(entry.getName());
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

    /**
     * 计算
     *
     * @param file
     * @return
     */
    public static String hashMD5(File file) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            try (FileInputStream fis = new FileInputStream(file);
                 DigestInputStream dis = new DigestInputStream(fis, md)) {

                byte[] buffer = new byte[65536];
                while (dis.read(buffer) != -1) {
                    // 读取文件内容并更新 MessageDigest 对象
                }
            }

            // 完成后获取计算得到的 MD5 值
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));

            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
