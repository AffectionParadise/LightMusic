package net.doge.constants;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author yzx
 * @Description 歌单分类
 * @Date 2020/12/7
 */
public class Tags {
    public static Map<String, String[]> programSearchTag = Collections.synchronizedMap(new LinkedHashMap<>());

    public static Map<String, String[]> recPlaylistTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static Map<String, String[]> playlistTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static Map<String, String[]> newSongTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static Map<String, String[]> newAlbumTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static Map<String, String[]> artistTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static Map<String, String[]> radioTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static Map<String, String[]> programTag = Collections.synchronizedMap(new LinkedHashMap<>());
    public static Map<String, String[]> mvTag = Collections.synchronizedMap(new LinkedHashMap<>());
}
