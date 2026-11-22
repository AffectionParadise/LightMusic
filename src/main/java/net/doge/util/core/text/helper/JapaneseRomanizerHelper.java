package net.doge.util.core.text.helper;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import com.ibm.icu.text.Transliterator;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JapaneseRomanizerHelper {
    private static final Pattern JAPANESE_TEXT = Pattern.compile(
            "[\\p{IsHan}\\p{IsHiragana}\\p{IsKatakana}\u3099\u309Aーｰﾞﾟ]+");
    private static final Pattern KANA = Pattern.compile("[\\p{IsHiragana}\\p{IsKatakana}]");
    private static final Pattern HIRAGANA = Pattern.compile("\\p{IsHiragana}");
    private static final Pattern HAN = Pattern.compile("\\p{IsHan}");
    private static final Pattern OTHER_SCRIPT = Pattern.compile(
            "[\\p{L}&&[^\\p{IsHan}\\p{IsHiragana}\\p{IsKatakana}\\p{IsLatin}ー]]");
    private static final String LATIN_WORD = "\\p{IsLatin}+(?:['’]\\p{IsLatin}+)*";
    private static final Pattern LATIN_PHRASE_AT_EDGE = Pattern.compile(
            "^\\P{L}*" + LATIN_WORD + "\\h+" + LATIN_WORD + "\\h+"
                    + "|\\h+" + LATIN_WORD + "\\h+" + LATIN_WORD + "\\P{L}*$");
    private static final String LRC_METADATA = "\\[[A-Za-z][A-Za-z0-9_-]*:[^\\]\\r\\n]*\\]";
    private static final String LRC_TIMESTAMP = "\\[\\d+:\\d{1,2}(?:[.:]\\d+)?\\]";
    private static final String SENTENCE_END = "[。｡！？!?；;，]|(?<!\\p{Nd})[.．]|[.．](?!\\p{Nd})";
    private static final Pattern SENTENCE_SEPARATOR = Pattern.compile(LRC_METADATA + "|\\R|\\t+|" + SENTENCE_END);
    private static final Pattern CLAUSE_SEPARATOR = Pattern.compile("[、,「」『』“”]");
    private static final Pattern LRC_LINE = Pattern.compile(
            "^\\uFEFF?\\h*(?:" + LRC_TIMESTAMP + "\\h*)+(\\V*)$", Pattern.MULTILINE);
    private static final Pattern LRC_LYRIC_SEPARATOR = Pattern.compile(
            LRC_TIMESTAMP + "|" + LRC_METADATA + "|" + SENTENCE_END
                    + "|[/／|｜,\\t\u3000]+|\\h{2,}|[（(](?=[^()（）]*[）)]\\h*$)");
    private static final Pattern HORIZONTAL_SPACE = Pattern.compile("\\h+");
    private static final Pattern REPEATED_LONG_VOWEL = Pattern.compile("ー{2,}");
    private static final Pattern READING_UNIT = Pattern.compile(
            "[っッ]*[\\p{IsHiragana}\\p{IsKatakana}&&[^っッ]]\\p{M}*"
                    + "[ぁぃぅぇぉゃゅょゎァィゥェォャュョヮ]*ー*|\\p{IsHan}+|[っッ]+|.");

    /**
     * 将文本中识别为日语的完整句子转写为小写罗马音，发音单元之间用空格分隔。
     * LRC 中带时间戳的日语歌词片段也会转写。
     * 其他句子、原有换行、空白和标点均按原样保留。
     *
     * @param text 可包含多行和多种语言的文本
     * @return 转换日语句子后的文本
     */
    public static String toRomaji(String text) {
        if (text == null || !KANA.matcher(text).find()) return text;

        StringBuilder result = new StringBuilder(text.length());
        Matcher timedLines = LRC_LINE.matcher(text);
        int cursor = 0;
        while (timedLines.find()) {
            int start = timedLines.start(), end = timedLines.end();
            appendSentences(result, text.substring(cursor, start), SENTENCE_SEPARATOR, true);
            result.append(text, start, timedLines.start(1));
            appendLrcLyrics(result, timedLines.group(1));
            cursor = end;
        }
        appendSentences(result, text.substring(cursor), SENTENCE_SEPARATOR, true);
        return result.toString();
    }

    private static void appendLrcLyrics(StringBuilder result, String lyrics) {
        Matcher separators = LRC_LYRIC_SEPARATOR.matcher(lyrics);
        int start = 0;
        while (separators.find()) {
            int s = separators.start(), e = separators.end();
            appendLrcPart(result, lyrics.substring(start, s));
            result.append(lyrics, s, e);
            start = e;
        }
        appendLrcPart(result, lyrics.substring(start));
    }

    private static void appendLrcPart(StringBuilder result, String part) {
        if (isJapaneseText(part, true)) {
            appendRomanizedSentence(result, part);
            return;
        }

        int translationStart = findUnmarkedTranslation(part);
        if (translationStart >= 0) {
            appendRomanizedSentence(result, part.substring(0, translationStart));
            result.append(part, translationStart, part.length());
        } else {
            result.append(part);
        }
    }

    private static int findUnmarkedTranslation(String part) {
        if (!KANA.matcher(part).find()) return -1;

        Matcher gaps = HORIZONTAL_SPACE.matcher(part);
        List<Integer> candidates = new ArrayList<>();
        while (gaps.find()) {
            int start = gaps.start(), end = gaps.end(), len = part.length();
            if (start > 0 && end < len) candidates.add(start);
        }
        // 优先保留包含空格的最长日语歌词片段，剩余内容按原样保留
        for (int i = candidates.size() - 1; i >= 0; i--) {
            int boundary = candidates.get(i);
            if (isJapaneseText(part.substring(0, boundary), true)) return boundary;
        }
        return -1;
    }

    private static void appendSentences(StringBuilder result, String text, Pattern separator, boolean tryClauses) {
        Matcher boundaries = separator.matcher(text);
        int start = 0;
        while (boundaries.find()) {
            int s = boundaries.start(), e = boundaries.end();
            appendSentence(result, text.substring(start, s), tryClauses);
            result.append(text, s, e);
            start = e;
        }
        appendSentence(result, text.substring(start), tryClauses);
    }

    private static void appendSentence(StringBuilder result, String sentence, boolean tryClauses) {
        if (isJapaneseText(sentence, false)) appendRomanizedSentence(result, sentence);
        else if (tryClauses && CLAUSE_SEPARATOR.matcher(sentence).find())
            appendSentences(result, sentence, CLAUSE_SEPARATOR, false);
        else result.append(sentence);
    }

    private static boolean isJapaneseText(String sentence, boolean allowLyricFragment) {
        if (!KANA.matcher(sentence).find()) return false;

        String normalized = Normalizer.normalize(sentence, Normalizer.Form.NFKC);
        if (OTHER_SCRIPT.matcher(normalized).find() || LATIN_PHRASE_AT_EDGE.matcher(normalized).find()) return false;

        Token last = null;
        Token previous = null;
        boolean hasTopic = false;
        for (Token token : Resources.TOKENIZER.tokenize(normalized)) {
            String surface = token.getSurface();
            String partOfSpeech = token.getPartOfSpeechLevel1();
            if ("記号".equals(partOfSpeech) || !JAPANESE_TEXT.matcher(surface).find()) continue;
            // 没有日语读音的汉字词可能属于中文，不能仅凭末尾的假名判断
            if (!token.isKnown() && HAN.matcher(surface).find()) return false;
            hasTopic |= last != null && "助詞".equals(partOfSpeech) && "は".equals(surface);
            previous = last;
            last = token;
        }

        if (last == null) return false;
        if (allowLyricFragment && (HIRAGANA.matcher(normalized).find() || !HAN.matcher(normalized).find())) return true;

        String subtype = last.getPartOfSpeechLevel2();
        switch (last.getPartOfSpeechLevel1()) {
            case "感動詞":
                return true;
            case "名詞":
                return hasTopic || "形容動詞語幹".equals(subtype);
            case "助詞":
                if (previous == null) return false;
                String surface = last.getSurface();
                return "終助詞".equals(subtype)
                        || subtype.contains("終助詞") && isPredicate(previous)
                        || "接続助詞".equals(subtype) && ("て".equals(surface) || "で".equals(surface))
                        && "動詞".equals(previous.getPartOfSpeechLevel1());
            default:
                return isPredicate(last);
        }
    }

    private static boolean isPredicate(Token token) {
        switch (token.getPartOfSpeechLevel1()) {
            case "動詞":
            case "形容詞":
            case "助動詞":
                String form = token.getConjugationForm();
                return "基本形".equals(form) || form.startsWith("命令") || "いい".equals(token.getSurface());
            default:
                return false;
        }
    }

    private static void appendRomanizedSentence(StringBuilder result, String text) {
        Matcher japanese = JAPANESE_TEXT.matcher(text);
        int cursor = 0;
        while (japanese.find()) {
            int start = japanese.start();
            int end = japanese.end();
            result.append(text, cursor, start);
            if (start > 0 && Character.isLetterOrDigit(text.codePointBefore(start))) {
                result.append(' ');
            }
            String normalized = Normalizer.normalize(japanese.group(), Normalizer.Form.NFKC);
            appendJapanese(result, REPEATED_LONG_VOWEL.matcher(normalized).replaceAll("ー"));
            if (end < text.length() && Character.isLetterOrDigit(text.codePointAt(end))) {
                result.append(' ');
            }
            cursor = end;
        }
        result.append(text, cursor, text.length());
    }

    private static void appendJapanese(StringBuilder result, String japanese) {
        StringBuilder reading = new StringBuilder(japanese.length());
        for (Token token : Resources.TOKENIZER.tokenize(japanese)) {
            reading.append(readingOf(token));
        }

        StringBuilder spaced = new StringBuilder(reading.length());
        Matcher units = READING_UNIT.matcher(reading);
        while (units.find()) {
            char lastKana = reading.charAt(units.end() - 1);
            // 后面没有假名的小写“っ”（促音）与前一个音归在一起
            if (units.start() > 0 && lastKana != 'っ' && lastKana != 'ッ') {
                spaced.append(' ');
            }
            spaced.append(reading, units.start(), units.end());
        }
        result.append(toLatin(spaced.toString()));
    }

    private static String readingOf(Token token) {
        String surface = token.getSurface();
        if ("こんにちは".equals(surface) || "こんばんは".equals(surface)) {
            return token.getPronunciation();
        }
        if ("助詞".equals(token.getPartOfSpeechLevel1())) {
            switch (surface) {
                case "は":
                    return "ワ";
                case "へ":
                    return "エ";
                case "を":
                    return "オ";
                default:
                    break;
            }
        }

        String reading = token.getReading();
        return "*".equals(reading) ? surface : reading;
    }

    private static String toLatin(String reading) {
        String kana = reading.replace('ぢ', 'じ').replace('づ', 'ず')
                .replace('ヂ', 'ジ').replace('ヅ', 'ズ');
        synchronized (Resources.KANA_TO_LATIN) {
            return Resources.KANA_TO_LATIN.transliterate(kana).replace("~tsu", "'");
        }
    }

    private static final class Resources {
        private static final Tokenizer TOKENIZER = new Tokenizer();
        private static final Transliterator KANA_TO_LATIN =
                Transliterator.getInstance("Hiragana-Katakana; Katakana-Latin");
    }
}
