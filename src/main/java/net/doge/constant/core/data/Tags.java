package net.doge.constant.core.data;

import net.doge.constant.service.NetMusicSource;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Doge
 * @description 分类标签
 * @date 2020/12/7
 */
public class Tags {
    public static final Map<String, String[]> programSearchTag = Collections.synchronizedMap(new LinkedHashMap<>());

    public static final Map<String, String[]> recPlaylistTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static final int[] recPlaylistMap = {NetMusicSource.NC, NetMusicSource.KG, NetMusicSource.QQ, NetMusicSource.ME, NetMusicSource.FS};

    public static final Map<String, String[]> hotPlaylistTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static final int[] hotPlaylistMap = {NetMusicSource.NC, NetMusicSource.NC, NetMusicSource.KG, NetMusicSource.KG, NetMusicSource.QQ,
            NetMusicSource.KW, NetMusicSource.MG, NetMusicSource.QI, NetMusicSource.ME, NetMusicSource.ME, NetMusicSource.FS};

    public static final Map<String, String[]> hotSongTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static final int[] hotSongMap = {NetMusicSource.NC, NetMusicSource.KG, NetMusicSource.KG, NetMusicSource.KG,
            NetMusicSource.KG, NetMusicSource.HF, NetMusicSource.GG};

    public static final Map<String, String[]> newSongTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static final int[] newSongMap = {NetMusicSource.NC, NetMusicSource.NC, NetMusicSource.KG, NetMusicSource.KG,
            NetMusicSource.QQ, NetMusicSource.HF, NetMusicSource.GG, NetMusicSource.FS};

    public static final Map<String, String[]> newAlbumTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static final int[] newAlbumMap = {NetMusicSource.NC, NetMusicSource.NC, NetMusicSource.NC, NetMusicSource.KG,
            NetMusicSource.KG, NetMusicSource.QQ, NetMusicSource.DB, NetMusicSource.DT};

    public static final Map<String, String[]> artistTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static final int[] artistMap = {NetMusicSource.NC, NetMusicSource.NC, NetMusicSource.NC, NetMusicSource.KG, NetMusicSource.KG,
            NetMusicSource.QQ, NetMusicSource.KW, NetMusicSource.KW, NetMusicSource.MG, NetMusicSource.QI, NetMusicSource.ME};

    public static final Map<String, String[]> radioTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static final int[] radioMap = {NetMusicSource.NC, NetMusicSource.NC, NetMusicSource.XM, NetMusicSource.XM, NetMusicSource.XM,
            NetMusicSource.ME, NetMusicSource.DB, NetMusicSource.DB};

    public static final Map<String, String[]> programTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static final int[] programMap = {NetMusicSource.ME, NetMusicSource.ME};

    public static final Map<String, String[]> mvTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static final int[] mvMap = {NetMusicSource.NC, NetMusicSource.NC, NetMusicSource.KG, NetMusicSource.KG, NetMusicSource.QQ, NetMusicSource.QQ,
            NetMusicSource.QQ, NetMusicSource.KW, NetMusicSource.HK, NetMusicSource.BI, NetMusicSource.FA, NetMusicSource.LZ};
}
