package net.doge;

import net.doge.constant.meta.SoftInfo;
import net.doge.constant.system.SimplePath;
import net.doge.util.system.FileUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class App {
    public static void main(String[] args) throws IOException {
        // 参数验证，确保主程序调用
        if (args.length == 0 || !"54ee8fdb2c9f213c4e4eea81268fc38b".equals(args[0])) return;
        // 解压更新包
        Path zipIn = Paths.get(SimplePath.TEMP_PATH + SoftInfo.PACKAGE_FILE_NAME);
        Path zipOut = Paths.get("../" + SoftInfo.OUTPUT_DIR);
        FileUtil.unzip(zipIn, zipOut);
        // 删除更新包
        zipIn.toFile().delete();
        // 启动程序
        Runtime.getRuntime().exec(SoftInfo.APP_FILE_NAME);
    }
}