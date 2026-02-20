package net.doge.constant.service.tag;

import net.doge.constant.service.source.NetResourceSource;

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
    public static final int[] recPlaylistIndices = {NetResourceSource.NC, NetResourceSource.KG, NetResourceSource.QQ, NetResourceSource.ME, NetResourceSource.FS};

    public static final Map<String, String[]> hotPlaylistTags = Collections.synchronizedMap(new LinkedHashMap<>());
    public static final int[] hotPlaylistIndices = {NetResourceSource.NC, NetResourceSource.NC, NetResourceSource.KG, NetResourceSource.KG, NetResourceSource.QQ,
            NetResourceSource.KW, NetResourceSource.MG, NetResourceSource.QI, NetResourceSource.ME, NetResourceSource.ME, NetResourceSource.FS};

    public static final Map<String, String[]> hotSongTags = Collections.synchronizedMap(new LinkedHashMap<>());
    public static final int[] hotSongIndices = {NetResourceSource.NC, NetResourceSource.KG, NetResourceSource.KG, NetResourceSource.KG,
            NetResourceSource.KG, NetResourceSource.HF, NetResourceSource.GG};

    public static final Map<String, String[]> newSongTags = Collections.synchronizedMap(new LinkedHashMap<>());
    public static final int[] newSongIndices = {NetResourceSource.NC, NetResourceSource.NC, NetResourceSource.KG, NetResourceSource.KG,
            NetResourceSource.QQ, NetResourceSource.HF, NetResourceSource.GG, NetResourceSource.FS};

    public static final Map<String, String[]> newAlbumTags = Collections.synchronizedMap(new LinkedHashMap<>());
    public static final int[] newAlbumIndices = {NetResourceSource.NC, NetResourceSource.NC, NetResourceSource.NC, NetResourceSource.KG,
            NetResourceSource.KG, NetResourceSource.QQ, NetResourceSource.DB, NetResourceSource.DT};

    public static final Map<String, String[]> artistTags = Collections.synchronizedMap(new LinkedHashMap<>());
    public static final int[] artistIndices = {NetResourceSource.NC, NetResourceSource.NC, NetResourceSource.NC, NetResourceSource.KG, NetResourceSource.KG,
            NetResourceSource.QQ, NetResourceSource.KW, NetResourceSource.KW, NetResourceSource.MG, NetResourceSource.QI, NetResourceSource.ME};

    public static final Map<String, String[]> radioTags = Collections.synchronizedMap(new LinkedHashMap<>());
    public static final int[] radioIndices = {NetResourceSource.NC, NetResourceSource.NC, NetResourceSource.XM, NetResourceSource.XM, NetResourceSource.XM,
            NetResourceSource.ME, NetResourceSource.DB, NetResourceSource.DB, NetResourceSource.MG};

    public static final Map<String, String[]> programTags = Collections.synchronizedMap(new LinkedHashMap<>());
    public static final int[] programIndices = {NetResourceSource.ME, NetResourceSource.ME};

    public static final Map<String, String[]> mvTags = Collections.synchronizedMap(new LinkedHashMap<>());
    public static final int[] mvIndices = {NetResourceSource.NC, NetResourceSource.NC, NetResourceSource.KG, NetResourceSource.KG, NetResourceSource.QQ, NetResourceSource.QQ,
            NetResourceSource.QQ, NetResourceSource.KW, NetResourceSource.HK, NetResourceSource.BI, NetResourceSource.FA, NetResourceSource.LZ};
}
