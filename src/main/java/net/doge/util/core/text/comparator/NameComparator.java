package net.doge.util.core.text.comparator;

import java.text.Collator;
import java.util.Comparator;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 名称比较器
 */
public class NameComparator implements Comparator<String> {
    private static NameComparator instance;

    public static NameComparator getInstance() {
        if (instance == null) instance = new NameComparator();
        return instance;
    }

    private final Pattern NAME_PART = Pattern.compile("\\p{Nd}+|\\P{Nd}+");
    private final Collator textOrder = Collator.getInstance();

    private NameComparator() {
        textOrder.setStrength(Collator.SECONDARY);
    }

    /**
     * 按默认区域设置比较文本，并将连续的十进制数字按数值大小进行比较
     */
    @Override
    public int compare(String first, String second) {
        Objects.requireNonNull(first, "first");
        Objects.requireNonNull(second, "second");
        Matcher firstParts = NAME_PART.matcher(first);
        Matcher secondParts = NAME_PART.matcher(second);
        boolean firstHasPart = firstParts.find();
        boolean secondHasPart = secondParts.find();

        while (firstHasPart && secondHasPart) {
            int comparison = comparePart(firstParts.group(), secondParts.group());
            if (comparison != 0) return comparison;
            firstHasPart = firstParts.find();
            secondHasPart = secondParts.find();
        }
        return Boolean.compare(firstHasPart, secondHasPart);
    }

    private int comparePart(String first, String second) {
        boolean firstIsNumber = Character.isDigit(first.codePointAt(0));
        boolean secondIsNumber = Character.isDigit(second.codePointAt(0));
        if (firstIsNumber && secondIsNumber) return compareNumbers(first, second);
        if (firstIsNumber != secondIsNumber) return firstIsNumber ? -1 : 1;
        synchronized (textOrder) {
            return textOrder.compare(first, second);
        }
    }

    private int compareNumbers(String first, String second) {
        int firstDigit = skipLeadingZeros(first);
        int secondDigit = skipLeadingZeros(second);
        int firstLen = first.length(), secondLen = second.length();
        int firstCpLen = first.codePointCount(firstDigit, firstLen);
        int secondCpLen = second.codePointCount(secondDigit, secondLen);
        if (firstCpLen != secondCpLen) return Integer.compare(firstCpLen, secondCpLen);

        while (firstDigit < firstLen) {
            int firstCodePoint = first.codePointAt(firstDigit);
            int secondCodePoint = second.codePointAt(secondDigit);
            int comparison = Integer.compare(Character.digit(firstCodePoint, 10), Character.digit(secondCodePoint, 10));
            if (comparison != 0) return comparison;
            firstDigit += Character.charCount(firstCodePoint);
            secondDigit += Character.charCount(secondCodePoint);
        }

        // 在数值相等的情况下，将前导零更多的连续数字串排在前面
        return Integer.compare(second.codePointCount(0, secondLen), first.codePointCount(0, firstLen));
    }

    private int skipLeadingZeros(String number) {
        int offset = 0, len = number.length();
        while (offset < len) {
            int digit = number.codePointAt(offset);
            if (Character.digit(digit, 10) != 0) break;
            offset += Character.charCount(digit);
        }
        return offset;
    }
}
