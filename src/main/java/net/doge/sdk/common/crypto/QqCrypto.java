package net.doge.sdk.common.crypto;

import net.doge.util.core.crypto.CryptoUtil;

public class QqCrypto {
    private static QqCrypto instance;

    private QqCrypto() {
    }

    public static QqCrypto getInstance() {
        if (instance == null) instance = new QqCrypto();
        return instance;
    }

    private final int[] PART_1_INDEXES = {23, 14, 6, 36, 16, 40, 7, 19};
    private final int[] PART_2_INDEXES = {16, 1, 32, 12, 19, 27, 8, 5};
    private final int[] SCRAMBLE_VALUES = {
            89, 39, 179, 150, 218, 82, 58, 252, 177, 52,
            186, 123, 120, 64, 242, 133, 143, 161, 121, 179
    };

    public String zzcSign(String text) {
        String hash = CryptoUtil.sha1Hex(text);
        String part1 = pickHashByIdx(hash, PART_1_INDEXES);
        String part2 = pickHashByIdx(hash, PART_2_INDEXES);
        int scrambleLen = SCRAMBLE_VALUES.length;
        byte[] part3 = new byte[scrambleLen];
        for (int i = 0; i < scrambleLen; i++) {
            int hex = Integer.parseInt(hash.substring(i * 2, i * 2 + 2), 16);
            part3[i] = (byte) (SCRAMBLE_VALUES[i] ^ hex);
        }
        String b64Part = CryptoUtil.base64Encode(part3).replaceAll("[\\\\/+=]", "");
        return ("zzc" + part1 + b64Part + part2).toLowerCase();
    }

    private String pickHashByIdx(String hash, int[] indexes) {
        StringBuilder sb = new StringBuilder();
        int len = hash.length();
        for (int idx : indexes) {
            if (idx >= 0 && idx < len) sb.append(hash.charAt(idx));
            // 越界时模拟 JS undefined 被 join('') 忽略
        }
        return sb.toString();
    }
}