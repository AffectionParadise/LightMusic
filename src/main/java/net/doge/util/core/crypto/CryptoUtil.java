package net.doge.util.core.crypto;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.digest.DigestUtil;
import net.doge.util.core.log.LogUtil;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * @author Doge
 * @description 加解密工具类
 * @date 2020/12/15
 */
public class CryptoUtil {
    /**
     * 计算文件 32 位 MD5 值(16 进制字符串)
     *
     * @param s
     * @return
     */
    public static String md5Hex(String s) {
        return DigestUtil.md5Hex(s);
    }

    /**
     * 计算文件 32 位 MD5 值(16 进制字符串)
     *
     * @param file
     * @return
     */
    public static String md5Hex(File file) {
        return DigestUtil.md5Hex(file);
    }

    /**
     * 计算 HMAC-SHA256，返回 16 进制字符串
     *
     * @param data
     * @param key
     * @return
     */
    public static String hmacSha256Hex(String data, String key) {
        byte[] res = hmacSha256(data.getBytes(StandardCharsets.UTF_8), key.getBytes(StandardCharsets.UTF_8));
        return res == null ? null : bytesToHex(res);
    }

    /**
     * 计算 HMAC-SHA256，返回 bytes
     *
     * @param data
     * @param key
     * @return
     */
    public static byte[] hmacSha256(byte[] data, byte[] key) {
        try {
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key, "HmacSHA256");
            hmacSha256.init(secretKey);
            return hmacSha256.doFinal(data);
        } catch (Exception e) {
            LogUtil.error(e);
            return null;
        }
    }

    /**
     * HMAC-SHA256 加密并转换为 Base64 URL 编码（不带填充 '='）
     */
    public static String hmacSha256Base64Url(String data, String key) {
        byte[] hmac = hmacSha256(data.getBytes(StandardCharsets.UTF_8), key.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeUrlSafe(hmac);
    }

    /**
     * Base 64 编码 bytes
     *
     * @param data
     * @return
     */
    public static String base64Encode(byte[] data) {
        return Base64.encode(data);
    }

    /**
     * Base 64 编码字符串
     *
     * @param s
     * @return
     */
    public static String base64Encode(String s) {
        return Base64.encode(s);
    }

    /**
     * Base 64 解码为字符串
     *
     * @param base64
     * @return
     */
    public static String base64DecodeStr(String base64) {
        return Base64.decodeStr(base64);
    }

    /**
     * Base 64 解码为 bytes
     *
     * @param base64
     * @return
     */
    public static byte[] base64Decode(String base64) {
        return Base64.decode(base64);
    }

    /**
     * AES ECB 加密，返回 bytes
     *
     * @param data
     * @param key
     * @return
     */
    public static byte[] aesEcbEncrypt(byte[] data, byte[] key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return cipher.doFinal(data);
        } catch (Exception e) {
            LogUtil.error(e);
            return null;
        }
    }

    /**
     * AES CBC 加密，返回 bytes
     *
     * @param data
     * @param key
     * @param iv
     * @return
     */
    public static byte[] aesCbcEncrypt(byte[] data, byte[] key, byte[] iv) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));
            return cipher.doFinal(data);
        } catch (Exception e) {
            LogUtil.error(e);
            return null;
        }
    }

    /**
     * AES GCM 加密，返回 bytes
     *
     * @param data
     * @param key
     * @param iv
     * @return
     */
    public static byte[] aesGcmEncrypt(byte[] data, byte[] key, int tLen, byte[] iv) {
        return aesGcm(data, key, tLen, iv, Cipher.ENCRYPT_MODE);
    }

    /**
     * AES GCM 解密，返回 bytes
     *
     * @param data
     * @param key
     * @param iv
     * @return
     */
    public static byte[] aesGcmDecrypt(byte[] data, byte[] key, int tLen, byte[] iv) {
        return aesGcm(data, key, tLen, iv, Cipher.DECRYPT_MODE);
    }

    // AES GCM 加解密
    public static byte[] aesGcm(byte[] data, byte[] key, int tLen, byte[] iv, int mode) {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            cipher.init(mode, secretKeySpec, new GCMParameterSpec(tLen, iv));
            return cipher.doFinal(data);
        } catch (Exception e) {
            LogUtil.error(e);
            return null;
        }
    }

    /**
     * RSA 加密，返回 bytes
     *
     * @param data
     * @param key
     * @return
     */
    public static byte[] rsaEncrypt(byte[] data, byte[] key) {
        try {
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(key));
            Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            LogUtil.error(e);
            return null;
        }
    }

    /**
     * bytes 转 16 进制串
     *
     * @param data
     * @return
     */
    public static String bytesToHex(byte[] data) {
        return HexUtil.encodeHexStr(data);
    }

    /**
     * 16 进制串转 bytes
     *
     * @param hex
     * @return
     */
    public static byte[] hexToBytes(String hex) {
        return HexUtil.decodeHex(hex);
    }

    /**
     * zlib 压缩 bytes
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] compress(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        // 压缩数据
        deflater.finish();
        byte[] compressedBytes = new byte[data.length];
        int compressedLength = deflater.deflate(compressedBytes);
        // 拷贝有效数据
        return Arrays.copyOf(compressedBytes, compressedLength);
    }

    /**
     * zlib 解压缩 bytes
     *
     * @param data
     * @return
     */
    public static byte[] decompress(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        byte[] buffer = new byte[1024];
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            inflater.end();
            return outputStream.toByteArray();
        } catch (Exception e) {
            LogUtil.error(e);
            return null;
        }
    }
}
