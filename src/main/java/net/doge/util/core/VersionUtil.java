package net.doge.util.core;

/**
 * @Author Doge
 * @Description 版本工具类
 * @Date 2020/12/15
 */
public class VersionUtil {
    private static final String DELIMITER = "\\.";

    /**
     * 判断 v1 版本号是否大于 v2
     *
     * @param v1
     * @param v2
     * @return
     */
    public static boolean isGreaterVersion(String v1, String v2) {
        String[] sp1 = v1.split(DELIMITER), sp2 = v2.split(DELIMITER);
        int len1 = sp1.length, len2 = sp2.length;
        for (int i = 0, l = Math.max(len1, len2); i < l; i++) {
            int n1 = i >= len1 ? 0 : Integer.parseInt(sp1[i]), n2 = i >= len2 ? 0 : Integer.parseInt(sp2[i]);
            if (n1 == n2) continue;
            return n1 > n2;
        }
        return false;
    }

//    public static void main(String[] args) {
//        System.out.println(isGreaterVersion("1.18", "1.17.19"));
//    }
}
