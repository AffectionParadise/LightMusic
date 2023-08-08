package net.doge.sdk.util;

import net.doge.model.entity.*;
import net.doge.model.entity.base.NetResource;
import net.doge.sdk.common.CommonResult;
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
import net.doge.sdk.entity.mv.info.MvUrlReq;
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
import net.doge.sdk.entity.radio.tag.HotRadioTag;
import net.doge.sdk.entity.ranking.info.RankingInfoReq;
import net.doge.sdk.entity.ranking.search.RankingSearchReq;
import net.doge.sdk.entity.sheet.SheetReq;
import net.doge.sdk.entity.user.info.UserInfoReq;
import net.doge.sdk.entity.user.menu.UserMenuReq;
import net.doge.sdk.entity.user.search.UserSearchReq;
import net.doge.sdk.system.DownloadReq;
import net.doge.sdk.system.MottoReq;
import net.doge.sdk.system.listener.DownloadListener;

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
        new MusicSearchTagReq().initProgramSearchTag();
    }

    /**
     * 加载推荐歌单标签
     *
     * @return
     */
    public static void initRecPlaylistTag() {
        new RecPlaylistTagReq().initRecPlaylistTag();
    }

    /**
     * 加载歌单标签
     *
     * @return
     */
    public static void initPlaylistTag() {
        new HotPlaylistTagReq().initHotPlaylistTag();
    }

    /**
     * 加载飙升歌曲标签
     *
     * @return
     */
    public static void initHotSongTag() {
        new HotSongTagReq().initHotSongTag();
    }

    /**
     * 加载新歌标签
     *
     * @return
     */
    public static void initNewSongTag() {
        new NewSongTagReq().initNewSongTag();
    }

    /**
     * 加载新碟标签
     *
     * @return
     */
    public static void initNewAlbumTag() {
        new NewAlbumTagReq().initNewAlbumTag();
    }

    /**
     * 加载歌手标签
     *
     * @return
     */
    public static void initArtistTag() {
        new ArtistTagReq().initArtistTag();
    }

    /**
     * 加载电台标签
     *
     * @return
     */
    public static void initRadioTag() {
        new HotRadioTag().initRadioTag();
    }

    /**
     * 加载节目标签
     *
     * @return
     */
    public static void initProgramTag() {
        new ProgramTagReq().initProgramTag();
    }

    /**
     * 加载 MV 标签
     *
     * @return
     */
    public static void initMvTag() {
        new MvTagReq().initMvTag();
    }

    /**
     * 获取格言
     *
     * @return
     */
    public static String getMotto() {
        return new MottoReq().getMotto();
    }

    /**
     * 获取热搜
     *
     * @return
     */
    public static Set<String> getHotSearch() {
        return new HotSearchReq().getHotSearch();
    }

    /**
     * 获取搜索建议
     *
     * @return
     */
    public static Set<String> getSearchSuggestion(String keyword) {
        return new SearchSuggestionReq().getSearchSuggestion(keyword);
    }

    /**
     * 根据关键词获取歌曲
     */
    public static CommonResult<NetMusicInfo> searchMusic(int src, int type, String subType, String keyword, int limit, int page) {
        return new MusicSearchReq().searchMusic(src, type, subType, keyword, limit, page);
    }

    /**
     * 补充 NetMusicInfo 歌曲信息(包括 时长、专辑名称、封面图片、歌词)
     */
    public static void fillMusicInfo(NetMusicInfo musicInfo) {
        new MusicInfoReq().fillMusicInfo(musicInfo);
    }

    /**
     * 补充 NetMusicInfo 的 url
     */
    public static void fillMusicUrl(NetMusicInfo musicInfo) {
        new MusicUrlReq().fillMusicUrl(musicInfo);
    }

    /**
     * 根据关键词获取歌单
     */
    public static CommonResult<NetPlaylistInfo> searchPlaylists(int src, String keyword, int limit, int page) {
        return new PlaylistSearchReq().searchPlaylists(src, keyword, limit, page);
    }

    /**
     * 根据关键词获取专辑
     */
    public static CommonResult<NetAlbumInfo> searchAlbums(int src, String keyword, int limit, int page) {
        return new AlbumSearchReq().searchAlbums(src, keyword, limit, page);
    }

    /**
     * 根据关键词获取歌手
     */
    public static CommonResult<NetArtistInfo> searchArtists(int src, String keyword, int limit, int page) {
        return new ArtistSearchReq().searchArtists(src, keyword, limit, page);
    }

    /**
     * 根据关键词获取电台
     */
    public static CommonResult<NetRadioInfo> searchRadios(int src, String keyword, int limit, int page) {
        return new RadioSearchReq().searchRadios(src, keyword, limit, page);
    }

    /**
     * 根据关键词获取 MV
     */
    public static CommonResult<NetMvInfo> searchMvs(int src, String keyword, int limit, int page) {
        return new MvSearchReq().searchMvs(src, keyword, limit, page);
    }

    /**
     * 获取所有榜单
     */
    public static CommonResult<NetRankingInfo> getRankings(int src) {
        return new RankingSearchReq().getRankings(src);
    }

    /**
     * 根据关键词获取用户
     */
    public static CommonResult<NetUserInfo> searchUsers(int src, String keyword, int limit, int page) {
        return new UserSearchReq().searchUsers(src, keyword, limit, page);
    }

    /**
     * 获取 歌曲 / 歌单 / 专辑 / MV 评论
     */
    public static CommonResult<NetCommentInfo> getComments(NetResource resource, String type, int limit, int page, String cursor) {
        return new CommentReq().getComments(resource, type, limit, page, cursor);
    }

    /**
     * 获取歌曲乐谱
     */
    public static CommonResult<NetSheetInfo> getSheets(NetMusicInfo musicInfo) {
        return new SheetReq().getSheets(musicInfo);
    }

    /**
     * 获取乐谱图片链接
     */
    public static CommonResult<String> getSheetImgUrls(NetSheetInfo sheetInfo) {
        return new SheetReq().getSheetImgUrls(sheetInfo);
    }

    /**
     * 获取专辑照片链接
     */
    public static CommonResult<String> getAlbumImgUrls(NetAlbumInfo albumInfo, int page, int limit, String cursor) {
        return new AlbumInfoReq().getAlbumImgUrls(albumInfo, page, limit, cursor);
    }

    /**
     * 获取歌手照片链接
     */
    public static CommonResult<String> getArtistImgUrls(NetArtistInfo artistInfo, int page) {
        return new ArtistMenuReq().getArtistImgUrls(artistInfo, page);
    }

    /**
     * 获取电台照片链接
     */
    public static CommonResult<String> getRadioImgUrls(NetRadioInfo radioInfo, int page) {
        return new RadioInfoReq().getRadioImgUrls(radioInfo, page);
    }

    /**
     * 获取电台海报链接
     */
    public static CommonResult<String> getRadioPosterUrls(NetRadioInfo radioInfo, int page) {
        return new RadioInfoReq().getRadioPosterUrls(radioInfo, page);
    }

    /**
     * 获取推荐歌单
     */
    public static CommonResult<NetPlaylistInfo> getRecommendPlaylists(int src, String tag, int limit, int page) {
        return new RecommendPlaylistReq().getRecommendPlaylists(src, tag, limit, page);
    }

    /**
     * 获取精品歌单 + 网友精选碟，分页
     */
    public static CommonResult<NetPlaylistInfo> getHighQualityPlaylists(int src, String tag, int limit, int page) {
        return new HighQualityPlaylistReq().getHighQualityPlaylists(src, tag, limit, page);
    }

    /**
     * 获取歌手排行
     */
    public static CommonResult<NetArtistInfo> getArtistLists(int src, String tag, int limit, int page) {
        return new ArtistListReq().getArtistLists(src, tag, limit, page);
    }

    /**
     * 获取新晋电台
     */
    public static CommonResult<NetRadioInfo> getNewRadios(int src, int limit, int page) {
        return new NewRadioReq().getNewRadios(src, limit, page);
    }

    /**
     * 获取个性电台 + 今日优选 + 热门电台 + 热门电台榜
     */
    public static CommonResult<NetRadioInfo> getHotRadios(int src, String tag, int limit, int page) {
        return new HotRadioReq().getHotRadios(src, tag, limit, page);
    }

    /**
     * 获取推荐节目
     */
    public static CommonResult<NetMusicInfo> getRecommendPrograms(int src, String tag, int limit, int page) {
        return new RecommendProgramReq().getRecommendPrograms(src, tag, limit, page);
    }

    /**
     * 获取飙升歌曲
     */
    public static CommonResult<NetMusicInfo> getHotMusicRecommend(int src, String tag, int limit, int page) {
        return new HotMusicRecommendReq().getHotMusicRecommend(src, tag, limit, page);
    }

    /**
     * 获取推荐歌曲 + 新歌速递
     */
    public static CommonResult<NetMusicInfo> getNewMusic(int src, String tag, int limit, int page) {
        return new NewMusicReq().getNewMusic(src, tag, limit, page);
    }

    /**
     * 获取新碟上架
     */
    public static CommonResult<NetAlbumInfo> getNewAlbums(int src, String tag, int limit, int page) {
        return new NewAlbumReq().getNewAlbums(src, tag, limit, page);
    }

    /**
     * 获取 MV 排行 + 最新 MV + 推荐 MV
     */
    public static CommonResult<NetMvInfo> getRecommendMvs(int src, String tag, int limit, int page) {
        return new RecommendMvReq().getRecommendMvs(src, tag, limit, page);
    }

    /**
     * 根据歌单 id 和 source 预加载歌单信息
     */
    public static void preloadPlaylistInfo(NetPlaylistInfo playlistInfo) {
        new PlaylistInfoReq().preloadPlaylistInfo(playlistInfo);
    }

    /**
     * 根据歌单 id 获取歌单
     */
    public static CommonResult<NetPlaylistInfo> getPlaylistInfo(int source, String id) {
        return new PlaylistInfoReq().getPlaylistInfo(source, id);
    }

    /**
     * 根据歌单 id 补全歌单信息(包括封面图、描述)
     */
    public static void fillPlaylistInfo(NetPlaylistInfo playlistInfo) {
        new PlaylistInfoReq().fillPlaylistInfo(playlistInfo);
    }

    /**
     * 根据专辑 id 预加载专辑信息
     */
    public static void preloadAlbumInfo(NetAlbumInfo albumInfo) {
        new AlbumInfoReq().preloadAlbumInfo(albumInfo);
    }

    /**
     * 根据专辑 id 获取专辑
     */
    public static CommonResult<NetAlbumInfo> getAlbumInfo(String id, int source) {
        return new AlbumInfoReq().getAlbumInfo(id, source);
    }

    /**
     * 根据专辑 id 补全专辑信息(包括封面图、描述)
     */
    public static void fillAlbumInfo(NetAlbumInfo albumInfo) {
        new AlbumInfoReq().fillAlbumInfo(albumInfo);
    }

    /**
     * 根据歌手 id 预加载歌手信息
     */
    public static void preloadArtistInfo(NetArtistInfo artistInfo) {
        new ArtistInfoReq().preloadArtistInfo(artistInfo);
    }

    /**
     * 根据歌手 id 获取歌手
     */
    public static CommonResult<NetArtistInfo> getArtistInfo(String id, int source) {
        return new ArtistInfoReq().getArtistInfo(id, source);
    }

    /**
     * 根据歌手 id 补全歌手信息(包括封面图、描述)
     */
    public static void fillArtistInfo(NetArtistInfo artistInfo) {
        new ArtistInfoReq().fillArtistInfo(artistInfo);
    }

    /**
     * 根据电台 id 预加载电台信息
     */
    public static void preloadRadioInfo(NetRadioInfo radioInfo) {
        new RadioInfoReq().preloadRadioInfo(radioInfo);
    }

    /**
     * 根据电台 id 获取电台
     */
    public static CommonResult<NetRadioInfo> getRadioInfo(String id, int source) {
        return new RadioInfoReq().getRadioInfo(id, source);
    }

    /**
     * 根据电台 id 补全电台信息(包括封面图、描述)
     */
    public static void fillRadioInfo(NetRadioInfo radioInfo) {
        new RadioInfoReq().fillRadioInfo(radioInfo);
    }

    /**
     * 根据 MV id 预加载 MV 信息
     */
    public static void preloadMvInfo(NetMvInfo mvInfo) {
        new MvInfoReq().preloadMvInfo(mvInfo);
    }

    /**
     * 根据 MV id 补全 MV 信息(只包含 url)
     */
    public static void fillMvInfo(NetMvInfo mvInfo) {
        new MvInfoReq().fillMvInfo(mvInfo);
    }

    /**
     * 根据 MV id 补全 MV 基本信息
     */
    public static void fillMvDetail(NetMvInfo mvInfo) {
        new MvInfoReq().fillMvDetail(mvInfo);
    }

    /**
     * 根据榜单 id 预加载榜单信息(包括封面图)
     */
    public static void preloadRankingInfo(NetRankingInfo rankingInfo) {
        new RankingInfoReq().preloadRankingInfo(rankingInfo);
    }

    /**
     * 根据榜单 id 补全榜单信息(包括封面图)
     */
    public static void fillRankingInfo(NetRankingInfo rankingInfo) {
        new RankingInfoReq().fillRankingInfo(rankingInfo);
    }

    /**
     * 根据用户 id 预加载用户信息
     */
    public static void preloadUserInfo(NetUserInfo userInfo) {
        new UserInfoReq().preloadUserInfo(userInfo);
    }

    /**
     * 根据用户 id 补全用户信息(包括封面图、描述)
     */
    public static void fillUserInfo(NetUserInfo userInfo) {
        new UserInfoReq().fillUserInfo(userInfo);
    }

    /**
     * 根据歌单 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public static CommonResult<NetMusicInfo> getMusicInfoInPlaylist(NetPlaylistInfo playlistInfo, int limit, int page) {
        return new PlaylistInfoReq().getMusicInfoInPlaylist(playlistInfo, limit, page);
    }

    /**
     * 根据专辑 id 获取里面歌曲的粗略信息，分页，返回 NetMusicInfo
     */
    public static CommonResult<NetMusicInfo> getMusicInfoInAlbum(NetAlbumInfo albumInfo, int limit, int page) {
        return new AlbumInfoReq().getMusicInfoInAlbum(albumInfo, limit, page);
    }

    /**
     * 根据歌手 id 获取里面歌曲的粗略信息，分页，返回 NetMusicInfo
     */
    public static CommonResult<NetMusicInfo> getMusicInfoInArtist(NetArtistInfo artistInfo, int limit, int page) {
        return new ArtistInfoReq().getMusicInfoInArtist(artistInfo, limit, page);
    }

    /**
     * 根据歌手 id 获取里面专辑的粗略信息，分页，返回 NetAlbumInfo
     */
    public static CommonResult<NetAlbumInfo> getAlbumInfoInArtist(NetArtistInfo artistInfo, int limit, int page) {
        return new ArtistMenuReq().getAlbumInfoInArtist(artistInfo, limit, page);
    }

    /**
     * 根据歌手 id 获取里面 MV 的粗略信息，分页，返回 NetMvInfo
     */
    public static CommonResult<NetMvInfo> getMvInfoInArtist(NetArtistInfo artistInfo, int limit, int page) {
        return new ArtistMenuReq().getMvInfoInArtist(artistInfo, limit, page);
    }

    /**
     * 根据电台 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public static CommonResult<NetMusicInfo> getMusicInfoInRadio(NetRadioInfo radioInfo, int sortType, int limit, int page) {
        return new RadioInfoReq().getMusicInfoInRadio(radioInfo, sortType, limit, page);
    }

    /**
     * 根据榜单 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public static CommonResult<NetMusicInfo> getMusicInfoInRanking(String rankingId, int source, int limit, int page) {
        return new RankingInfoReq().getMusicInfoInRanking(rankingId, source, limit, page);
    }

    /**
     * 根据用户 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public static CommonResult<NetMusicInfo> getMusicInfoInUser(int recordType, NetUserInfo userInfo, int limit, int page) {
        return new UserInfoReq().getMusicInfoInUser(recordType, userInfo, limit, page);
    }

    /**
     * 获取相似歌曲
     *
     * @return
     */
    public static CommonResult<NetMusicInfo> getSimilarSongs(NetMusicInfo musicInfo) {
        return new MusicMenuReq().getSimilarSongs(musicInfo);
    }

    /**
     * 获取用户歌单（通过评论）
     *
     * @return
     */
    public static CommonResult<NetPlaylistInfo> getUserPlaylists(NetCommentInfo commentInfo, int limit, int page) {
        return new UserMenuReq().getUserPlaylists(commentInfo, limit, page);
    }

    /**
     * 获取用户专辑（通过评论）
     *
     * @return
     */
    public static CommonResult<NetAlbumInfo> getUserAlbums(NetCommentInfo commentInfo, int limit, int page) {
        return new UserMenuReq().getUserAlbums(commentInfo, limit, page);
    }

    /**
     * 获取用户歌单（通过用户）
     *
     * @return
     */
    public static CommonResult<NetPlaylistInfo> getUserPlaylists(NetUserInfo userInfo, int limit, int page) {
        return new UserMenuReq().getUserPlaylists(userInfo, limit, page);
    }

    /**
     * 获取用户专辑（通过用户）
     *
     * @return
     */
    public static CommonResult<NetAlbumInfo> getUserAlbums(NetUserInfo userInfo, int limit, int page) {
        return new UserMenuReq().getUserAlbums(userInfo, limit, page);
    }

    /**
     * 获取用户电台（通过用户）
     *
     * @return
     */
    public static CommonResult<NetRadioInfo> getUserRadios(NetUserInfo userInfo, int limit, int page) {
        return new UserMenuReq().getUserRadios(userInfo, limit, page);
    }

    /**
     * 获取用户视频 (通过用户)
     *
     * @return
     */
    public static CommonResult<NetMvInfo> getUserVideos(NetUserInfo userInfo, int sortType, int page, int limit, String cursor) {
        return new UserMenuReq().getUserVideos(userInfo, sortType, page, limit, cursor);
    }

    /**
     * 获取相关歌单（通过歌曲）
     *
     * @return
     */
    public static CommonResult<NetPlaylistInfo> getRelatedPlaylists(NetMusicInfo musicInfo) {
        return new MusicMenuReq().getRelatedPlaylists(musicInfo);
    }

    /**
     * 获取相关歌单（通过歌单）
     *
     * @return
     */
    public static CommonResult<NetPlaylistInfo> getSimilarPlaylists(NetPlaylistInfo playlistInfo) {
        return new PlaylistMenuReq().getSimilarPlaylists(playlistInfo);
    }

    /**
     * 获取相关 MV (通过歌曲)
     *
     * @return
     */
    public static CommonResult<NetMvInfo> getRelatedMvs(NetMusicInfo musicInfo, int limit, int page) {
        return new MvMenuReq().getRelatedMvs(musicInfo, limit, page);
    }

    /**
     * 获取相似 MV (通过 MV)
     *
     * @return
     */
    public static CommonResult<NetMvInfo> getSimilarMvs(NetMvInfo mvInfo) {
        return new MvMenuReq().getSimilarMvs(mvInfo);
    }

    /**
     * 获取视频分集
     *
     * @return
     */
    public static CommonResult<NetMvInfo> getVideoEpisodes(NetMvInfo mvInfo, int page, int limit) {
        return new MvMenuReq().getVideoEpisodes(mvInfo, page, limit);
    }

    /**
     * 获取相似专辑
     *
     * @return
     */
    public static CommonResult<NetAlbumInfo> getSimilarAlbums(NetAlbumInfo albumInfo) {
        return new AlbumMenuReq().getSimilarAlbums(albumInfo);
    }

    /**
     * 获取相似歌手 (通过歌手)
     *
     * @return
     */
    public static CommonResult<NetArtistInfo> getSimilarArtists(NetArtistInfo artistInfo, int page) {
        return new ArtistMenuReq().getSimilarArtists(artistInfo, page);
    }

    /**
     * 获取歌单收藏者
     *
     * @return
     */
    public static CommonResult<NetUserInfo> getPlaylistSubscribers(NetPlaylistInfo playlistInfo, int limit, int page) {
        return new PlaylistMenuReq().getPlaylistSubscribers(playlistInfo, limit, page);
    }

    /**
     * 获取歌手粉丝
     *
     * @return
     */
    public static CommonResult<NetUserInfo> getArtistFans(NetArtistInfo artistInfo, int limit, int page) {
        return new ArtistMenuReq().getArtistFans(artistInfo, limit, page);
    }

    /**
     * 获取歌手合作人
     *
     * @return
     */
    public static CommonResult<NetArtistInfo> getArtistBuddies(NetArtistInfo artistInfo, int page, int limit) {
        return new ArtistMenuReq().getArtistBuddies(artistInfo, page, limit);
    }

    /**
     * 获取歌手电台
     *
     * @return
     */
    public static CommonResult<NetRadioInfo> getArtistRadios(NetArtistInfo artistInfo, int page, int limit) {
        return new ArtistMenuReq().getArtistRadios(artistInfo, page, limit);
    }

    /**
     * 获取电台订阅者
     *
     * @return
     */
    public static CommonResult<NetUserInfo> getRadioSubscribers(NetRadioInfo radioInfo, int limit, int page) {
        return new RadioMenuReq().getRadioSubscribers(radioInfo, limit, page);
    }

    /**
     * 获取推荐电台
     *
     * @return
     */
    public static CommonResult<NetRadioInfo> getRecRadios(NetMusicInfo musicInfo) {
        return new MusicMenuReq().getRecRadios(musicInfo);
    }

    /**
     * 获取相似电台
     *
     * @return
     */
    public static CommonResult<NetRadioInfo> getSimilarRadios(NetRadioInfo radioInfo) {
        return new RadioMenuReq().getSimilarRadios(radioInfo);
    }

    /**
     * 获取电台演职员
     *
     * @return
     */
    public static CommonResult<NetArtistInfo> getRadioArtists(NetRadioInfo radioInfo) {
        return new RadioMenuReq().getRadioArtists(radioInfo);
    }

    /**
     * 获取用户实体 (通过用户 id)
     *
     * @return
     */
    public static CommonResult<NetUserInfo> getUserInfo(String id, int source) {
        return new UserInfoReq().getUserInfo(id, source);
    }

    /**
     * 获取用户关注 (通过用户)
     *
     * @return
     */
    public static CommonResult<NetUserInfo> getUserFollows(NetUserInfo userInfo, int limit, int page) {
        return new UserMenuReq().getUserFollows(userInfo, limit, page);
    }

    /**
     * 获取用户粉丝 (通过用户)
     *
     * @return
     */
    public static CommonResult<NetUserInfo> getUserFans(NetUserInfo userInfo, int limit, int page) {
        return new UserMenuReq().getUserFans(userInfo, limit, page);
    }

    /**
     * 根据歌曲 id 获取歌曲地址
     */
    public static String fetchMusicUrl(NetMusicInfo musicInfo) {
        return new MusicUrlReq().fetchMusicUrl(musicInfo);
    }

    /**
     * 歌曲换源
     *
     * @param musicInfo
     * @return
     */
    public static void fillAvailableMusicUrl(NetMusicInfo musicInfo) {
        new MusicUrlReq().fillAvailableMusicUrl(musicInfo);
    }

    /**
     * 根据 MV id 获取 MV 视频链接
     */
    public static String fetchMvUrl(NetMvInfo mvInfo) {
        return new MvUrlReq().fetchMvUrl(mvInfo);
    }

    /**
     * 根据为 NetMusicInfo 填充歌词字符串（包括原文、翻译、罗马音），没有的部分填充 ""
     */
    public static void fillLrc(NetMusicInfo musicInfo) {
        new MusicInfoReq().fillLrc(musicInfo);
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
        new DownloadReq().download(url, dest, headers, listener);
    }
}
