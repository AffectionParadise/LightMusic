package net.doge.sdk.util;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class BvAvConverter {
    private static BvAvConverter instance;

    private BvAvConverter() {
        initCharMap();
    }

    public static BvAvConverter getInstance() {
        if (instance == null) instance = new BvAvConverter();
        return instance;
    }

    public final Map<Character, String> CHAR_MAP = new HashMap<>();
    public final BigInteger BASE = new BigInteger("58");
    public final BigInteger SUB_V = new BigInteger("100618342136696320");
    public final BigInteger XOR_V = new BigInteger("1010100100111011001100100100", 2);
    public final int[] POWER = {6, 2, 4, 8, 5, 9, 3, 7, 1, 0};

    private void initCharMap() {
        CHAR_MAP.put('1', "13");
        CHAR_MAP.put('2', "12");
        CHAR_MAP.put('3', "46");
        CHAR_MAP.put('4', "31");
        CHAR_MAP.put('5', "43");
        CHAR_MAP.put('6', "18");
        CHAR_MAP.put('7', "40");
        CHAR_MAP.put('8', "28");
        CHAR_MAP.put('9', "5");
        CHAR_MAP.put('A', "54");
        CHAR_MAP.put('B', "20");
        CHAR_MAP.put('C', "15");
        CHAR_MAP.put('D', "8");
        CHAR_MAP.put('E', "39");
        CHAR_MAP.put('F', "57");
        CHAR_MAP.put('G', "45");
        CHAR_MAP.put('H', "36");
        CHAR_MAP.put('J', "38");
        CHAR_MAP.put('K', "51");
        CHAR_MAP.put('L', "42");
        CHAR_MAP.put('M', "49");
        CHAR_MAP.put('N', "52");
        CHAR_MAP.put('P', "53");
        CHAR_MAP.put('Q', "7");
        CHAR_MAP.put('R', "4");
        CHAR_MAP.put('S', "9");
        CHAR_MAP.put('T', "50");
        CHAR_MAP.put('U', "10");
        CHAR_MAP.put('V', "44");
        CHAR_MAP.put('W', "34");
        CHAR_MAP.put('X', "6");
        CHAR_MAP.put('Y', "25");
        CHAR_MAP.put('Z', "1");
        CHAR_MAP.put('a', "26");
        CHAR_MAP.put('b', "29");
        CHAR_MAP.put('c', "56");
        CHAR_MAP.put('d', "3");
        CHAR_MAP.put('e', "24");
        CHAR_MAP.put('f', "0");
        CHAR_MAP.put('g', "47");
        CHAR_MAP.put('h', "27");
        CHAR_MAP.put('i', "22");
        CHAR_MAP.put('j', "41");
        CHAR_MAP.put('k', "16");
        CHAR_MAP.put('m', "11");
        CHAR_MAP.put('n', "37");
        CHAR_MAP.put('o', "2");
        CHAR_MAP.put('p', "35");
        CHAR_MAP.put('q', "21");
        CHAR_MAP.put('r', "17");
        CHAR_MAP.put('s', "33");
        CHAR_MAP.put('t', "30");
        CHAR_MAP.put('u', "48");
        CHAR_MAP.put('v', "23");
        CHAR_MAP.put('w', "55");
        CHAR_MAP.put('x', "32");
        CHAR_MAP.put('y', "14");
        CHAR_MAP.put('z', "19");
    }

    /**
     * Bv 号转 Av
     *
     * @param bv
     * @return
     */
    public String convertBv2Av(String bv) {
        bv = bv.substring(2);
        BigInteger temp = new BigInteger("0");
        for (int i = 0; i < POWER.length; i++) {
            BigInteger part = new BigInteger(CHAR_MAP.get(bv.charAt(i)));
            part = part.multiply(BASE.pow(POWER[i]));
            temp = temp.add(part);
        }
        temp = temp.subtract(SUB_V);
        temp = temp.xor(XOR_V);
        return temp.toString();
    }

    /**
     * Av 号转 Bv
     *
     * @param av
     * @return
     */
    public String convertAv2Bv(String av) {
        av = av.substring(2);
        BigInteger temp = new BigInteger(av);
        temp = temp.xor(XOR_V);
        temp = temp.add(SUB_V);
        StringBuilder res = new StringBuilder("bv");
        for (int p : POWER) {
            String t = temp.divide(BASE.pow(p)).mod(BASE).toString();
            for (Entry<Character, String> entry : CHAR_MAP.entrySet()) {
                if (entry.getValue().equals(t)) res.append(entry.getKey());
            }
        }
        return res.toString();
    }
}
