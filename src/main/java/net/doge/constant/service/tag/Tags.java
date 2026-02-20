package net.doge.constant.service.tag;

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
    public static final Map<String, String[]> programSearchTags = Collections.synchronizedMap(new LinkedHashMap<>());

    public static final Map<String, String[]> recPlaylistTags = Collections.synchronizedMap(new LinkedHashMap<>());
    public static final int[] recPlaylistIndices = {NetMusicSource.NC, NetMusicSource.KG, NetMusicSource.QQ, NetMusicSource.ME, NetMusicSource.FS};

    public static final Map<String, String[]> hotPlaylistTags = Collections.synchronizedMap(new LinkedHashMap<>());
    public static final int[] hotPlaylistIndices = {NetMusicSource.NC, NetMusicSource.NC, NetMusicSource.KG, NetMusicSource.KG, NetMusicSource.QQ,
            NetMusicSource.KW, NetMusicSource.MG, NetMusicSource.QI, NetMusicSource.ME, NetMusicSource.ME, NetMusicSource.FS};

    public static final Map<String, String[]> hotSongTags = Collections.synchronizedMap(new LinkedHashMap<>());
    public static final int[] hotSongIndices = {NetMusicSource.NC, NetMusicSource.KG, NetMusicSource.KG, NetMusicSource.KG,
            NetMusicSource.KG, NetMusicSource.HF, NetMusicSource.GG};

    public static final Map<String, String[]> newSongTags = Collections.synchronizedMap(new LinkedHashMap<>());
    public static final int[] newSongIndices = {NetMusicSource.NC, NetMusicSource.NC, NetMusicSource.KG, NetMusicSource.KG,
            NetMusicSource.QQ, NetMusicSource.HF, NetMusicSource.GG, NetMusicSource.FS};

    public static final Map<String, String[]> newAlbumTags = Collections.synchronizedMap(new LinkedHashMap<>());
    public static final int[] newAlbumIndices = {NetMusicSource.NC, NetMusicSource.NC, NetMusicSource.NC, NetMusicSource.KG,
            NetMusicSource.KG, NetMusicSource.QQ, NetMusicSource.DB, NetMusicSource.DT};

    public static final Map<String, String[]> artistTags = Collections.synchronizedMap(new LinkedHashMap<>());
    public static final int[] artistIndices = {NetMusicSource.NC, NetMusicSource.NC, NetMusicSource.NC, NetMusicSource.KG, NetMusicSource.KG,
            NetMusicSource.QQ, NetMusicSource.KW, NetMusicSource.KW, NetMusicSource.MG, NetMusicSource.QI, NetMusicSource.ME};

    public static final Map<String, String[]> radioTags = Collections.synchronizedMap(new LinkedHashMap<>());
    public static final int[] radioIndices = {NetMusicSource.NC, NetMusicSource.NC, NetMusicSource.XM, NetMusicSource.XM, NetMusicSource.XM,
            NetMusicSource.ME, NetMusicSource.DB, NetMusicSource.DB, NetMusicSource.MG};

    public static final Map<String, String[]> programTags = Collections.synchronizedMap(new LinkedHashMap<>());
    public static final int[] programIndices = {NetMusicSource.ME, NetMusicSource.ME};

    public static final Map<String, String[]> mvTags = Collections.synchronizedMap(new LinkedHashMap<>());
    public static final int[] mvIndices = {NetMusicSource.NC, NetMusicSource.NC, NetMusicSource.KG, NetMusicSource.KG, NetMusicSource.QQ, NetMusicSource.QQ,
            NetMusicSource.QQ, NetMusicSource.KW, NetMusicSource.HK, NetMusicSource.BI, NetMusicSource.FA, NetMusicSource.LZ};
}
