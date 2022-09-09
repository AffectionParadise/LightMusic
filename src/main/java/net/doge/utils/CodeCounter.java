package net.doge.utils;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author yzx
 * @Description 计算项目代码行数
 * @Date 2020/12/15
 */
public class CodeCounter {

    private static int countLine = 0;
    private static List<File> fileList = new ArrayList<>();
    private static File root = new File(System.getProperty("user.dir") + File.separator + "src");

    public static void main(String[] args) throws IOException {
        getFile(root);
        for (File file : fileList) {
            System.out.println(file.getPath());
            readLine(file);
        }
        System.out.println("readLineSum:" + countLine);
    }

    private static void readLine(File file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String str;
        do {
            str = bufferedReader.readLine();
            if (str != null && !str.equals("")) {
                countLine++;
            }
        } while (str != null);

//        System.out.println(countLine);
    }

    private static void getFile(File targetFile) {
        File[] files = targetFile.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory() || pathname.getPath().endsWith(".java") || pathname.getPath().endsWith(".fxml") || pathname.getPath().endsWith(".css")) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        for (File file : files) {
            if (file.isDirectory()) {
                getFile(file);
            } else {
                fileList.add(file);
            }
        }
    }
}
