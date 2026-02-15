package net.doge.util.core.json;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import net.doge.util.core.io.FileUtil;
import net.doge.util.core.log.LogUtil;

import java.io.File;

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
     * 读取 Json 文件
     *
     * @param source
     * @return
     */
    public static JSONObject read(String source) {
        return read(new File(source));
    }

    /**
     * 读取 Json 文件
     *
     * @param file
     * @return
     */
    public static JSONObject read(File file) {
        try {
            String jsonStr = FileUtil.readStr(file);
            return JSONObject.parseObject(jsonStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 读取 Json 文件，没有就返回 {}
     *
     * @param source
     * @return
     */
    public static JSONObject readOrCreate(String source) {
        return readOrCreate(new File(source));
    }

    /**
     * 读取 Json 文件，没有就返回 {}
     *
     * @param file
     * @return
     */
    public static JSONObject readOrCreate(File file) {
        JSONObject obj = read(file);
        return obj != null ? obj : new JSONObject();
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
