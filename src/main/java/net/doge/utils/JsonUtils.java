package net.doge.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.io.*;

/**
 * @Author yzx
 * @Description Json 解析工具类
 * @Date 2020/12/15
 */
public class JsonUtils {
    /**
     * 读取 Json 文件，返回 JSONObject
     *
     * @param source
     * @return
     * @throws IOException
     */
    public static JSONObject readJson(String source) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(source));
            StringBuffer sb = new StringBuffer();
            String s;
            while ((s = reader.readLine()) != null) {
                sb.append(s);
            }
            reader.close();
            return JSONObject.fromObject(sb.toString());
        } catch (IOException e) {
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
        String jsonString = prettyJson(object.toString());
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

    /**
     * 美化 Json 字符串
     *
     * @param jsonStr
     * @return
     * @throws IOException
     */
    public static String prettyJson(String jsonStr) {
        if (StringUtils.isEmpty(jsonStr)) return jsonStr;
        com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(jsonStr);
        return JSON.toJSONString(jsonObject, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
    }

    /**
     * 判断文件是不是 Json
     *
     * @param file
     * @return
     */
    public static boolean isJson(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuffer sb = new StringBuffer();
        String s;
        while ((s = reader.readLine()) != null) {
            sb.append(s);
        }
        try {
            JSONObject.fromObject(sb.toString());
            return true;
        } catch (JSONException e) {
            return false;
        }
    }
}
