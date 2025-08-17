//package net.doge.sdk.entity.music.info.trackhero.kw;
//
//import cn.hutool.http.HttpRequest;
//import com.alibaba.fastjson2.JSONObject;
//import net.doge.util.common.CryptoUtil;
//import net.doge.util.common.JsonUtil;
//
//import java.nio.charset.StandardCharsets;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Map;
//
//public class KwTrackHeroV2 {
//    private static KwTrackHeroV2 instance;
//
//    private KwTrackHeroV2() {
//        initMap();
//    }
//
//    public static KwTrackHeroV2 getInstance() {
//        if (instance == null) instance = new KwTrackHeroV2();
//        return instance;
//    }
//
//    private final String SECRET_KEY = "ylzsxkwm";
//    private final int DES_MODE_DECRYPT = 1;
//    private final int[] arrayE = {
//            31, 0, DES_MODE_DECRYPT, 2, 3, 4, -1, -1, 3, 4, 5, 6, 7, 8, -1, -1, 7, 8, 9, 10, 11, 12, -1, -1, 11, 12, 13, 14,
//            15, 16, -1, -1, 15, 16, 17, 18, 19, 20, -1, -1, 19, 20, 21, 22, 23, 24, -1, -1, 23, 24, 25, 26, 27, 28, -1, -1,
//            27, 28, 29, 30, 31, 30, -1, -1
//    };
//    private final int[] arrayIP = {
//            57, 49, 41, 33, 25, 17, 9, DES_MODE_DECRYPT, 59, 51, 43, 35, 27, 19, 11, 3, 61, 53, 45, 37, 29, 21, 13, 5, 63,
//            55, 47, 39, 31, 23, 15, 7, 56, 48, 40, 32, 24, 16, 8, 0, 58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36, 28, 20,
//            12, 4, 62, 54, 46, 38, 30, 22, 14, 6
//    };
//    private final int[] arrayIP_1 = {
//            39, 7, 47, 15, 55, 23, 63, 31, 38, 6, 46, 14, 54, 22, 62, 30, 37, 5, 45, 13, 53, 21, 61, 29, 36, 4, 44, 12, 52,
//            20, 60, 28, 35, 3, 43, 11, 51, 19, 59, 27, 34, 2, 42, 10, 50, 18, 58, 26, 33, DES_MODE_DECRYPT, 41, 9, 49, 17,
//            57, 25, 32, 0, 40, 8, 48, 16, 56, 24
//    };
//    private final int[] arrayLs = {1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1};
//    private final int[] arrayLsMask = {0, 0x100001, 0x300003};
//    private final long[] arrayMask = {
//            1L, 2L, 4L, 8L, 16L, 32L, 64L, 128L, 256L, 512L, 1024L, 2048L, 4096L, 8192L, 16384L, 32768L, 65536L, 131072L,
//            262144L, 524288L, 1048576L, 2097152L, 4194304L, 8388608L, 16777216L, 33554432L, 67108864L, 134217728L, 268435456L, 536870912L,
//            1073741824L, 2147483648L, 4294967296L, 8589934592L, 17179869184L, 34359738368L, 68719476736L, 137438953472L, 274877906944L,
//            549755813888L, 1099511627776L, 2199023255552L, 4398046511104L, 8796093022208L, 17592186044416L, 35184372088832L,
//            70368744177664L, 140737488355328L, 281474976710656L, 562949953421312L, 1125899906842624L, 2251799813685248L, 4503599627370496L,
//            9007199254740992L, 18014398509481984L, 36028797018963968L, 72057594037927936L, 144115188075855872L, 288230376151711744L,
//            576460752303423488L, 1152921504606846976L, 2305843009213693952L, 4611686018427387904L, -9223372036854775808L
//    };
//    private final int[] arrayP = {
//            15, 6, 19, 20, 28, 11, 27, 16,
//            0, 14, 22, 25, 4, 17, 30, 9,
//            1, 7, 23, 13, 31, 26, 2, 8,
//            18, 12, 29, 5, 21, 10, 3, 24
//    };
//    private final int[] arrayPC_1 = {
//            56, 48, 40, 32, 24, 16, 8, 0,
//            57, 49, 41, 33, 25, 17, 9, 1,
//            58, 50, 42, 34, 26, 18, 10, 2,
//            59, 51, 43, 35, 62, 54, 46, 38,
//            30, 22, 14, 6, 61, 53, 45, 37,
//            29, 21, 13, 5, 60, 52, 44, 36,
//            28, 20, 12, 4, 27, 19, 11, 3
//    };
//    private final int[] arrayPC_2 = {
//            13, 16, 10, 23, 0, 4, -1, -1,
//            2, 27, 14, 5, 20, 9, -1, -1,
//            22, 18, 11, 3, 25, 7, -1, -1,
//            15, 6, 26, 19, 12, 1, -1, -1,
//            40, 51, 30, 36, 46, 54, -1, -1,
//            29, 39, 50, 44, 32, 47, -1, -1,
//            43, 48, 38, 55, 33, 52, -1, -1,
//            45, 41, 49, 35, 28, 31, -1, -1
//    };
//    private final int[][] matrixNSBox = {{
//            14, 4, 3, 15, 2, 13, 5, 3,
//            13, 14, 6, 9, 11, 2, 0, 5,
//            4, 1, 10, 12, 15, 6, 9, 10,
//            1, 8, 12, 7, 8, 11, 7, 0,
//            0, 15, 10, 5, 14, 4, 9, 10,
//            7, 8, 12, 3, 13, 1, 3, 6,
//            15, 12, 6, 11, 2, 9, 5, 0,
//            4, 2, 11, 14, 1, 7, 8, 13,}, {
//            15, 0, 9, 5, 6, 10, 12, 9,
//            8, 7, 2, 12, 3, 13, 5, 2,
//            1, 14, 7, 8, 11, 4, 0, 3,
//            14, 11, 13, 6, 4, 1, 10, 15,
//            3, 13, 12, 11, 15, 3, 6, 0,
//            4, 10, 1, 7, 8, 4, 11, 14,
//            13, 8, 0, 6, 2, 15, 9, 5,
//            7, 1, 10, 12, 14, 2, 5, 9,}, {
//            10, 13, 1, 11, 6, 8, 11, 5,
//            9, 4, 12, 2, 15, 3, 2, 14,
//            0, 6, 13, 1, 3, 15, 4, 10,
//            14, 9, 7, 12, 5, 0, 8, 7,
//            13, 1, 2, 4, 3, 6, 12, 11,
//            0, 13, 5, 14, 6, 8, 15, 2,
//            7, 10, 8, 15, 4, 9, 11, 5,
//            9, 0, 14, 3, 10, 7, 1, 12,}, {
//            7, 10, 1, 15, 0, 12, 11, 5,
//            14, 9, 8, 3, 9, 7, 4, 8,
//            13, 6, 2, 1, 6, 11, 12, 2,
//            3, 0, 5, 14, 10, 13, 15, 4,
//            13, 3, 4, 9, 6, 10, 1, 12,
//            11, 0, 2, 5, 0, 13, 14, 2,
//            8, 15, 7, 4, 15, 1, 10, 7,
//            5, 6, 12, 11, 3, 8, 9, 14,}, {
//            2, 4, 8, 15, 7, 10, 13, 6,
//            4, 1, 3, 12, 11, 7, 14, 0,
//            12, 2, 5, 9, 10, 13, 0, 3,
//            1, 11, 15, 5, 6, 8, 9, 14,
//            14, 11, 5, 6, 4, 1, 3, 10,
//            2, 12, 15, 0, 13, 2, 8, 5,
//            11, 8, 0, 15, 7, 14, 9, 4,
//            12, 7, 10, 9, 1, 13, 6, 3,}, {
//            12, 9, 0, 7, 9, 2, 14, 1,
//            10, 15, 3, 4, 6, 12, 5, 11,
//            1, 14, 13, 0, 2, 8, 7, 13,
//            15, 5, 4, 10, 8, 3, 11, 6,
//            10, 4, 6, 11, 7, 9, 0, 6,
//            4, 2, 13, 1, 9, 15, 3, 8,
//            15, 3, 1, 14, 12, 5, 11, 0,
//            2, 12, 14, 7, 5, 10, 8, 13,}, {
//            4, 1, 3, 10, 15, 12, 5, 0,
//            2, 11, 9, 6, 8, 7, 6, 9,
//            11, 4, 12, 15, 0, 3, 10, 5,
//            14, 13, 7, 8, 13, 14, 1, 2,
//            13, 6, 14, 9, 4, 1, 2, 14,
//            11, 13, 5, 0, 1, 10, 8, 3,
//            0, 11, 3, 5, 9, 4, 15, 2,
//            7, 8, 12, 15, 10, 7, 6, 12,}, {
//            13, 7, 10, 0, 6, 9, 5, 15,
//            8, 4, 3, 10, 11, 14, 12, 5,
//            2, 11, 9, 6, 15, 12, 0, 3,
//            4, 1, 14, 13, 1, 2, 7, 8,
//            1, 2, 12, 15, 10, 4, 0, 3,
//            13, 14, 6, 9, 7, 8, 9, 6,
//            15, 1, 5, 12, 3, 10, 14, 5,
//            8, 7, 11, 0, 4, 13, 2, 11,}
//    };
//
//    private Map<String, String> eMap = new HashMap<>();
//    private Map<String, String> hMap = new HashMap<>();
//
//    private void initMap() {
//        eMap.put("128k", "mp3");
//        eMap.put("320k", "mp3");
//        eMap.put("flac", "flac");
//        eMap.put("flac24bit", "flac");
//
//        hMap.put("128k", "128k");
//        hMap.put("320k", "320k");
//        hMap.put("flac", "2000k");
//        hMap.put("flac24bit", "4000k");
//    }
//
//    /**
//     * 获取酷我音乐歌曲链接
//     *
//     * @param mid     歌曲 id
//     * @param quality 品质(128k 320k flac flac24bit)
//     * @return
//     */
//    public String getTrackUrl(String mid, String quality) {
//        String params = String.format("user=0611d9b202ca0820&android_id=0611d9b202ca0820&prod=kwplayer_ar_11.3.0.0&corp=kuwo&newver=3&vipver=11.3.0.0&source=kwwear_ar_2.2.3_Fwatch.apk" +
//                "&p2p=1&q36=8899378ed08282acc723cdbc100010615202&approval=false&loginUid=0&loginSid=0&appuid=2788274549&allpay=0&notrace=0&oaid=3d3e1ab6-b5a1-4767-be7c-55fe63786812" +
//                "&type=convert_url_with_sign&br=%s%s&format=%s&sig=0&rid=%s&priority=bitrate&network=4G&localUid=-1&mode=audition&from=PC&token=bad205aa9e8a50181d948b1cafc16091" +
//                "&bc_token=1462dab652d0a688811e13bddf48596a&timestamp=1755249729&uid=2788274549", hMap.get(quality), eMap.get(quality), eMap.get(quality), mid);
//        String url = "http://nmobi.kuwo.cn/mobi.s?f=kuwo&q=" + params;
//        String urlBody = HttpRequest.get(url)
//                .executeAsync()
//                .body();
//        JSONObject urlJson = JSONObject.parseObject(urlBody).getJSONObject("data");
//        if (JsonUtil.isEmpty(urlJson)) return "";
//        String trackUrl = urlJson.getString("url");
//        return trackUrl;
//    }
//
//    public static void main(String[] args) {
/// /        System.out.println(getInstance().base64Decrypt("GfpFYNPP+sMNUO+mrQXAhQl3mi5yh3oaLllRP1zUrsWS+z0OdN4kZRorWOeN290cfOMggtHmERFJr7VDD6KSwimb8WlJ63WTW05lIbfwvLazE0ldxLrJ/KsyY25tgs+qP/yniqZc00T9dNvY0ZaKijSPEuXrwJ5hzCWAz9zBeRw6/2t3t/KL220vZs/ej5BH0COfkuC6iiFM/FuDzZQiXKFvDvU3cf3iUylr9yYul7vwLP/IiYIvJgrWaknLsL7yzwLvgwVM/MUjDctRfR7v/v5ak9iXwzeOnJFbcp9wDC0U2hdXIzDhNwb6alh97qveYjBOC2u0npFGQRv/KluhdBnx9PIZ50B8jIgWH6CVxUiIi/GoXp7TqjBKc5GpRYmb4R9fJoa9VH/FJS7mjh1o/ZPQVEx6P/6r+40BdbmCUsVVC9E9/Hkp8hNVV7evo7/7aVdCgIet8JTXw2xrCXJHJG00EHdiwIJgEOquAxh3ymEBlJ5T8Xq1A7/V5TI+MmjsBgpMKSVIp20w6+F/RCv7YV7u8Hu2TzY+0dMpR3TKgBs8E/U5w4mDRNR/cSqQlJnzl9C4WpDdzXw634X2IobYCOziZZJqXAhEHB+5fx98z66MaD/1zqN2HvkZw9CrWimdRUbOKIwISu4ha2HkYTs2ycCRoFNq4IsvHrwDdEhqg8aOj1FBXyy+KKTFo3QI1NPFagmwibxFrjvMPIsNVHUP6/l/nK3wc4wLbwEByD597PzRwkG/x+UbEbxhHNlx29ZWbP8mdEj6rBpzI++P+R3k3r9lc7KFhRliM0nVnNQsEFZ5jW5LICTMsProJoQrKbZZ7R/MkW7LmEpuVpPKd2XDbkJYZohZYD+iObULwJDJ6K6fjLUc55vMaPkz+ziKimbJamNIiFPkHdE="));
//        System.out.println(getInstance().getTrackUrl("228908", "flac"));
//    }
//
//    // 解密方法，用于调试加密参数
////    public String base64Decrypt(String encryptedMsg) {
////        // Base64解码
////        byte[] encryptedBytes = CryptoUtil.base64DecodeToBytes(encryptedMsg);
////        // 使用密钥解密
////        byte[] decryptedBytes = decrypt(encryptedBytes, SECRET_KEY.getBytes(StandardCharsets.UTF_8));
////        // 转换为字符串
////        return new String(decryptedBytes, StandardCharsets.UTF_8);
////    }
////
////    private byte[] decrypt(byte[] encrypted, byte[] key) {
////        // 处理密钥（取前8字节）
////        long keyLong = 0;
////        for (int i = 0; i < 8; i++) keyLong |= (long) (key[i] & 0xFF) << (i * 8);
////
////        // 生成子密钥
////        long[] encryptKeys = new long[16];
////        subKeys(keyLong, encryptKeys);
////        // 反转子密钥用于解密
////        long[] decryptKeys = new long[16];
////        for (int i = 0; i < 16; i++) decryptKeys[i] = encryptKeys[15-i];
////
////        // 分组处理（每组 8 字节）
////        int blockCount = encrypted.length / 8;
////        long[] decryptedBlocks = new long[blockCount];
////
////        // 转换字节块为 long
////        for (int m = 0; m < blockCount; m++) {
////            long block = 0;
////            for (int n = 0; n < 8; n++) block |= (long) (encrypted[n+m*8] & 0xFF) << (n * 8);
////            decryptedBlocks[m] = DES64(decryptKeys, block);
////        }
////
////        // 将 long 数组转回字节
////        byte[] result = new byte[8*blockCount];
////        int index = 0;
////        for (long block : decryptedBlocks) {
////            for (int i = 0; i < 8; i++) result[index++] = (byte) ((block >> (i * 8)) & 0xFF);
////        }
////        return result;
////    }
//
//    private String base64Encrypt(String msg) {
//        byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
//        byte[] secretKeyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
//        byte[] b = encrypt(msgBytes, secretKeyBytes);
//        String s = CryptoUtil.base64Encode(b);
//        return s.replace("\n", "");
//    }
//
//    private byte[] encrypt(byte[] msg, byte[] key) {
//        // 处理密钥块
//        long l = 0;
//        for (int i = 0; i < 8; i++) l = l | (long) key[i] << i * 8;
//
//        int j = msg.length / 8;
//        // arrLong1 存放的是转换后的密钥块, 在解密时只需要把这个密钥块反转就行了
//        long[] arrLong1 = new long[16];
//        subKeys(l, arrLong1);
//
//        // arrLong2 存放的是前部分的明文
//        long[] arrLong2 = new long[j];
//        for (int m = 0; m < j; m++)
//            for (int n = 0; n < 8; n++)
//                arrLong2[m] |= (long) msg[n + m * 8] << n * 8;
//
//        // 用于存放密文
//        long[] arrLong3 = new long[(1 + 8 * (j + 1)) / 8];
//        // 计算前部的数据块(除了最后一部分)
//        for (int i1 = 0; i1 < j; i1++)
//            arrLong3[i1] = DES64(arrLong1, arrLong2[i1]);
//
//        // 保存多出来的字节
//        byte[] arrByte1 = Arrays.copyOfRange(msg, j * 8, msg.length);
//        long l2 = 0;
//        for (int i1 = 0, len = msg.length % 8; i1 < len; i1++)
//            l2 |= (long) arrByte1[i1] << i1 * 8;
//        // 计算多出的那一位(最后一位)
//        arrLong3[j] = DES64(arrLong1, l2);
//
//        // 将密文转为字节型
//        byte[] arrByte2 = new byte[8 * arrLong3.length];
//        int i4 = 0;
//        for (long l3 : arrLong3)
//            for (int i6 = 0; i6 < 8; i6++)
//                arrByte2[i4++] = (byte) (255 & l3 >> i6 * 8);
//
//        return arrByte2;
//    }
//
//    private long DES64(long[] longs, long l) {
//        long out;
//        int SOut;
//        long[] pR = new long[8];
//        long[] pSource = new long[2];
//        long L;
//        long R;
//        out = bitTransform(arrayIP, 64, l);
//        pSource[0] = 0xFFFFFFFFL & out;
//        pSource[1] = (-4294967296L & out) >> 32;
//        for (int i = 0; i < 16; i++) {
//            R = pSource[1];
//            R = bitTransform(arrayE, 64, R);
//            R ^= longs[i];
//            for (int j = 0; j < 8; j++)
//                pR[j] = 255 & R >> j * 8;
//            SOut = 0;
//            for (int sbi = 7; sbi > -1; sbi--) {
//                SOut <<= 4;
//                SOut |= matrixNSBox[sbi][(int) pR[sbi]];
//            }
//
//            R = bitTransform(arrayP, 32, SOut);
//            L = pSource[0];
//            pSource[0] = pSource[1];
//            pSource[1] = L ^ R;
//        }
//        out = -4294967296L & pSource[0] << 32 | 0xFFFFFFFFL & pSource[1];
//        out = bitTransform(arrayIP_1, 64, out);
//        return out;
//    }
//
//    private void subKeys(long l, long[] longs) {
//        long l2 = bitTransform(arrayPC_1, 56, l);
//        for (int i = 0; i < 16; i++) {
//            l2 = (l2 & arrayLsMask[arrayLs[i]]) << 28 - arrayLs[i] | (l2 & ~arrayLsMask[arrayLs[i]]) >> arrayLs[i];
//            longs[i] = bitTransform(arrayPC_2, 64, l2);
//        }
//    }
//
//    private long bitTransform(int[] arrInt, int n, long l) {
//        long l2 = 0;
//        for (int i = 0; i < n; i++) {
//            if (arrInt[i] < 0 || (l & arrayMask[arrInt[i]]) == 0) continue;
//            l2 |= arrayMask[i];
//        }
//        return l2;
//    }
//}
