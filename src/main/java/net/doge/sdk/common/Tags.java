package net.doge.sdk.common;

import net.doge.constant.model.NetMusicSource;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author Doge
 * @Description 分类标签
 * @Date 2020/12/7
 */
public class Tags {
    public static Map<String, String[]> programSearchTag = Collections.synchronizedMap(new LinkedHashMap<>());

    public static Map<String, String[]> recPlaylistTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static int[] recPlaylistMap = {NetMusicSource.NC, NetMusicSource.KG, NetMusicSource.QQ, NetMusicSource.ME, NetMusicSource.FS};

    public static Map<String, String[]> hotPlaylistTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static int[] hotPlaylistMap = {NetMusicSource.NC, NetMusicSource.NC, NetMusicSource.KG, NetMusicSource.KG, NetMusicSource.QQ,
            NetMusicSource.KW, NetMusicSource.MG, NetMusicSource.QI, NetMusicSource.ME, NetMusicSource.ME, NetMusicSource.FS};

    public static Map<String, String[]> hotSongTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static int[] hotSongMap = {NetMusicSource.NC, NetMusicSource.KG, NetMusicSource.KG, NetMusicSource.KG,
            NetMusicSource.KG, NetMusicSource.HF, NetMusicSource.GG};

    public static Map<String, String[]> newSongTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static int[] newSongMap = {NetMusicSource.NC, NetMusicSource.NC, NetMusicSource.KG, NetMusicSource.KG,
            NetMusicSource.QQ, NetMusicSource.HF, NetMusicSource.GG, NetMusicSource.FS};

    public static Map<String, String[]> newAlbumTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static int[] newAlbumMap = {NetMusicSource.NC, NetMusicSource.NC, NetMusicSource.NC, NetMusicSource.KG,
            NetMusicSource.KG, NetMusicSource.QQ, NetMusicSource.DB, NetMusicSource.DT};

    public static Map<String, String[]> artistTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static int[] artistMap = {NetMusicSource.NC, NetMusicSource.NC, NetMusicSource.NC, NetMusicSource.KG, NetMusicSource.KG,
            NetMusicSource.QQ, NetMusicSource.KW, NetMusicSource.KW, NetMusicSource.QI, NetMusicSource.ME};

    public static Map<String, String[]> radioTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static int[] radioMap = {NetMusicSource.NC, NetMusicSource.NC, NetMusicSource.XM, NetMusicSource.XM, NetMusicSource.XM,
            NetMusicSource.ME, NetMusicSource.DB, NetMusicSource.DB};

    public static Map<String, String[]> programTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static int[] programMap = {NetMusicSource.ME, NetMusicSource.ME};

    public static Map<String, String[]> mvTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static int[] mvMap = {NetMusicSource.NC, NetMusicSource.NC, NetMusicSource.KG, NetMusicSource.KG, NetMusicSource.QQ, NetMusicSource.QQ,
            NetMusicSource.QQ, NetMusicSource.KW, NetMusicSource.HK, NetMusicSource.BI, NetMusicSource.FA, NetMusicSource.LZ};
}
