package net.doge.constants;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author yzx
 * @Description 分类标签
 * @Date 2020/12/7
 */
public class Tags {
    public static Map<String, String[]> programSearchTag = Collections.synchronizedMap(new LinkedHashMap<>());

    public static Map<String, String[]> recPlaylistTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static int[] recPlaylistMap = {NetMusicSource.NET_CLOUD, NetMusicSource.KG, NetMusicSource.QQ, NetMusicSource.ME};

    public static Map<String, String[]> playlistTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static int[] playlistMap = {NetMusicSource.NET_CLOUD, NetMusicSource.NET_CLOUD, NetMusicSource.KG, NetMusicSource.QQ,
            NetMusicSource.KW, NetMusicSource.MG, NetMusicSource.QI, NetMusicSource.ME, NetMusicSource.ME};

    public static Map<String, String[]> hotSongTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static int[] hotSongMap = {NetMusicSource.NET_CLOUD, NetMusicSource.HF, NetMusicSource.GG};

    public static Map<String, String[]> newSongTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static int[] newSongMap = {NetMusicSource.NET_CLOUD, NetMusicSource.NET_CLOUD, NetMusicSource.KG, NetMusicSource.QQ, NetMusicSource.HF, NetMusicSource.GG};

    public static Map<String, String[]> newAlbumTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static int[] newAlbumMap = {NetMusicSource.NET_CLOUD, NetMusicSource.NET_CLOUD, NetMusicSource.NET_CLOUD,
            NetMusicSource.QQ, NetMusicSource.DB, NetMusicSource.DT};

    public static Map<String, String[]> artistTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static int[] artistMap = {NetMusicSource.NET_CLOUD, NetMusicSource.NET_CLOUD, NetMusicSource.NET_CLOUD, NetMusicSource.KG, NetMusicSource.QQ,
            NetMusicSource.KW, NetMusicSource.KW, NetMusicSource.QI, NetMusicSource.ME};

    public static Map<String, String[]> radioTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static int[] radioMap = {NetMusicSource.NET_CLOUD, NetMusicSource.NET_CLOUD, NetMusicSource.XM, NetMusicSource.XM, NetMusicSource.XM,
            NetMusicSource.ME, NetMusicSource.DB, NetMusicSource.DB};

    public static Map<String, String[]> programTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static int[] programMap = {NetMusicSource.ME, NetMusicSource.ME};

    public static Map<String, String[]> mvTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static int[] mvMap = {NetMusicSource.NET_CLOUD, NetMusicSource.NET_CLOUD, NetMusicSource.KG, NetMusicSource.QQ,
            NetMusicSource.QQ, NetMusicSource.QQ, NetMusicSource.KW, NetMusicSource.HK, NetMusicSource.BI};
}
