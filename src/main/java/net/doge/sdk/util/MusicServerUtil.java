package net.doge.sdk.util;

import net.doge.model.entity.*;
import net.doge.model.entity.base.NetResource;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.core.DownloadReq;
import net.doge.sdk.core.MottoReq;
import net.doge.sdk.core.listener.DownloadListener;
import net.doge.sdk.entity.album.info.AlbumInfoReq;
import net.doge.sdk.entity.album.menu.AlbumMenuReq;
import net.doge.sdk.entity.album.rcmd.NewAlbumReq;
import net.doge.sdk.entity.album.search.AlbumSearchReq;
import net.doge.sdk.entity.album.tag.NewAlbumTagReq;
import net.doge.sdk.entity.artist.info.ArtistInfoReq;
import net.doge.sdk.entity.artist.menu.ArtistMenuReq;
import net.doge.sdk.entity.artist.rcmd.ArtistListReq;
import net.doge.sdk.entity.artist.search.ArtistSearchReq;
import net.doge.sdk.entity.artist.tag.ArtistTagReq;
import net.doge.sdk.entity.comment.CommentReq;
import net.doge.sdk.entity.music.info.MusicInfoReq;
import net.doge.sdk.entity.music.info.MusicUrlReq;
import net.doge.sdk.entity.music.menu.MusicMenuReq;
import net.doge.sdk.entity.music.rcmd.HotMusicRecommendReq;
import net.doge.sdk.entity.music.rcmd.NewMusicReq;
import net.doge.sdk.entity.music.rcmd.RecommendProgramReq;
import net.doge.sdk.entity.music.search.HotSearchReq;
import net.doge.sdk.entity.music.search.MusicSearchReq;
import net.doge.sdk.entity.music.search.SearchSuggestionReq;
import net.doge.sdk.entity.music.tag.HotSongTagReq;
import net.doge.sdk.entity.music.tag.MusicSearchTagReq;
import net.doge.sdk.entity.music.tag.NewSongTagReq;
import net.doge.sdk.entity.music.tag.ProgramTagReq;
import net.doge.sdk.entity.mv.info.MvInfoReq;
import net.doge.sdk.entity.mv.menu.MvMenuReq;
import net.doge.sdk.entity.mv.rcmd.RecommendMvReq;
import net.doge.sdk.entity.mv.search.MvSearchReq;
import net.doge.sdk.entity.mv.tag.MvTagReq;
import net.doge.sdk.entity.playlist.info.PlaylistInfoReq;
import net.doge.sdk.entity.playlist.menu.PlaylistMenuReq;
import net.doge.sdk.entity.playlist.rcmd.HighQualityPlaylistReq;
import net.doge.sdk.entity.playlist.rcmd.RecommendPlaylistReq;
import net.doge.sdk.entity.playlist.search.PlaylistSearchReq;
import net.doge.sdk.entity.playlist.tag.HotPlaylistTagReq;
import net.doge.sdk.entity.playlist.tag.RecPlaylistTagReq;
import net.doge.sdk.entity.radio.info.RadioInfoReq;
import net.doge.sdk.entity.radio.menu.RadioMenuReq;
import net.doge.sdk.entity.radio.rcmd.HotRadioReq;
import net.doge.sdk.entity.radio.rcmd.NewRadioReq;
import net.doge.sdk.entity.radio.search.RadioSearchReq;
import net.doge.sdk.entity.radio.tag.HotRadioTagReq;
import net.doge.sdk.entity.ranking.info.RankingInfoReq;
import net.doge.sdk.entity.ranking.search.RankingSearchReq;
import net.doge.sdk.entity.sheet.SheetReq;
import net.doge.sdk.entity.user.info.UserInfoReq;
import net.doge.sdk.entity.user.menu.UserMenuReq;
import net.doge.sdk.entity.user.search.UserSearchReq;

import java.util.Map;
import java.util.Set;

/**
 * @Author Doge
 * @description
 * @date 2020/12/19
 */
public class MusicServerUtil {
    /**
     * 加载节目搜索子标签
     *
     * @return
     */
    public static void initProgramSearchTag() {
        MusicSearchTagReq.getInstance().initProgramSearchTag();
    }

    /**
     * 加载推荐歌单标签
     *
     * @return
     */
    public static void initRecPlaylistTag() {
        RecPlaylistTagReq.getInstance().initRecPlaylistTag();
    }

    /**
     * 加载歌单标签
     *
     * @return
     */
    public static void initPlaylistTag() {
        HotPlaylistTagReq.getInstance().initHotPlaylistTag();
    }

    /**
     * 加载飙升歌曲标签
     *
     * @return
     */
    public static void initHotSongTag() {
        HotSongTagReq.getInstance().initHotSongTag();
    }

    /**
     * 加载新歌标签
     *
     * @return
     */
    public static void initNewSongTag() {
        NewSongTagReq.getInstance().initNewSongTag();
    }

    /**
     * 加载新碟标签
     *
     * @return
     */
    public static void initNewAlbumTag() {
        NewAlbumTagReq.getInstance().initNewAlbumTag();
    }

    /**
     * 加载歌手标签
     *
     * @return
     */
    public static void initArtistTag() {
        ArtistTagReq.getInstance().initArtistTag();
    }

    /**
     * 加载电台标签
     *
     * @return
     */
    public static void initRadioTag() {
        HotRadioTagReq.getInstance().initRadioTag();
    }

    /**
     * 加载节目标签
     *
     * @return
     */
    public static void initProgramTag() {
        ProgramTagReq.getInstance().initProgramTag();
    }

    /**
     * 加载 MV 标签
     *
     * @return
     */
    public static void initMvTag() {
        MvTagReq.getInstance().initMvTag();
    }

    /**
     * 获取格言
     *
     * @return
     */
    public static String getMotto() {
        return MottoReq.getInstance().getMotto();
    }

    /**
     * 获取热搜
     *
     * @return
     */
    public static Set<String> getHotSearch() {
        return HotSearchReq.getInstance().getHotSearch();
    }

    /**
     * 获取搜索建议
     *
     * @return
     */
    public static Set<String> getSearchSuggestion(String keyword) {
        return SearchSuggestionReq.getInstance().getSearchSuggestion(keyword);
    }

    /**
     * 根据关键词获取歌曲
     */
    public static CommonResult<NetMusicInfo> searchMusic(int src, int type, String subType, String keyword, int page, int limit) {
        return MusicSearchReq.getInstance().searchMusic(src, type, subType, keyword, page, limit);
    }

    /**
     * 补充 NetMusicInfo 歌曲信息(包括 时长、专辑名称、封面图片、歌词)
     */
    public static void fillMusicInfo(NetMusicInfo musicInfo) {
        MusicInfoReq.getInstance().fillMusicInfo(musicInfo);
    }

    /**
     * 补充 NetMusicInfo 的 url
     */
    public static void fillMusicUrl(NetMusicInfo musicInfo) {
        MusicUrlReq.getInstance().fillMusicUrl(musicInfo);
    }

    /**
     * 根据关键词获取歌单
     */
    public static CommonResult<NetPlaylistInfo> searchPlaylists(int src, String keyword, int page, int limit) {
        return PlaylistSearchReq.getInstance().searchPlaylists(src, keyword, page, limit);
    }

    /**
     * 根据关键词获取专辑
     */
    public static CommonResult<NetAlbumInfo> searchAlbums(int src, String keyword, int page, int limit) {
        return AlbumSearchReq.getInstance().searchAlbums(src, keyword, page, limit);
    }

    /**
     * 根据关键词获取歌手
     */
    public static CommonResult<NetArtistInfo> searchArtists(int src, String keyword, int page, int limit) {
        return ArtistSearchReq.getInstance().searchArtists(src, keyword, page, limit);
    }

    /**
     * 根据关键词获取电台
     */
    public static CommonResult<NetRadioInfo> searchRadios(int src, String keyword, int page, int limit) {
        return RadioSearchReq.getInstance().searchRadios(src, keyword, page, limit);
    }

    /**
     * 根据关键词获取 MV
     */
    public static CommonResult<NetMvInfo> searchMvs(int src, String keyword, int page, int limit, String cursor) {
        return MvSearchReq.getInstance().searchMvs(src, keyword, page, limit, cursor);
    }

    /**
     * 获取所有榜单
     */
    public static CommonResult<NetRankingInfo> getRankings(int src) {
        return RankingSearchReq.getInstance().getRankings(src);
    }

    /**
     * 根据关键词获取用户
     */
    public static CommonResult<NetUserInfo> searchUsers(int src, String keyword, int page, int limit) {
        return UserSearchReq.getInstance().searchUsers(src, keyword, page, limit);
    }

    /**
     * 获取 歌曲 / 歌单 / 专辑 / MV 评论
     */
    public static CommonResult<NetCommentInfo> getComments(NetResource resource, String type, int page, int limit, String cursor) {
        return CommentReq.getInstance().getComments(resource, type, page, limit, cursor);
    }

    /**
     * 获取歌曲乐谱
     */
    public static CommonResult<NetSheetInfo> getSheets(NetMusicInfo musicInfo) {
        return SheetReq.getInstance().getSheets(musicInfo);
    }

    /**
     * 获取乐谱图片链接
     */
    public static CommonResult<String> getSheetImgUrls(NetSheetInfo sheetInfo) {
        return SheetReq.getInstance().getSheetImgUrls(sheetInfo);
    }

    /**
     * 获取专辑照片链接
     */
    public static CommonResult<String> getAlbumImgUrls(NetAlbumInfo albumInfo, int page, int limit, String cursor) {
        return AlbumInfoReq.getInstance().getAlbumImgUrls(albumInfo, page, limit, cursor);
    }

    /**
     * 获取歌手照片链接
     */
    public static CommonResult<String> getArtistImgUrls(NetArtistInfo artistInfo, int page) {
        return ArtistMenuReq.getInstance().getArtistImgUrls(artistInfo, page);
    }

    /**
     * 获取电台照片链接
     */
    public static CommonResult<String> getRadioImgUrls(NetRadioInfo radioInfo, int page) {
        return RadioInfoReq.getInstance().getRadioImgUrls(radioInfo, page);
    }

    /**
     * 获取电台海报链接
     */
    public static CommonResult<String> getRadioPosterUrls(NetRadioInfo radioInfo, int page) {
        return RadioInfoReq.getInstance().getRadioPosterUrls(radioInfo, page);
    }

    /**
     * 获取推荐歌单
     */
    public static CommonResult<NetPlaylistInfo> getRecommendPlaylists(int src, String tag, int page, int limit) {
        return RecommendPlaylistReq.getInstance().getRecommendPlaylists(src, tag, page, limit);
    }

    /**
     * 获取精品歌单 + 网友精选碟，分页
     */
    public static CommonResult<NetPlaylistInfo> getHighQualityPlaylists(int src, String tag, int page, int limit) {
        return HighQualityPlaylistReq.getInstance().getHighQualityPlaylists(src, tag, page, limit);
    }

    /**
     * 获取歌手排行
     */
    public static CommonResult<NetArtistInfo> getArtistLists(int src, String tag, int page, int limit) {
        return ArtistListReq.getInstance().getArtistLists(src, tag, page, limit);
    }

    /**
     * 获取新晋电台
     */
    public static CommonResult<NetRadioInfo> getNewRadios(int src, int page, int limit) {
        return NewRadioReq.getInstance().getNewRadios(src, page, limit);
    }

    /**
     * 获取个性电台 + 今日优选 + 热门电台 + 热门电台榜
     */
    public static CommonResult<NetRadioInfo> getHotRadios(int src, String tag, int page, int limit) {
        return HotRadioReq.getInstance().getHotRadios(src, tag, page, limit);
    }

    /**
     * 获取推荐节目
     */
    public static CommonResult<NetMusicInfo> getRecommendPrograms(int src, String tag, int page, int limit) {
        return RecommendProgramReq.getInstance().getRecommendPrograms(src, tag, page, limit);
    }

    /**
     * 获取飙升歌曲
     */
    public static CommonResult<NetMusicInfo> getHotMusicRecommend(int src, String tag, int page, int limit) {
        return HotMusicRecommendReq.getInstance().getHotMusicRecommend(src, tag, page, limit);
    }

    /**
     * 获取推荐歌曲 + 新歌速递
     */
    public static CommonResult<NetMusicInfo> getNewMusic(int src, String tag, int page, int limit) {
        return NewMusicReq.getInstance().getNewMusic(src, tag, page, limit);
    }

    /**
     * 获取新碟上架
     */
    public static CommonResult<NetAlbumInfo> getNewAlbums(int src, String tag, int page, int limit) {
        return NewAlbumReq.getInstance().getNewAlbums(src, tag, page, limit);
    }

    /**
     * 获取 MV 排行 + 最新 MV + 推荐 MV
     */
    public static CommonResult<NetMvInfo> getRecommendMvs(int src, String tag, int page, int limit) {
        return RecommendMvReq.getInstance().getRecommendMvs(src, tag, page, limit);
    }

    /**
     * 根据歌单 id 和 source 预加载歌单信息
     */
    public static void preloadPlaylistInfo(NetPlaylistInfo playlistInfo) {
        PlaylistInfoReq.getInstance().preloadPlaylistInfo(playlistInfo);
    }

    /**
     * 根据歌单 id 获取歌单
     */
    public static CommonResult<NetPlaylistInfo> getPlaylistInfo(int source, String id) {
        return PlaylistInfoReq.getInstance().getPlaylistInfo(source, id);
    }

    /**
     * 根据歌单 id 补全歌单信息(包括封面图、描述)
     */
    public static void fillPlaylistInfo(NetPlaylistInfo playlistInfo) {
        PlaylistInfoReq.getInstance().fillPlaylistInfo(playlistInfo);
    }

    /**
     * 根据专辑 id 预加载专辑信息
     */
    public static void preloadAlbumInfo(NetAlbumInfo albumInfo) {
        AlbumInfoReq.getInstance().preloadAlbumInfo(albumInfo);
    }

    /**
     * 根据专辑 id 获取专辑
     */
    public static CommonResult<NetAlbumInfo> getAlbumInfo(int source, String id) {
        return AlbumInfoReq.getInstance().getAlbumInfo(source, id);
    }

    /**
     * 根据专辑 id 补全专辑信息(包括封面图、描述)
     */
    public static void fillAlbumInfo(NetAlbumInfo albumInfo) {
        AlbumInfoReq.getInstance().fillAlbumInfo(albumInfo);
    }

    /**
     * 根据歌手 id 预加载歌手信息
     */
    public static void preloadArtistInfo(NetArtistInfo artistInfo) {
        ArtistInfoReq.getInstance().preloadArtistInfo(artistInfo);
    }

    /**
     * 根据歌手 id 获取歌手
     */
    public static CommonResult<NetArtistInfo> getArtistInfo(int source, String id) {
        return ArtistInfoReq.getInstance().getArtistInfo(source, id);
    }

    /**
     * 根据歌手 id 补全歌手信息(包括封面图、描述)
     */
    public static void fillArtistInfo(NetArtistInfo artistInfo) {
        ArtistInfoReq.getInstance().fillArtistInfo(artistInfo);
    }

    /**
     * 根据电台 id 预加载电台信息
     */
    public static void preloadRadioInfo(NetRadioInfo radioInfo) {
        RadioInfoReq.getInstance().preloadRadioInfo(radioInfo);
    }

    /**
     * 根据电台 id 获取电台
     */
    public static CommonResult<NetRadioInfo> getRadioInfo(int source, String id) {
        return RadioInfoReq.getInstance().getRadioInfo(source, id);
    }

    /**
     * 根据电台 id 补全电台信息(包括封面图、描述)
     */
    public static void fillRadioInfo(NetRadioInfo radioInfo) {
        RadioInfoReq.getInstance().fillRadioInfo(radioInfo);
    }

    /**
     * 根据 MV id 预加载 MV 信息
     */
    public static void preloadMvInfo(NetMvInfo mvInfo) {
        MvInfoReq.getInstance().preloadMvInfo(mvInfo);
    }

    /**
     * 根据 MV id 补全 MV 信息(只包含 url)
     */
    public static void fillMvInfo(NetMvInfo mvInfo) {
        MvInfoReq.getInstance().fillMvInfo(mvInfo);
    }

    /**
     * 根据 MV id 补全 MV 基本信息
     */
    public static void fillMvDetail(NetMvInfo mvInfo) {
        MvInfoReq.getInstance().fillMvDetail(mvInfo);
    }

    /**
     * 根据榜单 id 预加载榜单信息(包括封面图)
     */
    public static void preloadRankingInfo(NetRankingInfo rankingInfo) {
        RankingInfoReq.getInstance().preloadRankingInfo(rankingInfo);
    }

    /**
     * 根据榜单 id 补全榜单信息(包括封面图)
     */
    public static void fillRankingInfo(NetRankingInfo rankingInfo) {
        RankingInfoReq.getInstance().fillRankingInfo(rankingInfo);
    }

    /**
     * 根据用户 id 预加载用户信息
     */
    public static void preloadUserInfo(NetUserInfo userInfo) {
        UserInfoReq.getInstance().preloadUserInfo(userInfo);
    }

    /**
     * 根据用户 id 补全用户信息(包括封面图、描述)
     */
    public static void fillUserInfo(NetUserInfo userInfo) {
        UserInfoReq.getInstance().fillUserInfo(userInfo);
    }

    /**
     * 根据歌单 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public static CommonResult<NetMusicInfo> getMusicInfoInPlaylist(NetPlaylistInfo playlistInfo, int page, int limit) {
        return PlaylistInfoReq.getInstance().getMusicInfoInPlaylist(playlistInfo, page, limit);
    }

    /**
     * 根据专辑 id 获取里面歌曲的粗略信息，分页，返回 NetMusicInfo
     */
    public static CommonResult<NetMusicInfo> getMusicInfoInAlbum(NetAlbumInfo albumInfo, int page, int limit) {
        return AlbumInfoReq.getInstance().getMusicInfoInAlbum(albumInfo, page, limit);
    }

    /**
     * 根据歌手 id 获取里面歌曲的粗略信息，分页，返回 NetMusicInfo
     */
    public static CommonResult<NetMusicInfo> getMusicInfoInArtist(NetArtistInfo artistInfo, int page, int limit) {
        return ArtistInfoReq.getInstance().getMusicInfoInArtist(artistInfo, page, limit);
    }

    /**
     * 根据歌手 id 获取里面专辑的粗略信息，分页，返回 NetAlbumInfo
     */
    public static CommonResult<NetAlbumInfo> getAlbumInfoInArtist(NetArtistInfo artistInfo, int page, int limit) {
        return ArtistMenuReq.getInstance().getAlbumInfoInArtist(artistInfo, page, limit);
    }

    /**
     * 根据歌手 id 获取里面 MV 的粗略信息，分页，返回 NetMvInfo
     */
    public static CommonResult<NetMvInfo> getMvInfoInArtist(NetArtistInfo artistInfo, int page, int limit) {
        return ArtistMenuReq.getInstance().getMvInfoInArtist(artistInfo, page, limit);
    }

    /**
     * 根据电台 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public static CommonResult<NetMusicInfo> getMusicInfoInRadio(NetRadioInfo radioInfo, int sortType, int page, int limit) {
        return RadioInfoReq.getInstance().getMusicInfoInRadio(radioInfo, sortType, page, limit);
    }

    /**
     * 根据榜单 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public static CommonResult<NetMusicInfo> getMusicInfoInRanking(String rankingId, int source, int page, int limit) {
        return RankingInfoReq.getInstance().getMusicInfoInRanking(rankingId, source, page, limit);
    }

    /**
     * 根据用户 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public static CommonResult<NetMusicInfo> getMusicInfoInUser(int recordType, NetUserInfo userInfo, int page, int limit) {
        return UserInfoReq.getInstance().getMusicInfoInUser(recordType, userInfo, page, limit);
    }

    /**
     * 获取相似歌曲
     *
     * @return
     */
    public static CommonResult<NetMusicInfo> getSimilarSongs(NetMusicInfo musicInfo) {
        return MusicMenuReq.getInstance().getSimilarSongs(musicInfo);
    }

    /**
     * 获取用户歌单（通过评论）
     *
     * @return
     */
    public static CommonResult<NetPlaylistInfo> getUserPlaylists(NetCommentInfo commentInfo, int page, int limit) {
        return UserMenuReq.getInstance().getUserPlaylists(commentInfo, page, limit);
    }

    /**
     * 获取用户专辑（通过评论）
     *
     * @return
     */
    public static CommonResult<NetAlbumInfo> getUserAlbums(NetCommentInfo commentInfo, int page, int limit) {
        return UserMenuReq.getInstance().getUserAlbums(commentInfo, page, limit);
    }

    /**
     * 获取用户歌单（通过用户）
     *
     * @return
     */
    public static CommonResult<NetPlaylistInfo> getUserPlaylists(NetUserInfo userInfo, int page, int limit) {
        return UserMenuReq.getInstance().getUserPlaylists(userInfo, page, limit);
    }

    /**
     * 获取用户专辑（通过用户）
     *
     * @return
     */
    public static CommonResult<NetAlbumInfo> getUserAlbums(NetUserInfo userInfo, int page, int limit) {
        return UserMenuReq.getInstance().getUserAlbums(userInfo, page, limit);
    }

    /**
     * 获取用户电台（通过用户）
     *
     * @return
     */
    public static CommonResult<NetRadioInfo> getUserRadios(NetUserInfo userInfo, int page, int limit) {
        return UserMenuReq.getInstance().getUserRadios(userInfo, page, limit);
    }

    /**
     * 获取用户视频 (通过用户)
     *
     * @return
     */
    public static CommonResult<NetMvInfo> getUserVideos(NetUserInfo userInfo, int sortType, int page, int limit, String cursor) {
        return UserMenuReq.getInstance().getUserVideos(userInfo, sortType, page, limit, cursor);
    }

    /**
     * 获取相关歌单（通过歌曲）
     *
     * @return
     */
    public static CommonResult<NetPlaylistInfo> getRelatedPlaylists(NetMusicInfo musicInfo) {
        return MusicMenuReq.getInstance().getRelatedPlaylists(musicInfo);
    }

    /**
     * 获取相关歌单（通过歌单）
     *
     * @return
     */
    public static CommonResult<NetPlaylistInfo> getSimilarPlaylists(NetPlaylistInfo playlistInfo) {
        return PlaylistMenuReq.getInstance().getSimilarPlaylists(playlistInfo);
    }

    /**
     * 获取相关 MV (通过歌曲)
     *
     * @return
     */
    public static CommonResult<NetMvInfo> getRelatedMvs(NetMusicInfo musicInfo, int page, int limit) {
        return MvMenuReq.getInstance().getRelatedMvs(musicInfo, page, limit);
    }

    /**
     * 获取相似 MV (通过 MV)
     *
     * @return
     */
    public static CommonResult<NetMvInfo> getSimilarMvs(NetMvInfo mvInfo) {
        return MvMenuReq.getInstance().getSimilarMvs(mvInfo);
    }

    /**
     * 获取视频分集
     *
     * @return
     */
    public static CommonResult<NetMvInfo> getVideoEpisodes(NetMvInfo mvInfo, int page, int limit) {
        return MvMenuReq.getInstance().getVideoEpisodes(mvInfo, page, limit);
    }

    /**
     * 获取相似专辑
     *
     * @return
     */
    public static CommonResult<NetAlbumInfo> getSimilarAlbums(NetAlbumInfo albumInfo) {
        return AlbumMenuReq.getInstance().getSimilarAlbums(albumInfo);
    }

    /**
     * 获取相似歌手 (通过歌手)
     *
     * @return
     */
    public static CommonResult<NetArtistInfo> getSimilarArtists(NetArtistInfo artistInfo) {
        return ArtistMenuReq.getInstance().getSimilarArtists(artistInfo);
    }

    /**
     * 获取歌单收藏者
     *
     * @return
     */
    public static CommonResult<NetUserInfo> getPlaylistSubscribers(NetPlaylistInfo playlistInfo, int page, int limit) {
        return PlaylistMenuReq.getInstance().getPlaylistSubscribers(playlistInfo, page, limit);
    }

    /**
     * 获取歌手粉丝
     *
     * @return
     */
    public static CommonResult<NetUserInfo> getArtistFans(NetArtistInfo artistInfo, int page, int limit) {
        return ArtistMenuReq.getInstance().getArtistFans(artistInfo, page, limit);
    }

    /**
     * 获取歌手合作人
     *
     * @return
     */
    public static CommonResult<NetArtistInfo> getArtistBuddies(NetArtistInfo artistInfo, int page, int limit) {
        return ArtistMenuReq.getInstance().getArtistBuddies(artistInfo, page, limit);
    }

    /**
     * 获取歌手电台
     *
     * @return
     */
    public static CommonResult<NetRadioInfo> getArtistRadios(NetArtistInfo artistInfo, int page, int limit) {
        return ArtistMenuReq.getInstance().getArtistRadios(artistInfo, page, limit);
    }

    /**
     * 获取电台订阅者
     *
     * @return
     */
    public static CommonResult<NetUserInfo> getRadioSubscribers(NetRadioInfo radioInfo, int page, int limit) {
        return RadioMenuReq.getInstance().getRadioSubscribers(radioInfo, page, limit);
    }

    /**
     * 获取推荐电台
     *
     * @return
     */
    public static CommonResult<NetRadioInfo> getRecRadios(NetMusicInfo musicInfo) {
        return MusicMenuReq.getInstance().getRecRadios(musicInfo);
    }

    /**
     * 获取相似电台
     *
     * @return
     */
    public static CommonResult<NetRadioInfo> getSimilarRadios(NetRadioInfo radioInfo) {
        return RadioMenuReq.getInstance().getSimilarRadios(radioInfo);
    }

    /**
     * 获取电台演职员
     *
     * @return
     */
    public static CommonResult<NetArtistInfo> getRadioArtists(NetRadioInfo radioInfo) {
        return RadioMenuReq.getInstance().getRadioArtists(radioInfo);
    }

    /**
     * 获取用户实体 (通过用户 id)
     *
     * @return
     */
    public static CommonResult<NetUserInfo> getUserInfo(int source, String id) {
        return UserInfoReq.getInstance().getUserInfo(source, id);
    }

    /**
     * 获取用户关注 (通过用户)
     *
     * @return
     */
    public static CommonResult<NetUserInfo> getUserFollows(NetUserInfo userInfo, int page, int limit) {
        return UserMenuReq.getInstance().getUserFollows(userInfo, page, limit);
    }

    /**
     * 获取用户粉丝 (通过用户)
     *
     * @return
     */
    public static CommonResult<NetUserInfo> getUserFans(NetUserInfo userInfo, int page, int limit) {
        return UserMenuReq.getInstance().getUserFans(userInfo, page, limit);
    }

    /**
     * 根据为 NetMusicInfo 填充歌词字符串（包括原文、翻译、罗马音），没有的部分填充 ""
     */
    public static void fillLrc(NetMusicInfo musicInfo) {
        MusicInfoReq.getInstance().fillLrc(musicInfo);
    }

    /**
     * 下载文件
     *
     * @param url
     * @param dest
     * @throws Exception
     */
    public static void download(String url, String dest, Map<String, String> headers) throws Exception {
        download(url, dest, headers, null);
    }

    /**
     * 下载文件
     *
     * @param url
     * @param dest
     * @throws Exception
     */
    public static void download(String url, String dest, DownloadListener listener) throws Exception {
        download(url, dest, null, listener);
    }

    /**
     * 下载文件，监听下载进度
     *
     * @param url
     * @param dest
     * @param headers
     * @param listener
     * @throws Exception
     */
    public static void download(String url, String dest, Map<String, String> headers, DownloadListener listener) throws Exception {
        DownloadReq.getInstance().download(url, dest, headers, listener);
    }
}
