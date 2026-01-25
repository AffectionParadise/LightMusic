package net.doge.util.lmdata;

import com.alibaba.fastjson2.JSONObject;
import net.doge.util.core.CryptoUtil;
import net.doge.util.os.FileUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * @Author Doge
 * @Description LM 数据工具类
 * @Date 2020/12/15
 */
public class LMDataUtil {
    /**
     * 从文件读取 LM 数据并转为 Json
     *
     * @param source
     * @return
     */
    public static JSONObject read(String source) {
        return read(new File(source));
    }

    /**
     * 从文件读取 LM 数据并转为 Json
     *
     * @param file
     * @return
     */
    public static JSONObject read(File file) {
        try {
            byte[] bytes = FileUtil.readBytes(file);
            byte[] decompressed = CryptoUtil.decompress(bytes);
            String jsonStr = new String(decompressed, StandardCharsets.UTF_8);
            JSONObject obj = JSONObject.parseObject(jsonStr);
            return obj == null ? new JSONObject() : obj;
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    /**
     * 将 Json 数据转为 LM 数据并存入文件
     *
     * @param obj
     * @param file
     */
    public static void toFile(JSONObject obj, File file) {
        byte[] compressed = CryptoUtil.compress(obj.toString().getBytes(StandardCharsets.UTF_8));
        FileUtil.writeBytes(compressed, file);
    }

    /**
     * 将 Json 数据转为 LM 数据并存入文件
     *
     * @param obj
     * @param dest
     */
    public static void toFile(JSONObject obj, String dest) {
        toFile(obj, new File(dest));
    }
}
