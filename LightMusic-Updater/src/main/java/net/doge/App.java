package net.doge;

import net.doge.constant.meta.SoftInfo;
import net.doge.constant.system.SimplePath;
import net.doge.util.system.FileUtil;

import java.io.File;
import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        // 参数验证，确保主程序调用
        if (args.length == 0) return;
        // 校验更新包
        String keyMD5 = args[0];
        File zipIn = new File(SimplePath.TEMP_PATH + SoftInfo.PACKAGE_FILE_NAME);
        if (!keyMD5.equalsIgnoreCase(FileUtil.hashMD5(zipIn))) return;
        // 解压更新包
        File zipOut = new File("../" + SoftInfo.OUTPUT_DIR);
        FileUtil.unzip(zipIn, zipOut);
        // 删除更新包
        zipIn.delete();
        // 启动程序
        Runtime.getRuntime().exec(SoftInfo.APP_FILE_NAME);
    }
}