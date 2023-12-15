package net.doge.sdk.entity.music.info.lyrichero.nc;

import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.model.entity.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.util.collection.ArrayUtil;
import net.doge.util.common.JsonUtil;
import net.doge.util.common.RegexUtil;
import net.doge.util.common.StringUtil;
import net.doge.util.common.TimeUtil;

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

    public void fillLrc(NetMusicInfo musicInfo) {
        String id = musicInfo.getId();

        Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.eapi("/api/song/lyric/v1");
        String lrcBody = SdkCommon.ncRequest(Method.POST, LYRIC_API,
                        String.format("{\"id\":\"%s\",\"cp\":false,\"tv\":0,\"lv\":0,\"rv\":0,\"kv\":0,\"yv\":0,\"ytv\":0,\"yrv\":0}", id), options)
                .executeAsync()
                .body();
        JSONObject lrcJson = JSONObject.parseObject(lrcBody);
        JSONObject lrc = lrcJson.getJSONObject("lrc");
        JSONObject yrc = lrcJson.getJSONObject("yrc");
        JSONObject tLrc = lrcJson.getJSONObject("tlyric");
        JSONObject romaLrc = lrcJson.getJSONObject("romalrc");
        // 逐字歌词
        if (JsonUtil.notEmpty(yrc)) {
            // 网易云歌词中包含部分 json 数据需要解析
            String lyric = yrc.getString("lyric");
            if (StringUtil.isEmpty(lyric)) musicInfo.setLrc("");
            else {
                String[] lsp = lyric.split("\n");
                StringBuilder sb = new StringBuilder();
                for (String l : lsp) {
                    if (JsonUtil.isValidObject(l)) {
                        JSONObject obj = JSONObject.parseObject(l);
                        Double t = obj.getDouble("t");
                        if (t != null) sb.append(TimeUtil.formatToLrcTime(t / 1000));
                        JSONArray cArray = obj.getJSONArray("c");
                        for (int i = 0, s = cArray.size(); i < s; i++)
                            sb.append(cArray.getJSONObject(i).getString("tx"));
                    } else {
                        // 行起始时间
                        String lineStartStr = RegexUtil.getGroup1("\\[(\\d+),\\d+\\]", l);
                        int lineStart = Integer.parseInt(lineStartStr);
                        String lrcTime = TimeUtil.formatToLrcTime((double) lineStart / 1000);
                        sb.append(lrcTime);

                        List<String> wordStartList = RegexUtil.findAllGroup1("\\((\\d+),\\d+,\\d+\\)", l);
                        List<String> wordDurationList = RegexUtil.findAllGroup1("\\(\\d+,(\\d+),\\d+\\)", l);
                        String[] sp = ArrayUtil.removeFirstEmpty(l.replaceFirst("\\[\\d+,\\d+\\]", "").split("\\(\\d+,\\d+,\\d+\\)", -1));
                        for (int i = 0, s = wordStartList.size(); i < s; i++) {
                            String wordStart = wordStartList.get(i);
                            int wsi = Integer.parseInt(wordStart);
                            sb.append("<")
                                    .append(wsi - lineStart)
                                    .append(",")
                                    .append(wordDurationList.get(i))
                                    .append(">")
                                    .append(sp[i]);
                        }
                    }
                    sb.append("\n");
                }
                musicInfo.setLrc(sb.toString());
            }
        }
        // lrc 歌词
        else if (JsonUtil.notEmpty(lrc)) {
            // 网易云歌词中包含部分 json 数据需要解析
            String lyric = lrc.getString("lyric");
            if (StringUtil.isEmpty(lyric)) musicInfo.setLrc("");
            else {
                String[] lsp = lyric.split("\n");
                StringBuilder sb = new StringBuilder();
                for (String l : lsp) {
                    if (JsonUtil.isValidObject(l)) {
                        JSONObject obj = JSONObject.parseObject(l);
                        Double t = obj.getDouble("t");
                        if (t != null) sb.append(TimeUtil.formatToLrcTime(t / 1000));
                        JSONArray cArray = obj.getJSONArray("c");
                        for (int i = 0, s = cArray.size(); i < s; i++)
                            sb.append(cArray.getJSONObject(i).getString("tx"));
                    } else sb.append(l);
                    sb.append("\n");
                }
                musicInfo.setLrc(sb.toString());
            }
        }
        if (JsonUtil.notEmpty(tLrc)) musicInfo.setTrans(tLrc.getString("lyric"));
        if (JsonUtil.notEmpty(romaLrc)) musicInfo.setRoma(romaLrc.getString("lyric"));
    }
}
