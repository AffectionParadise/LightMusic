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
    private static final String EAPI_KEY = "e82ckenh8dichen8";

    public static String weApi(String data) {
        byte[] secretKeyBytes = ArrayUtil.randomBytes(16);
        for (int i = 0, s = secretKeyBytes.length; i < s; i++)
            secretKeyBytes[i] = (byte) BASE62.charAt(secretKeyBytes[i] % 62);
        String params = CryptoUtil.base64Encode(
                CryptoUtil.aesEncrypt(
                        CryptoUtil.base64Encode(
                                CryptoUtil.aesEncrypt(
                                        data.getBytes(StandardCharsets.UTF_8),
                                        "CBC",
                                        PRESET_KEY.getBytes(StandardCharsets.UTF_8),
                                        IV.getBytes(StandardCharsets.UTF_8)
                                )
                        ).getBytes(StandardCharsets.UTF_8),
                        "CBC",
                        secretKeyBytes,
                        IV.getBytes(StandardCharsets.UTF_8)
                )
        );
        ArrayUtil.reverse(secretKeyBytes);
        String encSecKey = CryptoUtil.bytesToHex(
                CryptoUtil.rsaEncrypt(secretKeyBytes, CryptoUtil.base64DecodeToBytes(PUBLIC_KEY))
        );
        return String.format("params=%s&encSecKey=%s", StringUtil.urlEncode(params), encSecKey);
    }

//    public static String linuxApi(Object object) throws Exception {
//        String text = object.toString();
//        String encrypted = CryptoUtil.bytesToHex(
//                aesEncrypt(text.getBytes(StandardCharsets.UTF_8), "ECB", LINUX_API_KEY.getBytes(StandardCharsets.UTF_8), null)
//        ).toUpperCase();
//        return "{\"eparams\":\"" + encrypted + "\"}";
//    }

    public static String eApi(String url, String text) {
        String message = "nobody" + url + "use" + text + "md5forencrypt";
        String digest = CryptoUtil.hashMD5(message);
        String data = url + "-36cd479b6b5-" + text + "-36cd479b6b5-" + digest;
        String params = CryptoUtil.bytesToHex(
                CryptoUtil.aesEncrypt(data.getBytes(StandardCharsets.UTF_8), "ECB", EAPI_KEY.getBytes(StandardCharsets.UTF_8), null)
        ).toUpperCase();
        return String.format("params=%s", params);
    }
}
