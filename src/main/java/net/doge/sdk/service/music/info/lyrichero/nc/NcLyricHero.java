package net.doge.sdk.service.music.info.lyrichero.nc;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.lyric.LyricPattern;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.array.ArrayUtil;
import net.doge.util.core.http.constant.Method;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.media.DurationUtil;

import java.util.List;
import java.util.Map;

public class NcLyricHero {
    private static NcLyricHero instance;

    private NcLyricHero() {
    }

    public static NcLyricHero getInstance() {
        if (instance == null) instance = new NcLyricHero();
        return instance;
    }

    // 歌词 API
    private final String LYRIC_API = "https://interface3.music.163.com/eapi/song/lyric/v1";

    public void fillLyric(NetMusicInfo musicInfo) {
        String id = musicInfo.getId();

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.eapi("/api/song/lyric/v1");
        String lyricBody = SdkCommon.ncRequest(Method.POST, LYRIC_API,
                        String.format("{\"id\":\"%s\",\"cp\":false,\"tv\":0,\"lv\":0,\"rv\":0,\"kv\":0,\"yv\":0,\"ytv\":0,\"yrv\":0}", id), options)
                .executeAsStr();
        JSONObject lyricJson = JSONObject.parseObject(lyricBody);
        JSONObject lrcJson = lyricJson.getJSONObject("lrc");
        JSONObject yrcJson = lyricJson.getJSONObject("yrc");
        JSONObject tLrcJson = lyricJson.getJSONObject("tlyric");
        JSONObject romaLrcJson = lyricJson.getJSONObject("romalrc");
        // 逐字歌词
        if (JsonUtil.notEmpty(yrcJson)) {
            // 网易云歌词中包含部分 json 数据需要解析
            String lyric = yrcJson.getString("lyric");
            if (StringUtil.isEmpty(lyric)) musicInfo.setLyric("");
            else {
                String[] lsp = lyric.split("\n");
                StringBuilder sb = new StringBuilder();
                for (String l : lsp) {
                    if (JsonUtil.isValidObject(l)) {
                        JSONObject obj = JSONObject.parseObject(l);
                        Double t = obj.getDouble("t");
                        if (t != null) sb.append(DurationUtil.formatToLyricTime(t / 1000));
                        JSONArray cArray = obj.getJSONArray("c");
                        for (int i = 0, s = cArray.size(); i < s; i++)
                            sb.append(cArray.getJSONObject(i).getString("tx"));
                    } else {
                        // 行起始时间
                        String lineStartStr = RegexUtil.getGroup1("\\[(\\d+),\\d+\\]", l);
                        if (StringUtil.notEmpty(lineStartStr)) {
                            int lineStart = Integer.parseInt(lineStartStr);
                            String lyricTime = DurationUtil.formatToLyricTime((double) lineStart / 1000);
                            sb.append(lyricTime);

                            List<String> wordStartList = RegexUtil.findAllGroup1("\\((\\d+),\\d+,\\d+\\)", l);
                            List<String> wordDurationList = RegexUtil.findAllGroup1("\\(\\d+,(\\d+),\\d+\\)", l);
                            String[] sp = ArrayUtil.removeFirstEmpty(l.replaceFirst("\\[\\d+,\\d+\\]", "").split("\\(\\d+,\\d+,\\d+\\)", -1));
                            for (int i = 0, s = wordStartList.size(); i < s; i++) {
                                String wordStart = wordStartList.get(i);
                                int wsi = Integer.parseInt(wordStart);
                                sb.append(String.format(LyricPattern.PAIR_FMT, wsi - lineStart, wordDurationList.get(i)));
                                sb.append(sp[i]);
                            }
                        } else sb.append(l);
                    }
                    sb.append("\n");
                }
                musicInfo.setLyric(sb.toString());
            }
        }
        // lrc 歌词
        if (!musicInfo.hasLyric() && JsonUtil.notEmpty(lrcJson)) {
            // 网易云歌词中包含部分 json 数据需要解析
            String lyric = lrcJson.getString("lyric");
            if (StringUtil.isEmpty(lyric)) musicInfo.setLyric("");
            else {
                String[] lsp = lyric.split("\n");
                StringBuilder sb = new StringBuilder();
                for (String l : lsp) {
                    if (JsonUtil.isValidObject(l)) {
                        JSONObject obj = JSONObject.parseObject(l);
                        Double t = obj.getDouble("t");
                        if (t != null) sb.append(DurationUtil.formatToLyricTime(t / 1000));
                        JSONArray cArray = obj.getJSONArray("c");
                        for (int i = 0, s = cArray.size(); i < s; i++)
                            sb.append(cArray.getJSONObject(i).getString("tx"));
                    } else sb.append(l);
                    sb.append("\n");
                }
                musicInfo.setLyric(sb.toString());
            }
        }
        if (JsonUtil.notEmpty(tLrcJson)) musicInfo.setTrans(tLrcJson.getString("lyric"));
        if (JsonUtil.notEmpty(romaLrcJson)) musicInfo.setRoma(romaLrcJson.getString("lyric"));
    }
}
