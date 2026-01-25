package net.doge.util.core;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import net.doge.util.os.FileUtil;

import java.io.File;
import java.io.IOException;

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
     * 判断字符串是否为合法的 Json
     *
     * @param s
     * @return
     */
    public static boolean isValidObject(String s) {
        return JSON.isValidObject(s);
    }

    /**
     * 读取 Json 文件，返回 JSONObject
     *
     * @param source
     * @return
     * @throws IOException
     */
    public static JSONObject read(String source) {
        return read(new File(source));
    }

    /**
     * 读取 Json 文件，返回 JSONObject
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static JSONObject read(File file) {
        try {
            String jsonStr = FileUtil.readStr(file);
            JSONObject obj = JSONObject.parseObject(jsonStr);
            return obj == null ? new JSONObject() : obj;
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    /**
     * 保存 Json 文件
     *
     * @param obj, dest
     * @return
     */
    public static boolean toFile(JSONObject obj, String dest) {
        return toFile(obj, new File(dest));
    }

    /**
     * 保存 Json 文件
     *
     * @param obj, file
     * @return
     */
    public static boolean toFile(JSONObject obj, File file) {
        try {
            String jsonStr = obj.toString(JSONWriter.Feature.WriteMapNullValue);
            FileUtil.writeStr(jsonStr, file);
            return true;
        } catch (Exception e) {
            LogUtil.error(e);
            return false;
        }
    }
}
