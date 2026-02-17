package net.doge.sdk.service.music.info.impl.musicinfo.lyric.kw;

import net.doge.constant.core.lyric.LyricPattern;
import net.doge.entity.service.NetMusicInfo;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.array.ArrayUtil;
import net.doge.util.core.crypto.CryptoUtil;
import net.doge.util.core.http.HttpRequest;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class KwLyricReq {
    private static KwLyricReq instance;

    private KwLyricReq() {
    }

    public static KwLyricReq getInstance() {
        if (instance == null) instance = new KwLyricReq();
        return instance;
    }

    // 歌词 API (酷我)
//    private final String LYRIC_KW_API = "http://m.kuwo.cn/newh5/singles/songinfoandlrc?musicId=%s&httpsStatus=1";
    private final String LYRIC_KW_API = "http://newlyric.kuwo.cn/newlyric.lrc?";

    public void fillLyric(NetMusicInfo musicInfo) {
        String id = musicInfo.getId();

        // 请求参数加密
        byte[] keyBytes = "yeelion".getBytes(StandardCharsets.UTF_8);
        int keyLen = keyBytes.length;
        String params = "user=12345,web,web,web&requester=localhost&req=1&rid=MUSIC_" + id + "&lrcx=1";
        byte[] paramsBytes = params.getBytes(StandardCharsets.UTF_8);
        int paramsLen = paramsBytes.length;
        byte[] output = new byte[paramsLen];
        int i = 0;
        while (i < paramsLen) {
            int j = 0;
            while (j < keyLen && i < paramsLen) {
                output[i] = (byte) (keyBytes[j] ^ paramsBytes[i]);
                i++;
                j++;
            }
        }
        params = CryptoUtil.base64Encode(output);

        // 获取歌词
        byte[] bodyBytes = HttpRequest.get(LYRIC_KW_API + params)
                .executeAsBytes();
        if (!"tp=content".equals(new String(bodyBytes, 0, 10))) return;
        int index = ArrayUtil.indexOf(bodyBytes, "\r\n\r\n".getBytes(StandardCharsets.UTF_8)) + 4;
        byte[] nBytes = Arrays.copyOfRange(bodyBytes, index, bodyBytes.length);
        byte[] lyricData = CryptoUtil.decompress(nBytes);
        // 无 lrcx 参数时，此处直接获得 lrc 歌词
//        String lyricStr = new String(lyricData, Charset.forName("gb18030"));
        String lyricDataStr = new String(lyricData, StandardCharsets.UTF_8);
        byte[] lyricBytes = CryptoUtil.base64DecodeToBytes(lyricDataStr);
        int lyricLen = lyricBytes.length;
        output = new byte[lyricLen];
        i = 0;
        while (i < lyricLen) {
            int j = 0;
            while (j < keyLen && i < lyricLen) {
                output[i] = (byte) (lyricBytes[i] ^ keyBytes[j]);
                i++;
                j++;
            }
        }
        String lyricStr = new String(output, Charset.forName("gb18030"));

        // 解析酷我的偏移值
        int offset = 1, offset2 = 1;
        String kuwoValStr = RegexUtil.getGroup1("\\[kuwo:(\\d+)\\]", lyricStr);
        if (StringUtil.notEmpty(kuwoValStr)) {
            int kuwoVal = Integer.parseInt(kuwoValStr, 8);
            offset = kuwoVal / 10;
            offset2 = kuwoVal % 10;
        }
        // 解析逐字歌词
        String lineTimeExp = "\\[\\d+:\\d+(?:[.:]\\d+)?\\]";
        String[] lsp = lyricStr.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String l : lsp) {
            List<String> s1List = RegexUtil.findAllGroup1("<(\\d+),-?\\d+>", l);
            if (s1List.isEmpty()) sb.append(l);
            else {
                List<String> s2List = RegexUtil.findAllGroup1("<\\d+,(-?\\d+)>", l);
                // 行时间
                String lineTimeStr = RegexUtil.getGroup0(lineTimeExp, l);
                sb.append(lineTimeStr);
                String[] sp = ArrayUtil.removeFirstEmpty(l.replaceFirst(lineTimeExp, "").split("<\\d+,-?\\d+>", -1));
                for (int k = 0, s = s1List.size(); k < s; k++) {
                    int n1 = Integer.parseInt(s1List.get(k));
                    int n2 = Integer.parseInt(s2List.get(k));
                    int wordStartTime = Math.abs((n1 + n2) / (offset * 2));
                    int wordDuration = Math.abs((n1 - n2) / (offset2 * 2));
                    sb.append(String.format(LyricPattern.PAIR_FMT, wordStartTime, wordDuration));
                    sb.append(sp[k]);
                }
            }
            sb.append("\n");
        }
        lyricStr = sb.toString();

        // 分离歌词和翻译
        String[] sp = lyricStr.split("\n");
        sb = new StringBuilder();
        boolean hasTrans = false;
        int s = sp.length;
        for (int j = 0; j < s; j++) {
            String sentence = sp[j], nextSentence = j + 1 < s ? sp[j + 1] : null;
            // 歌词中带有翻译时，最后一句是翻译直接跳过
            if (hasTrans && StringUtil.isEmpty(nextSentence)) break;
            String time = RegexUtil.getGroup0(lineTimeExp, sentence);
            if (StringUtil.isEmpty(time)) {
                sb.append(sentence).append("\n");
                continue;
            }
            String nextTime = null;
            if (StringUtil.notEmpty(nextSentence)) nextTime = RegexUtil.getGroup0(lineTimeExp, nextSentence);
            // 歌词中带有翻译，有多个 time 相同的歌词时取不重复的第二个
            if (!time.equals(nextTime)) sb.append(sentence).append("\n");
            else hasTrans = true;
        }
        musicInfo.setLyric(sb.toString());

        sb = new StringBuilder();
        hasTrans = false;
        String lastTime = null;
        for (i = 0; i < s; i++) {
            String sentence = sp[i], nextSentence = i + 1 < s ? sp[i + 1] : null;
            String time = RegexUtil.getGroup0(lineTimeExp, sentence);
            if (StringUtil.isEmpty(time)) continue;
            String nextTime = null;
            if (StringUtil.notEmpty(nextSentence)) nextTime = RegexUtil.getGroup0(lineTimeExp, nextSentence);
            // 歌词中带有翻译，有多个 time 相同的歌词时取重复的第一个；最后一句也是翻译
            if (hasTrans && nextTime == null || time.equals(nextTime)) {
                sb.append(lastTime);
                sb.append(sentence.replaceFirst(lineTimeExp, ""));
                sb.append("\n");
                hasTrans = true;
            }
            lastTime = time;
        }
        // 去除翻译中无用的逐字时间轴
        musicInfo.setTrans(sb.toString().replaceAll("<\\d+,-?\\d+>", ""));

//            String lyricBody = SdkCommon.kwRequest(String.format(LYRIC_KW_API, id))
//                    .executeAsync()
//                    .body();
//            JSONObject data = JSONObject.parseObject(lyricBody).getJSONObject("data");
//            if (JsonUtil.isEmpty(data)) {
//                musicInfo.setLyric(null);
//                musicInfo.setTrans(null);
//                return;
//            }
//            // 酷我歌词返回的是数组，需要先处理成字符串！
//            // lyriclist 可能是数组也可能为 null ！
//            JSONArray lyricArray = data.getJSONArray("lyriclist");
//            if (JsonUtil.notEmpty(lyricArray)) {
//                StringBuilder sb = new StringBuilder();
//                boolean hasTrans = false;
//                for (int i = 0, len = lyricArray.size(); i < len; i++) {
//                    JSONObject sentenceJson = lyricArray.getJSONObject(i);
//                    JSONObject nextSentenceJson = i + 1 < len ? lyricArray.getJSONObject(i + 1) : null;
//                    // 歌词中带有翻译时，最后一句是翻译直接跳过
//                    if (hasTrans && JsonUtil.isEmpty(nextSentenceJson)) break;
//                    String time = TimeUtil.formatToLyricTime(sentenceJson.getDouble("time"));
//                    String nextTime = null;
//                    if (JsonUtil.notEmpty(nextSentenceJson))
//                        nextTime = TimeUtil.formatToLyricTime(nextSentenceJson.getDouble("time"));
//                    // 歌词中带有翻译，有多个 time 相同的歌词时取不重复的第二个
//                    if (!time.equals(nextTime)) {
//                        sb.append(time);
//                        String lineLyric = StringUtil.removeHTMLLabel(sentenceJson.getString("lineLyric"));
//                        sb.append(lineLyric);
//                        sb.append("\n");
//                    } else hasTrans = true;
//                }
//                musicInfo.setLyric(sb.toString());
//            } else musicInfo.setLyric(null);
//
//            // 酷我歌词返回的是数组，需要先处理成字符串！
//            // lyriclist 可能是数组也可能为 null ！
//            if (JsonUtil.notEmpty(lyricArray)) {
//                StringBuilder sb = new StringBuilder();
//                boolean hasTrans = false;
//                String lastTime = null;
//                for (int i = 0, len = lyricArray.size(); i < len; i++) {
//                    JSONObject sentenceJson = lyricArray.getJSONObject(i);
//                    JSONObject nextSentenceJson = i + 1 < len ? lyricArray.getJSONObject(i + 1) : null;
//                    String time = TimeUtil.formatToLyricTime(sentenceJson.getDouble("time"));
//                    String nextTime = null;
//                    if (JsonUtil.notEmpty(nextSentenceJson))
//                        nextTime = TimeUtil.formatToLyricTime(nextSentenceJson.getDouble("time"));
//                    // 歌词中带有翻译，有多个 time 相同的歌词时取重复的第一个；最后一句也是翻译
//                    if (hasTrans && nextTime == null || time.equals(nextTime)) {
//                        sb.append(lastTime);
//                        String lineLyric = StringUtil.removeHTMLLabel(sentenceJson.getString("lineLyric"));
//                        sb.append(lineLyric);
//                        sb.append("\n");
//                        hasTrans = true;
//                    }
//                    lastTime = time;
//                }
//                musicInfo.setTrans(sb.toString());
//            } else musicInfo.setTrans(null);
    }
}
