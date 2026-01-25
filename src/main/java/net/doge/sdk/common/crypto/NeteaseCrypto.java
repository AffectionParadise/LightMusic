package net.doge.sdk.common.crypto;

import net.doge.util.collection.ArrayUtil;
import net.doge.util.core.CryptoUtil;
import net.doge.util.core.UrlUtil;

import java.nio.charset.StandardCharsets;

public class NeteaseCrypto {
    private static NeteaseCrypto instance;

    private NeteaseCrypto() {
    }

    public static NeteaseCrypto getInstance() {
        if (instance == null) instance = new NeteaseCrypto();
        return instance;
    }

    private final String BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final String IV = "0102030405060708";
    private final String PRESET_KEY = "0CoJUm6Qyw8W8jud";
    //    private final String LINUX_API_KEY = "rFgB&h#%2?^eDg:Q";
    private final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDgtQn2JZ34ZC28NWYpAUd98iZ37BUrX/aKzmFbt7clFSs6sXqHauqKWqdtLkF2KexO40H1YTX8z2lSgBBOAxLsvaklV8k4cBFK9snQXE9/DDaFt6Rr7iVZMldczhC0JNgTz+SHXT6CBHuX3e9SdB1Ua44oncaTWz7OBGLbCiK45wIDAQAB";
    private final String EAPI_KEY = "e82ckenh8dichen8";

    public String weapi(String data) {
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
        return "params=" + UrlUtil.encodeAll(params) + "&encSecKey=" + encSecKey;
    }

//    public String linuxApi(Object object) throws Exception {
//        String text = object.toString();
//        String encrypted = CryptoUtil.bytesToHex(
//                aesEncrypt(text.getBytes(StandardCharsets.UTF_8), "ECB", LINUX_API_KEY.getBytes(StandardCharsets.UTF_8), null)
//        ).toUpperCase();
//        return "{\"eparams\":\"" + encrypted + "\"}";
//    }

    public String eapi(String path, String data) {
        String message = "nobody" + path + "use" + data + "md5forencrypt";
        String digest = CryptoUtil.md5(message);
        String dat = path + "-36cd479b6b5-" + data + "-36cd479b6b5-" + digest;
        byte[] aesBytes = CryptoUtil.aesEncrypt(dat.getBytes(StandardCharsets.UTF_8), "ECB", EAPI_KEY.getBytes(StandardCharsets.UTF_8), null);
        String params = CryptoUtil.bytesToHex(aesBytes).toUpperCase();
        return "params=" + params;
    }
}
