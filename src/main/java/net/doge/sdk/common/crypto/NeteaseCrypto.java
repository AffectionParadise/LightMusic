package net.doge.sdk.common.crypto;

import net.doge.util.collection.ArrayUtil;
import net.doge.util.common.CryptoUtil;
import net.doge.util.common.StringUtil;

import java.nio.charset.StandardCharsets;

public class NeteaseCrypto {
    private static final String BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String IV = "0102030405060708";
    private static final String PRESET_KEY = "0CoJUm6Qyw8W8jud";
    //    private static final String LINUX_API_KEY = "rFgB&h#%2?^eDg:Q";
    private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDgtQn2JZ34ZC28NWYpAUd98iZ37BUrX/aKzmFbt7clFSs6sXqHauqKWqdtLkF2KexO40H1YTX8z2lSgBBOAxLsvaklV8k4cBFK9snQXE9/DDaFt6Rr7iVZMldczhC0JNgTz+SHXT6CBHuX3e9SdB1Ua44oncaTWz7OBGLbCiK45wIDAQAB";
    private static final String E_API_KEY = "e82ckenh8dichen8";

    public static String weapi(String data) {
        byte[] secretKey = ArrayUtil.randomBytes(16);
        for (int i = 0, s = secretKey.length; i < s; i++)
            secretKey[i] = (byte) BASE62.charAt(secretKey[i] % 62);
        // params
        byte[] aesBytes = CryptoUtil.aesEncrypt(data.getBytes(StandardCharsets.UTF_8), "CBC",
                PRESET_KEY.getBytes(StandardCharsets.UTF_8), IV.getBytes(StandardCharsets.UTF_8));
        byte[] base64Bytes = CryptoUtil.base64Encode(aesBytes).getBytes(StandardCharsets.UTF_8);
        byte[] aesBytes2 = CryptoUtil.aesEncrypt(base64Bytes, "CBC", secretKey, IV.getBytes(StandardCharsets.UTF_8));
        String params = CryptoUtil.base64Encode(aesBytes2);
        // encSecKey
        ArrayUtil.reverse(secretKey);
        byte[] rsaBytes = CryptoUtil.rsaEncrypt(secretKey, CryptoUtil.base64DecodeToBytes(PUBLIC_KEY));
        String encSecKey = CryptoUtil.bytesToHex(rsaBytes);
        return "params=" + StringUtil.urlEncodeAll(params) + "&encSecKey=" + encSecKey;
    }

//    public static String linuxApi(Object object) throws Exception {
//        String text = object.toString();
//        String encrypted = CryptoUtil.bytesToHex(
//                aesEncrypt(text.getBytes(StandardCharsets.UTF_8), "ECB", LINUX_API_KEY.getBytes(StandardCharsets.UTF_8), null)
//        ).toUpperCase();
//        return "{\"eparams\":\"" + encrypted + "\"}";
//    }

    public static String eapi(String path, String data) {
        String message = "nobody" + path + "use" + data + "md5forencrypt";
        String digest = CryptoUtil.hashMD5(message);
        String dat = path + "-36cd479b6b5-" + data + "-36cd479b6b5-" + digest;
        byte[] aesBytes = CryptoUtil.aesEncrypt(dat.getBytes(StandardCharsets.UTF_8), "ECB", E_API_KEY.getBytes(StandardCharsets.UTF_8), null);
        String params = CryptoUtil.bytesToHex(aesBytes).toUpperCase();
        return "params=" + params;
    }
}
