package net.doge.util.common;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

import java.io.*;

/**
 * @Author Doge
 * @Description Json 解析工具类
 * @Date 2020/12/15
 */
public class JsonUtil {
    /**
     * 判断 Json 对象是否为 null 或 {}
     *
     * @param obj
     * @return
     */
    public static boolean isEmpty(JSONObject obj) {
        return obj == null || obj.isEmpty();
    }

    /**
     * 判断 Json 对象是否不为 null 和 {}
     *
     * @param obj
     * @return
     */
    public static boolean notEmpty(JSONObject obj) {
        return obj != null && !obj.isEmpty();
    }

    /**
     * 判断 Json 数组是否为 null 或 {}
     *
     * @param array
     * @return
     */
    public static boolean isEmpty(JSONArray array) {
        return array == null || array.isEmpty();
    }

    /**
     * 判断 Json 数组是否不为 null 或 {}
     *
     * @param array
     * @return
     */
    public static boolean notEmpty(JSONArray array) {
        return array != null && !array.isEmpty();
    }

    /**
     * 读取 Json 文件，返回 JSONObject
     *
     * @param source
     * @return
     * @throws IOException
     */
    public static JSONObject readJson(String source) {
        try (BufferedReader reader = new BufferedReader(new FileReader(source))) {
            StringBuilder sb = new StringBuilder();
            String s;
            while ((s = reader.readLine()) != null) sb.append(s);
            JSONObject obj = JSONObject.parseObject(sb.toString());
            return obj == null ? new JSONObject() : obj;
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    /**
     * 读取 Json 文件，返回 JSONObject
     *
     * @param input
     * @return
     * @throws IOException
     */
    public static JSONObject readJson(File input) {
        return readJson(input.getAbsolutePath());
    }

    /**
     * 保存 Json 文件
     *
     * @param object, dest
     * @return
     * @throws IOException
     */
    public static void saveJson(JSONObject object, String dest) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(dest));
        String jsonString = JSONObject.toJSONString(object, JSONWriter.Feature.PrettyFormat, JSONWriter.Feature.WriteMapNullValue);
        writer.write(jsonString, 0, jsonString.length());
        writer.close();
    }

    /**
     * 保存 Json 文件
     *
     * @param object, output
     * @return
     * @throws IOException
     */
    public static void saveJson(JSONObject object, File output) throws IOException {
        saveJson(object, output.getAbsolutePath());
    }
}
