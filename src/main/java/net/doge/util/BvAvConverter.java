package net.doge.util;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class BvAvConverter {
    public final static Map<Character, String> charMap;
    public final static BigInteger base = new BigInteger("58");
    public final static BigInteger subV = new BigInteger("100618342136696320");
    public final static BigInteger xorV = new BigInteger("1010100100111011001100100100", 2);
    public final static int[] power = {6, 2, 4, 8, 5, 9, 3, 7, 1, 0};

    static {
        charMap = new HashMap<>();
        charMap.put('1', "13");
        charMap.put('2', "12");
        charMap.put('3', "46");
        charMap.put('4', "31");
        charMap.put('5', "43");
        charMap.put('6', "18");
        charMap.put('7', "40");
        charMap.put('8', "28");
        charMap.put('9', "5");
        charMap.put('A', "54");
        charMap.put('B', "20");
        charMap.put('C', "15");
        charMap.put('D', "8");
        charMap.put('E', "39");
        charMap.put('F', "57");
        charMap.put('G', "45");
        charMap.put('H', "36");
        charMap.put('J', "38");
        charMap.put('K', "51");
        charMap.put('L', "42");
        charMap.put('M', "49");
        charMap.put('N', "52");
        charMap.put('P', "53");
        charMap.put('Q', "7");
        charMap.put('R', "4");
        charMap.put('S', "9");
        charMap.put('T', "50");
        charMap.put('U', "10");
        charMap.put('V', "44");
        charMap.put('W', "34");
        charMap.put('X', "6");
        charMap.put('Y', "25");
        charMap.put('Z', "1");
        charMap.put('a', "26");
        charMap.put('b', "29");
        charMap.put('c', "56");
        charMap.put('d', "3");
        charMap.put('e', "24");
        charMap.put('f', "0");
        charMap.put('g', "47");
        charMap.put('h', "27");
        charMap.put('i', "22");
        charMap.put('j', "41");
        charMap.put('k', "16");
        charMap.put('m', "11");
        charMap.put('n', "37");
        charMap.put('o', "2");
        charMap.put('p', "35");
        charMap.put('q', "21");
        charMap.put('r', "17");
        charMap.put('s', "33");
        charMap.put('t', "30");
        charMap.put('u', "48");
        charMap.put('v', "23");
        charMap.put('w', "55");
        charMap.put('x', "32");
        charMap.put('y', "14");
        charMap.put('z', "19");
    }

    /**
     * Bv 号转 Av
     * @param bv
     * @return
     */
    public static String convertBv2Av(String bv) {
        bv = bv.substring(2);
        BigInteger temp = new BigInteger("0");
        for (int i = 0; i < power.length; i++) {
            BigInteger part = new BigInteger(charMap.get(bv.charAt(i)));
            part = part.multiply(base.pow(power[i]));
            temp = temp.add(part);
        }
        temp = temp.subtract(subV);
        temp = temp.xor(xorV);
        return temp.toString();
    }

    /**
     * Av 号转 Bv
     * @param av
     * @return
     */
    public static String convertAv2Bv(String av) {
        av = av.substring(2);
        BigInteger temp = new BigInteger(av);
        temp = temp.xor(xorV);
        temp = temp.add(subV);
        String res = "bv";
        for (int i = 0; i < power.length; i++) {
            String t = temp.divide(base.pow(power[i])).mod(base).toString();
            for (Entry<Character, String> entry : charMap.entrySet()) {
                if(entry.getValue().equals(t)) {
                    res += entry.getKey();
                }
            }
        }
        return res;
    }
}
