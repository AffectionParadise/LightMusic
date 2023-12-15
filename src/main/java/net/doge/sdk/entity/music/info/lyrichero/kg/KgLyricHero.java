package net.doge.sdk.entity.music.info.lyrichero.kg;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.model.entity.NetMusicInfo;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.common.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class KgLyricHero {
    private static KgLyricHero instance;

    private KgLyricHero() {
    }

    public static KgLyricHero getInstance() {
        if (instance == null) instance = new KgLyricHero();
        return instance;
    }

    // 歌词 API (酷狗)
    private final String SEARCH_LYRIC_KG_API = "http://lyrics.kugou.com/search?ver=1&man=yes&client=pc&keyword=%s&hash=%s&timelength=%s";
    private final String LYRIC_KG_API = "http://lyrics.kugou.com/download?ver=1&client=pc&id=%s&accesskey=%s&fmt=krc&charset=utf8";

    public void fillLrc(NetMusicInfo musicInfo) {
        String hash = musicInfo.getHash();
        String name = musicInfo.getName();
        double duration = musicInfo.getDuration();

        String lrcId, accessKey;

        // 搜索歌词
        String lBody = HttpRequest.get(String.format(SEARCH_LYRIC_KG_API, StringUtil.urlEncodeAll(name), hash, duration))
                .header(Header.USER_AGENT, "KuGou2012-9020-ExpandSearchManager")
                .header("KG-RC", "1")
                .header("KG-THash", "expand_search_manager.cpp:852736169:451")
                .executeAsync()
                .body();
        // 部分响应体 json 格式有误，直接用正则表达式提取
        if (JsonUtil.isValidObject(lBody)) {
            JSONObject data = JSONObject.parseObject(lBody);
            JSONArray candidates = data.getJSONArray("candidates");
            if (JsonUtil.isEmpty(candidates)) return;
            JSONObject info = candidates.getJSONObject(0);
            lrcId = info.getString("id");
            accessKey = info.getString("accesskey");
        } else {
            lrcId = RegexUtil.getGroup1("\"id\":\"(\\d+)\"", lBody);
            accessKey = RegexUtil.getGroup1("\"accesskey\":\"([0-9A-Z]+)\"", lBody);
        }
        if (StringUtil.isEmpty(lrcId) || StringUtil.isEmpty(accessKey)) return;

        // 获取歌词
        String lrcBody = HttpRequest.get(String.format(LYRIC_KG_API, lrcId, accessKey))
                .header(Header.USER_AGENT, "KuGou2012-9020-ExpandSearchManager")
                .header("KG-RC", "1")
                .header("KG-THash", "expand_search_manager.cpp:852736169:451")
                .executeAsync()
                .body();
        JSONObject lrcData = JSONObject.parseObject(lrcBody);
        String content = lrcData.getString("content");
        if (StringUtil.isEmpty(content)) return;

        // 解密
        byte[] encKey = new byte[]{0x40, 0x47, 0x61, 0x77, 0x5e, 0x32, 0x74, 0x47, 0x51, 0x36, 0x31, 0x2d, (byte) 0xce, (byte) 0xd2, 0x6e, 0x69};
        byte[] contentBytes = CryptoUtil.base64DecodeToBytes(content);
        contentBytes = Arrays.copyOfRange(contentBytes, 4, contentBytes.length);
        for (int i = 0, len = contentBytes.length; i < len; i++)
            contentBytes[i] = (byte) (contentBytes[i] ^ encKey[i % 16]);
        String result = new String(CryptoUtil.decompress(contentBytes), StandardCharsets.UTF_8);

        // 提取酷狗歌词
        String headExp = "^.*\\[id:\\$\\w+\\]\\n";
        result = result.replace("\r", "");
        if (RegexUtil.contains(headExp, result)) result = result.replaceFirst(headExp, "");
        String trans = RegexUtil.getGroup1("\\[language:([\\w=\\\\/+]+)\\]", result);

        // 处理翻译和罗马音
        if (StringUtil.notEmpty(trans)) {
            result = result.replaceFirst("\\[language:[\\w=\\\\/+]+\\]\\n", "");
            String transBody = CryptoUtil.base64Decode(trans);
            JSONObject transJson = JSONObject.parseObject(transBody);
            JSONArray contentArray = transJson.getJSONArray("content");
            JSONArray transArray = null, romaArray = null;
            for (int i = 0, s = contentArray.size(); i < s; i++) {
                JSONObject json = contentArray.getJSONObject(i);
                if (json.getIntValue("type") == 1) transArray = json.getJSONArray("lyricContent");
                else romaArray = json.getJSONArray("lyricContent");
            }
            // 翻译
            if (JsonUtil.notEmpty(transArray)) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0, s = transArray.size(); i < s; i++) {
                    JSONArray array = transArray.getJSONArray(i);
                    sb.append(SdkUtil.joinString(array, ""));
                    sb.append("\n");
                }
                musicInfo.setTrans(sb.toString());
            }
            // 罗马音
            if (JsonUtil.notEmpty(romaArray)) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0, s = romaArray.size(); i < s; i++) {
                    JSONArray array = romaArray.getJSONArray(i);
                    sb.append(SdkUtil.joinString(array, "").trim());
                    sb.append("\n");
                }
                musicInfo.setRoma(StringUtil.shortenBlank(sb.toString()));
            }
        }

        // 处理逐字歌词
        String[] lsp = result.split("\n");
        String lineTimeExp = "\\[(\\d+),\\d+\\]";
        StringBuilder sb = new StringBuilder();
        for (String l : lsp) {
            if (RegexUtil.contains(lineTimeExp, l)) {
                // 行起始时间
                String lineStartStr = RegexUtil.getGroup1(lineTimeExp, l);
                int lineStart = Integer.parseInt(lineStartStr);
                String lrcTime = TimeUtil.formatToLrcTime((double) lineStart / 1000);
                sb.append(lrcTime);
                sb.append(l.replaceFirst(lineTimeExp, "").replaceAll("<-?(\\d+),(\\d+),\\d+>", "<$1,$2>"));
            } else sb.append(l);
            sb.append("\n");
        }
        musicInfo.setLrc(sb.toString());
    }
}
