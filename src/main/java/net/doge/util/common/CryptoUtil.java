package net.doge.util.common;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.digest.DigestUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * @Author Doge
 * @Description 加解密工具类
 * @Date 2020/12/15
 */
public class CryptoUtil {
    /**
     * 字符串转为 32 位 MD5
     *
     * @param s
     * @return
     */
    public static String md5(String s) {
        return DigestUtil.md5Hex(s);
    }

    /**
     * 计算文件 MD5 值
     *
     * @param file
     * @return
     */
    public static String md5(File file) {
        return DigestUtil.md5Hex(file);
    }

    /**
     * Base 64 解码为字符串
     *
     * @param base64
     * @return
     */
    public static String base64Decode(String base64) {
        return Base64.decodeStr(base64);
    }

    /**
     * Base 64 解码为 bytes
     *
     * @param base64
     * @return
     */
    public static byte[] base64DecodeToBytes(String base64) {
        return Base64.decode(base64);
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
     * AES 加密，返回 bytes
     *
     * @param data
     * @param mode
     * @param key
     * @param iv
     * @return
     */
    public static byte[] aesEncrypt(byte[] data, String mode, byte[] key, byte[] iv) {
        try {
            Cipher cipher = Cipher.getInstance("AES/" + mode + "/PKCS5Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            if (iv != null) cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));
            else cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return cipher.doFinal(data);
        } catch (Exception e) {
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

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];

        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }

            inflater.end();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }
}
