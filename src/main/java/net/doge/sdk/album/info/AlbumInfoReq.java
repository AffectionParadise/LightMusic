package net.doge.sdk.album.info;

import cn.hutool.http.HttpRequest;
import net.doge.constants.GlobalExecutors;
import net.doge.constants.NetMusicSource;
import net.doge.models.entities.NetAlbumInfo;
import net.doge.models.entities.NetMusicInfo;
import net.doge.models.server.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.util.SdkUtil;
import net.doge.utils.StringUtil;
import net.doge.utils.TimeUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class AlbumInfoReq {
    // 专辑信息 API
    private final String ALBUM_DETAIL_API = SdkCommon.prefix + "/album?id=%s";
    // 专辑信息 API (酷狗)
    private final String ALBUM_DETAIL_KG_API = "http://mobilecdn.kugou.com/api/v3/album/info?version=9108&albumid=%s";
    // 专辑歌曲 API (酷狗)
    private final String ALBUM_SONGS_KG_API = "http://mobilecdn.kugou.com/api/v3/album/song?version=9108&albumid=%s&page=%s&pagesize=%s";
    // 专辑信息 API (QQ)
    private final String ALBUM_DETAIL_QQ_API = SdkCommon.prefixQQ33 + "/album?albummid=%s";
    // 专辑歌曲 API (QQ)
    private final String ALBUM_SONGS_QQ_API = SdkCommon.prefixQQ33 + "/album/songs?albummid=%s";
    // 专辑信息 API (酷我)
    private final String ALBUM_DETAIL_KW_API = "http://www.kuwo.cn/api/www/album/albumInfo?albumId=%s&pn=%s&rn=%s&httpsStatus=1";
    // 专辑信息 API (咪咕)
    private final String ALBUM_DETAIL_MG_API = SdkCommon.prefixMg + "/album?id=%s";
    // 专辑信息 API (千千)
    private final String ALBUM_DETAIL_QI_API = "https://music.91q.com/v1/album/info?albumAssetCode=%s&appid=16073360&timestamp=%s";
    // 专辑信息 API (豆瓣)
    private final String ALBUM_DETAIL_DB_API = "https://music.douban.com/subject/%s/";
    // 专辑信息 API (堆糖)
    private final String ALBUM_DETAIL_DT_API = "https://www.duitang.com/napi/album/detail/?album_id=%s";
    
    // 获取专辑照片 API (堆糖)
    private final String GET_ALBUMS_IMG_DT_API
            = "https://www.duitang.com/napi/vienna/blog/by_album/?album_id=%s&after_id=%s&limit=%s&_=%s";

    // 歌曲封面信息 API (QQ)
    private final String SINGLE_SONG_IMG_QQ_API = "https://y.gtimg.cn/music/photo_new/T002R500x500M000%s.jpg";

    /**
     * 根据专辑 id 预加载专辑信息
     */
    public void preloadAlbumInfo(NetAlbumInfo albumInfo) {
        // 信息完整直接跳过
        if (albumInfo.isIntegrated()) return;

        GlobalExecutors.imageExecutor.submit(() -> albumInfo.setCoverImgThumb(SdkUtil.extractCover(albumInfo.getCoverImgThumbUrl())));
    }

    /**
     * 根据专辑 id 获取专辑
     */
    public CommonResult<NetAlbumInfo> getAlbumInfo(String id, int source) {
        LinkedList<NetAlbumInfo> res = new LinkedList<>();
        Integer t = 1;

        if (!"0".equals(id) && !"null".equals(id) && StringUtil.isNotEmpty(id)) {
            // 网易云
            if (source == NetMusicSource.NET_CLOUD) {
                String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_API, id))
                        .execute()
                        .body();
                JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
                JSONObject albumJson = albumInfoJson.getJSONObject("album");

                String albumId = albumJson.getString("id");
                String name = albumJson.getString("name");
                String artist = SdkUtil.parseArtists(albumJson, NetMusicSource.NET_CLOUD);
                String artistId = albumJson.getJSONArray("artists").getJSONObject(0).getString("id");
                String publishTime = TimeUtil.msToDate(albumJson.getLong("publishTime"));
                String coverImgThumbUrl = albumJson.getString("picUrl");
                Integer songNum = albumJson.getInt("size");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setId(albumId);
                albumInfo.setName(name);
                albumInfo.setArtist(artist);
                albumInfo.setArtistId(artistId);
                albumInfo.setPublishTime(publishTime);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(albumInfo);
            }

            // 酷狗
            else if (source == NetMusicSource.KG) {
                String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_KG_API, id))
                        .execute()
                        .body();
                JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
                JSONObject albumJson = albumInfoJson.getJSONObject("data");

                String albumId = albumJson.getString("albumid");
                String name = albumJson.getString("albumname");
                String artist = albumJson.getString("singername");
                String artistId = albumJson.getString("singerid");
                String publishTime = albumJson.getString("publishtime").split(" ")[0];
                String coverImgThumbUrl = albumJson.getString("imgurl").replace("/{size}", "");
//                Integer songNum = albumJson.getInt("songcount");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.KG);
                albumInfo.setId(albumId);
                albumInfo.setName(name);
                albumInfo.setArtist(artist);
                albumInfo.setArtistId(artistId);
                albumInfo.setPublishTime(publishTime);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(albumInfo);
            }

            // QQ
            else if (source == NetMusicSource.QQ) {
                String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_QQ_API, id))
                        .execute()
                        .body();
                JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
                JSONObject albumJson = albumInfoJson.getJSONObject("data");

                String albumId = albumJson.getString("mid");
                String name = albumJson.getString("albumName");
                String artist = SdkUtil.parseArtists(albumJson, NetMusicSource.QQ);
                String artistId = albumJson.getJSONArray("ar").getJSONObject(0).getString("mid");
                String publishTime = albumJson.getString("publishTime");
                String coverImgThumbUrl = String.format(SINGLE_SONG_IMG_QQ_API, albumId);
//                Integer songNum = albumJson.getInt("songcount");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.QQ);
                albumInfo.setId(albumId);
                albumInfo.setName(name);
                albumInfo.setArtist(artist);
                albumInfo.setArtistId(artistId);
                albumInfo.setPublishTime(publishTime);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(albumInfo);
            }

            // 酷我
            else if (source == NetMusicSource.KW) {
                String albumInfoBody = SdkCommon.kwRequest(String.format(ALBUM_DETAIL_KW_API, id, 1, 1))
                        .execute()
                        .body();
                JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
                JSONObject albumJson = albumInfoJson.getJSONObject("data");

                String albumId = albumJson.getString("albumid");
                String name = albumJson.getString("album");
                String artist = albumJson.getString("artist");
                String artistId = albumJson.getString("artistid");
                String publishTime = albumJson.getString("releaseDate");
                String coverImgThumbUrl = albumJson.getString("pic");
                Integer songNum = albumJson.getInt("total");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.KW);
                albumInfo.setId(albumId);
                albumInfo.setName(name);
                albumInfo.setArtist(artist);
                albumInfo.setArtistId(artistId);
                albumInfo.setPublishTime(publishTime);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(albumInfo);
            }

            // 咪咕
            else if (source == NetMusicSource.MG) {
                String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_MG_API, id))
                        .execute()
                        .body();
                JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
                JSONObject albumJson = albumInfoJson.getJSONObject("data");

                String albumId = albumJson.getString("id");
                String name = albumJson.getString("name");
                String artist = SdkUtil.parseArtists(albumJson, NetMusicSource.MG);
                String artistId = albumJson.getJSONArray("artists").getJSONObject(0).getString("id");
                String publishTime = albumJson.getString("publishTime");
                String coverImgThumbUrl = "https:" + albumJson.getString("picUrl");
                Integer songNum = albumJson.getJSONArray("songList").size();

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.MG);
                albumInfo.setId(albumId);
                albumInfo.setName(name);
                albumInfo.setArtist(artist);
                albumInfo.setArtistId(artistId);
                albumInfo.setPublishTime(publishTime);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(albumInfo);
            }

            // 千千
            else if (source == NetMusicSource.QI) {
                String albumInfoBody = HttpRequest.get(SdkCommon.buildQianUrl(String.format(ALBUM_DETAIL_QI_API, id, System.currentTimeMillis())))
                        .execute()
                        .body();
                JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
                JSONObject albumJson = albumInfoJson.getJSONObject("data");

                String albumId = albumJson.getString("albumAssetCode");
                String name = albumJson.getString("title");
                String artist = SdkUtil.parseArtists(albumJson, NetMusicSource.QI);
                JSONArray artistArray = albumJson.optJSONArray("artist");
                String artistId = artistArray != null && !artistArray.isEmpty() ? artistArray.getJSONObject(0).getString("artistCode") : "";
                String publishTime = albumJson.getString("releaseDate").split("T")[0];
                String coverImgThumbUrl = albumJson.getString("pic");
                Integer songNum = albumJson.getJSONArray("trackList").size();

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.QI);
                albumInfo.setId(albumId);
                albumInfo.setName(name);
                albumInfo.setArtist(artist);
                albumInfo.setArtistId(artistId);
                albumInfo.setPublishTime(publishTime);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(albumInfo);
            }
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 根据专辑 id 补全专辑信息(包括封面图、描述)
     */
    public void fillAlbumInfo(NetAlbumInfo albumInfo) {
        // 信息完整直接跳过
        if (albumInfo.isIntegrated()) return;

        int source = albumInfo.getSource();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_API, albumInfo.getId()))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject albumJson = albumInfoJson.getJSONObject("album");

            String coverImgUrl = albumJson.getString("picUrl");
            String description = albumJson.getString("description");
            if (!albumInfo.hasSongNum()) albumInfo.setSongNum(albumJson.getInt("size"));
            if (!albumInfo.hasPublishTime())
                albumInfo.setPublishTime(TimeUtil.msToDate(albumJson.getLong("publishTime")));

            if (!albumInfo.hasCoverImgUrl()) albumInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.submit(() -> albumInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            albumInfo.setDescription(description.equals("null") ? "" : description);
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_KG_API, albumInfo.getId()))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");

            String coverImgUrl = data.getString("imgurl").replace("/{size}", "");
            String description = data.getString("intro").replace("\\n", "\n");

            if (!albumInfo.hasCoverImgUrl()) albumInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.submit(() -> albumInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            albumInfo.setDescription(description);
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_QQ_API, albumInfo.getId()))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");

            // QQ 专辑封面图片 url 获取方式与歌曲相同
            String coverImgUrl = String.format(SINGLE_SONG_IMG_QQ_API, albumInfo.getId());
            String description = data.getString("desc");

            if (!albumInfo.hasCoverImgUrl()) albumInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.submit(() -> albumInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            albumInfo.setDescription(description);
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            String albumInfoBody = SdkCommon.kwRequest(String.format(ALBUM_DETAIL_KW_API, albumInfo.getId(), 1, 1))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.optJSONObject("data");

            if (data != null) {
                String coverImgUrl = data.getString("pic");
                String description = data.getString("albuminfo");
                Integer songNum = data.getInt("total");

                if (!albumInfo.hasCoverImgUrl()) albumInfo.setCoverImgUrl(coverImgUrl);
                GlobalExecutors.imageExecutor.submit(() -> albumInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
                albumInfo.setDescription(description);
                albumInfo.setSongNum(songNum);
            }
        }

        // 咪咕
        else if (source == NetMusicSource.MG) {
            String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_MG_API, albumInfo.getId()))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");

            String coverImgUrl = "https:" + data.getString("picUrl");
            String description = data.getString("desc");

            if (!albumInfo.hasCoverImgUrl()) albumInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.submit(() -> albumInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            albumInfo.setDescription(description);
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            String albumInfoBody = HttpRequest.get(SdkCommon.buildQianUrl(String.format(ALBUM_DETAIL_QI_API, albumInfo.getId(), System.currentTimeMillis())))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject albumJson = albumInfoJson.getJSONObject("data");

            String coverImgUrl = albumJson.getString("pic");
            String description = albumJson.getString("introduce");
            if (!albumInfo.hasSongNum()) albumInfo.setSongNum(albumJson.getJSONArray("trackList").size());
            if (!albumInfo.hasPublishTime())
                albumInfo.setPublishTime(albumJson.getString("releaseDate").split("T")[0]);

            if (!albumInfo.hasCoverImgUrl()) albumInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.submit(() -> albumInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            albumInfo.setDescription(description);
        }

        // 豆瓣
        else if (source == NetMusicSource.DB) {
            String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_DB_API, albumInfo.getId()))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(albumInfoBody);
            String info = StringUtil.getPrettyText(doc.select("div#info").first()) + "\n";
            Element re = doc.select("div#link-report").first();
            Elements span = re.select("span");
            String desc = StringUtil.getPrettyText(span.isEmpty() ? re : span.last()) + "\n";
            String tracks = StringUtil.getPrettyText(doc.select("div.track-list div div").first());
            String coverImgUrl = doc.select("div#mainpic img").attr("src");

            albumInfo.setDescription(info + desc + "\n曲目：\n" + tracks);
            if (!albumInfo.hasCoverImgUrl()) albumInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.submit(() -> albumInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
        }

        // 堆糖
        else if (source == NetMusicSource.DT) {
            String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_DT_API, albumInfo.getId()))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject albumJson = albumInfoJson.getJSONObject("data");

            String coverImgUrl = albumJson.getJSONArray("covers").getString(0);
            String description = albumJson.getString("desc");
            if (!albumInfo.hasSongNum()) albumInfo.setSongNum(albumJson.getInt("count"));
            if (!albumInfo.hasPublishTime())
                albumInfo.setPublishTime(TimeUtil.msToDate(albumJson.getLong("updated_at_ts") * 1000));

            if (!albumInfo.hasCoverImgUrl()) albumInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.submit(() -> albumInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            albumInfo.setDescription(description);
        }
    }

    /**
     * 根据专辑 id 获取里面歌曲的粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInAlbum(String albumId, int source, int limit, int page) {
        int total = 0;
        List<NetMusicInfo> netMusicInfos = new LinkedList<>();

        // 网易云 (程序分页)
        if (source == NetMusicSource.NET_CLOUD) {
            String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_API, albumId))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONArray songArray = albumInfoJson.getJSONArray("songs");
            total = songArray.size();
            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("id");
                String name = songJson.getString("name").trim();
                String artists = SdkUtil.parseArtists(songJson, NetMusicSource.NET_CLOUD);
                String artistId = songJson.getJSONArray("ar").getJSONObject(0).getString("id");
                String albumName = songJson.getJSONObject("al").getString("name");
                String alId = songJson.getJSONObject("al").getString("id");
                Double duration = songJson.getDouble("dt") / 1000;
                String mvId = songJson.getString("mv");

                NetMusicInfo netMusicInfo = new NetMusicInfo();
                netMusicInfo.setId(songId);
                netMusicInfo.setName(name);
                netMusicInfo.setArtist(artists);
                netMusicInfo.setArtistId(artistId);
                netMusicInfo.setAlbumName(albumName);
                netMusicInfo.setAlbumId(alId);
                netMusicInfo.setDuration(duration);
                netMusicInfo.setMvId(mvId);

                netMusicInfos.add(netMusicInfo);
            }
        }

        // 酷狗 (接口分页)
        else if (source == NetMusicSource.KG) {
            String albumInfoBody = HttpRequest.get(String.format(ALBUM_SONGS_KG_API, albumId, page, limit))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            total = data.getInt("total");
            JSONArray songArray = data.getJSONArray("info");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String hash = songJson.getString("hash");
                String songId = songJson.getString("album_audio_id");
                String[] s = songJson.getString("filename").split(" - ");
                String name = s[1];
                String artists = s[0];
//                String albumName = songJson.getString("remark");
                String alId = songJson.getString("album_id");
                Double duration = songJson.getDouble("duration");
                String mvId = songJson.getString("mvhash");

                NetMusicInfo netMusicInfo = new NetMusicInfo();
                netMusicInfo.setSource(NetMusicSource.KG);
                netMusicInfo.setHash(hash);
                netMusicInfo.setId(songId);
                netMusicInfo.setName(name);
                netMusicInfo.setArtist(artists);
//                netMusicInfo.setAlbumName(albumName);
                netMusicInfo.setAlbumId(alId);
                netMusicInfo.setDuration(duration);
                netMusicInfo.setMvId(mvId);

                netMusicInfos.add(netMusicInfo);
            }
        }

        // QQ (程序分页)
        else if (source == NetMusicSource.QQ) {
            String albumInfoBody = HttpRequest.get(String.format(ALBUM_SONGS_QQ_API, albumId))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            total = data.getInt("total");
            JSONArray songArray = data.getJSONArray("list");
            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("mid");
                String name = songJson.getString("name");
                String artists = SdkUtil.parseArtists(songJson, NetMusicSource.QQ);
                String artistId = songJson.getJSONArray("singer").getJSONObject(0).getString("mid");
                String albumName = songJson.getJSONObject("album").getString("name");
                String alId = songJson.getJSONObject("album").getString("mid");
                Double duration = songJson.getDouble("interval");
                String mvId = songJson.getJSONObject("mv").getString("vid");

                NetMusicInfo netMusicInfo = new NetMusicInfo();
                netMusicInfo.setSource(NetMusicSource.QQ);
                netMusicInfo.setId(songId);
                netMusicInfo.setName(name);
                netMusicInfo.setArtist(artists);
                netMusicInfo.setArtistId(artistId);
                netMusicInfo.setAlbumName(albumName);
                netMusicInfo.setAlbumId(alId);
                netMusicInfo.setDuration(duration);
                netMusicInfo.setMvId(mvId);

                netMusicInfos.add(netMusicInfo);
            }
        }

        // 酷我 (接口分页)
        else if (source == NetMusicSource.KW) {
            String albumInfoBody = SdkCommon.kwRequest(String.format(ALBUM_DETAIL_KW_API, albumId, page, limit))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            total = data.getInt("total");
            JSONArray songArray = data.getJSONArray("musicList");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("rid");
                String name = songJson.getString("name");
                String artists = songJson.getString("artist");
                String artistId = songJson.getString("artistid");
                String albumName = songJson.getString("album");
                String alId = songJson.getString("albumid");
                Double duration = songJson.getDouble("duration");
                String mvId = songJson.getInt("hasmv") == 0 ? "" : songId;

                NetMusicInfo netMusicInfo = new NetMusicInfo();
                netMusicInfo.setSource(NetMusicSource.KW);
                netMusicInfo.setId(songId);
                netMusicInfo.setName(name);
                netMusicInfo.setArtist(artists);
                netMusicInfo.setArtistId(artistId);
                netMusicInfo.setAlbumName(albumName);
                netMusicInfo.setAlbumId(alId);
                netMusicInfo.setDuration(duration);
                netMusicInfo.setMvId(mvId);

                netMusicInfos.add(netMusicInfo);
            }
        }

        // 咪咕 (程序分页)
        else if (source == NetMusicSource.MG) {
            String albumInfoBody = HttpRequest.get(String.format(ALBUM_DETAIL_MG_API, albumId))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            JSONArray songArray = data.getJSONArray("songList");
            total = songArray.size();
            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("cid");
                String name = songJson.getString("name");
                String artists = SdkUtil.parseArtists(songJson, NetMusicSource.MG);
                String artistId = songJson.getJSONArray("artists").getJSONObject(0).getString("id");
                String albumName = songJson.getJSONObject("album").getString("name");
                String alId = songJson.getJSONObject("album").getString("id");
                // 咪咕音乐可能没有 mvId
                String mvId = songJson.optString("mvId");

                NetMusicInfo netMusicInfo = new NetMusicInfo();
                netMusicInfo.setSource(NetMusicSource.MG);
                netMusicInfo.setId(songId);
                netMusicInfo.setName(name);
                netMusicInfo.setArtist(artists);
                netMusicInfo.setArtistId(artistId);
                netMusicInfo.setAlbumName(albumName);
                netMusicInfo.setAlbumId(alId);
                netMusicInfo.setMvId(mvId);

                netMusicInfos.add(netMusicInfo);
            }
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            String albumInfoBody = HttpRequest.get(SdkCommon.buildQianUrl(String.format(ALBUM_DETAIL_QI_API, albumId, System.currentTimeMillis())))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.fromObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            JSONArray songArray = data.getJSONArray("trackList");
            total = songArray.size();
            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("assetId");
                String name = songJson.getString("title");
                String artists = SdkUtil.parseArtists(songJson, NetMusicSource.QI);
                JSONArray artistArray = songJson.optJSONArray("artist");
                String artistId = artistArray != null && !artistArray.isEmpty() ? artistArray.getJSONObject(0).getString("artistCode") : "";
                String albumName = data.getString("title");
                String alId = data.getString("albumAssetCode");
                Double duration = songJson.getDouble("duration");

                NetMusicInfo netMusicInfo = new NetMusicInfo();
                netMusicInfo.setSource(NetMusicSource.QI);
                netMusicInfo.setId(songId);
                netMusicInfo.setName(name);
                netMusicInfo.setArtist(artists);
                netMusicInfo.setArtistId(artistId);
                netMusicInfo.setAlbumName(albumName);
                netMusicInfo.setAlbumId(alId);
                netMusicInfo.setDuration(duration);

                netMusicInfos.add(netMusicInfo);
            }
        }

        return new CommonResult<>(netMusicInfos, total);
    }
    
    /**
     * 获取专辑照片链接
     */
    public CommonResult<String> getAlbumImgUrls(NetAlbumInfo albumInfo, int page, int limit, String cursor) {
        int source = albumInfo.getSource();
        String id = albumInfo.getId();
        LinkedList<String> imgUrls = new LinkedList<>();
        cursor = StringUtil.encode(cursor);
        Integer total = 0;

        if (source == NetMusicSource.DT) {
            String imgInfoBody = HttpRequest.get(String.format(GET_ALBUMS_IMG_DT_API, id, cursor, limit, System.currentTimeMillis()))
                    .execute()
                    .body();
            JSONObject data = JSONObject.fromObject(imgInfoBody).getJSONObject("data");
            JSONArray imgs = data.getJSONArray("object_list");
            cursor = data.getString("after");
            total = page * limit;
            if (data.getInt("more") == 1) total++;
            else total = (page - 1) * limit + imgs.size();
            for (int i = 0, len = imgs.size(); i < len; i++) {
                JSONObject img = imgs.getJSONObject(i);
                imgUrls.add(img.getJSONObject("photo").getString("path"));
            }
        }

        return new CommonResult<>(imgUrls, total, cursor);
    }
}
