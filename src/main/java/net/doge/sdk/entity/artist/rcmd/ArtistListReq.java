package net.doge.sdk.entity.artist.rcmd;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.system.NetMusicSource;
import net.doge.sdk.common.Tags;
import net.doge.model.entity.NetArtistInfo;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.collection.ListUtil;
import net.doge.util.common.StringUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class ArtistListReq {
    // 歌手榜 API
    private final String ARTIST_RANKING_LIST_API = SdkCommon.prefix + "/toplist/artist?type=%s";
    // 热门歌手 API
    private final String HOT_ARTIST_LIST_API = SdkCommon.prefix + "/top/artists?offset=%s&limit=%s";
    // 分类歌手 API
    private final String CAT_ARTIST_API = SdkCommon.prefix + "/artist/list?type=%s&area=%s&initial=%s&offset=%s&limit=%s";
    // 曲风歌手 API
    private final String STYLE_ARTIST_API = SdkCommon.prefix + "/style/artist?tagId=%s&cursor=%s&size=%s";
    // 热门歌手推荐 API (酷狗)
    private final String HOT_ARTIST_LIST_KG_API = "http://mobilecdnbj.kugou.com/api/v5/singer/list?sextype=%s&type=%s&sort=1&page=%s&pagesize=%s";
    // 飙升歌手推荐 API (酷狗)
    private final String UP_ARTIST_LIST_KG_API = "http://mobilecdnbj.kugou.com/api/v5/singer/list?sextype=%s&type=%s&sort=2&page=%s&pagesize=%s";
    // 推荐歌手 API (QQ)
    private final String ARTIST_LIST_QQ_API = SdkCommon.prefixQQ33 + "/singer/list?sex=%s&genre=%s&index=%s&area=%s&pageNo=%s";
    // 歌手推荐 API (酷我)
    private final String ARTIST_LIST_KW_API = "http://www.kuwo.cn/api/www/artist/artistInfo?category=%s&pn=%s&rn=%s&httpsStatus=1";
    // 全部歌手 API (酷我)
    private final String ALL_ARTISTS_LIST_KW_API = "http://www.kuwo.cn/api/www/artist/artistInfo?category=%s&prefix=%s&pn=%s&rn=%s&httpsStatus=1";
    // 来电新声榜 API (咪咕)
    private final String ARTIST_LIST_MG_API = "https://app.c.nf.migu.cn/MIGUM2.0/v1.0/content/querycontentbyId.do?columnId=22425062";
    // 来电唱作榜 API (咪咕)
    private final String ARTIST_LIST_MG_API_2 = "https://app.c.nf.migu.cn/MIGUM2.0/v1.0/content/querycontentbyId.do?columnId=22425072";
    // 推荐歌手 API (千千)
    private final String REC_ARTISTS_LIST_QI_API = "https://music.91q.com/v1/index?appid=16073360&pageSize=12&timestamp=%s&type=song";
    // 分类歌手 API (千千)
    private final String CAT_ARTISTS_LIST_QI_API = "https://music.91q.com/v1/artist/list?appid=16073360&" +
            "artistFristLetter=%s&artistGender=%s&artistRegion=%s&pageNo=%s&pageSize=%s&timestamp=%s";
    // CV 广场 API (猫耳)
    private final String CAT_CV_ME_API = "https://www.missevan.com/organization/getseiys?initial=%s&p=%s&page_size=%s";
    // 社团广场 API (猫耳)
    private final String CAT_ORGANIZATIONS_ME_API = "https://www.missevan.com/organization/getorganizations?initial=%s&p=%s&page_size=%s";

    // 歌手图片 API (QQ)
    private final String ARTIST_IMG_QQ_API = "https://y.gtimg.cn/music/photo_new/T001R500x500M000%s.jpg";

    /**
     * 获取歌手排行
     */
    public CommonResult<NetArtistInfo> getArtistLists(int src, String tag, int limit, int page) {
        AtomicInteger total = new AtomicInteger();
        List<NetArtistInfo> artistInfos = new LinkedList<>();
//        Set<NetArtistInfo> set = Collections.synchronizedSet(new HashSet<>());

        final String defaultTag = "默认";
        String[] s = Tags.artistTag.get(tag);

        // 网易云 (接口分页)
        // 歌手榜
        Callable<CommonResult<NetArtistInfo>> getArtistRanking = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[0])) {
                String artistInfoBody = HttpRequest.get(String.format(ARTIST_RANKING_LIST_API, s[0]))
                        .execute()
                        .body();
                JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
                JSONArray artistArray = artistInfoJson.getJSONObject("list").getJSONArray("artists");
                t = artistArray.size();
                for (int i = (page - 1) * limit, len = Math.min(artistArray.size(), page * limit); i < len; i++) {
                    JSONObject artistJson = artistArray.getJSONObject(i);

                    String artistId = artistJson.getString("id");
                    String artistName = artistJson.getString("name");
                    Integer songNum = artistJson.getIntValue("musicSize");
                    Integer albumNum = artistJson.getIntValue("albumSize");
                    String coverImgThumbUrl = artistJson.getString("img1v1Url");

                    NetArtistInfo artistInfo = new NetArtistInfo();
                    artistInfo.setId(artistId);
                    artistInfo.setName(artistName);
                    artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    artistInfo.setSongNum(songNum);
                    artistInfo.setAlbumNum(albumNum);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        artistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(artistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 热门歌手
        Callable<CommonResult<NetArtistInfo>> getHotArtist = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            String artistInfoBody = HttpRequest.get(String.format(HOT_ARTIST_LIST_API, (page - 1) * limit, limit))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONArray artistArray = artistInfoJson.getJSONArray("artists");
            t = artistArray.size();
            for (int i = 0, len = artistArray.size(); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i);

                String artistId = artistJson.getString("id");
                String artistName = artistJson.getString("name");
                Integer songNum = artistJson.getIntValue("musicSize");
                Integer albumNum = artistJson.getIntValue("albumSize");
                String coverImgThumbUrl = artistJson.getString("img1v1Url");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                artistInfo.setSongNum(songNum);
                artistInfo.setAlbumNum(albumNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(artistInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 分类歌手
        Callable<CommonResult<NetArtistInfo>> getCatArtist = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[1])) {
                String[] split = s[1].split(" ");
                String artistInfoBody = HttpRequest.get(String.format(CAT_ARTIST_API, split[0], split[1], split[2], (page - 1) * limit, limit))
                        .execute()
                        .body();
                JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
                JSONArray artistArray = artistInfoJson.getJSONArray("artists");
                t = artistArray.size();
                for (int i = 0, len = artistArray.size(); i < len; i++) {
                    JSONObject artistJson = artistArray.getJSONObject(i);

                    String artistId = artistJson.getString("id");
                    String artistName = artistJson.getString("name");
                    Integer songNum = artistJson.getIntValue("musicSize");
                    Integer albumNum = artistJson.getIntValue("albumSize");
                    String coverImgThumbUrl = artistJson.getString("img1v1Url");

                    NetArtistInfo artistInfo = new NetArtistInfo();
                    artistInfo.setId(artistId);
                    artistInfo.setName(artistName);
                    artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    artistInfo.setSongNum(songNum);
                    artistInfo.setAlbumNum(albumNum);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        artistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(artistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 曲风歌手
        Callable<CommonResult<NetArtistInfo>> getStyleArtist = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[2])) {
                String artistInfoBody = HttpRequest.get(String.format(STYLE_ARTIST_API, s[2], (page - 1) * limit, limit))
                        .execute()
                        .body();
                JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
                JSONObject data = artistInfoJson.getJSONObject("data");
                JSONArray artistArray = data.getJSONArray("artists");
                t = data.getJSONObject("page").getIntValue("total");
                for (int i = 0, len = artistArray.size(); i < len; i++) {
                    JSONObject artistJson = artistArray.getJSONObject(i);

                    String artistId = artistJson.getString("id");
                    String artistName = artistJson.getString("name");
                    Integer songNum = artistJson.getIntValue("musicSize");
                    Integer albumNum = artistJson.getIntValue("albumSize");
                    String coverImgThumbUrl = artistJson.getString("img1v1Url");

                    NetArtistInfo artistInfo = new NetArtistInfo();
                    artistInfo.setId(artistId);
                    artistInfo.setName(artistName);
                    artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    artistInfo.setSongNum(songNum);
                    artistInfo.setAlbumNum(albumNum);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        artistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(artistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 酷狗(接口分页)
        // 热门歌手
        Callable<CommonResult<NetArtistInfo>> getHotArtistKg = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[3])) {
                String[] split = s[3].split(" ");
                String artistInfoBody = HttpRequest.get(String.format(HOT_ARTIST_LIST_KG_API, split[0], split[1], page, limit))
                        .execute()
                        .body();
                JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
                JSONObject data = artistInfoJson.getJSONObject("data");
                t = data.getIntValue("total");
                JSONArray artistArray = data.getJSONArray("info");
                for (int i = 0, len = artistArray.size(); i < len; i++) {
                    JSONObject artistJson = artistArray.getJSONObject(i);

                    String artistId = artistJson.getString("singerid");
                    String artistName = artistJson.getString("singername");
//            Integer songNum = artistJson.getIntValue("songcount");
//            Integer albumNum = artistJson.getIntValue("albumcount");
//            Integer mvNum = artistJson.getIntValue("mvcount");
                    String coverImgThumbUrl = artistJson.getString("imgurl").replace("{size}", "240");

                    NetArtistInfo artistInfo = new NetArtistInfo();
                    artistInfo.setSource(NetMusicSource.KG);
                    artistInfo.setId(artistId);
                    artistInfo.setName(artistName);
//            artistInfo.setSongNum(songNum);
//            artistInfo.setAlbumNum(albumNum);
//            artistInfo.setMvNum(mvNum);
                    artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        artistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(artistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 飙升歌手
        Callable<CommonResult<NetArtistInfo>> getUpArtistKg = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[3])) {
                String[] split = s[3].split(" ");
                String artistInfoBody = HttpRequest.get(String.format(UP_ARTIST_LIST_KG_API, split[0], split[1], page, limit))
                        .execute()
                        .body();
                JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
                JSONObject data = artistInfoJson.getJSONObject("data");
                t = data.getIntValue("total");
                JSONArray artistArray = data.getJSONArray("info");
                for (int i = 0, len = artistArray.size(); i < len; i++) {
                    JSONObject artistJson = artistArray.getJSONObject(i);

                    String artistId = artistJson.getString("singerid");
                    String artistName = artistJson.getString("singername");
//                    Integer songNum = artistJson.getIntValue("songcount");
//                    Integer albumNum = artistJson.getIntValue("albumcount");
//                    Integer mvNum = artistJson.getIntValue("mvcount");
                    String coverImgThumbUrl = artistJson.getString("imgurl").replace("{size}", "240");

                    NetArtistInfo artistInfo = new NetArtistInfo();
                    artistInfo.setSource(NetMusicSource.KG);
                    artistInfo.setId(artistId);
                    artistInfo.setName(artistName);
//                    artistInfo.setSongNum(songNum);
//                    artistInfo.setAlbumNum(albumNum);
//                    artistInfo.setMvNum(mvNum);
                    artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        artistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(artistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // QQ (每页固定 80 条)
        Callable<CommonResult<NetArtistInfo>> getArtistRankingQq = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[4])) {
                final int num = 80;
                String[] split = s[4].split(" ");
                String artistInfoBody = HttpRequest.get(String.format(ARTIST_LIST_QQ_API, split[0], split[1], split[2], split[3], (page - 1) / 4 + 1))
                        .execute()
                        .body();
                JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
                JSONObject data = artistInfoJson.getJSONObject("data");
                t = data.getIntValue("total");
                JSONArray artistArray = data.getJSONArray("list");
                for (int i = (page - 1) * limit % num, len = Math.min(artistArray.size(), (page - 1) * limit % num + limit); i < len; i++) {
                    JSONObject artistJson = artistArray.getJSONObject(i);

                    String artistId = artistJson.getString("singer_mid");
                    String artistName = artistJson.getString("singer_name");
                    String coverImgThumbUrl = String.format(ARTIST_IMG_QQ_API, artistId);

                    NetArtistInfo artistInfo = new NetArtistInfo();
                    artistInfo.setSource(NetMusicSource.QQ);
                    artistInfo.setId(artistId);
                    artistInfo.setName(artistName);
                    artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        artistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(artistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 酷我 (接口分页)
        // 推荐歌手
        Callable<CommonResult<NetArtistInfo>> getArtistRankingKw = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[5])) {
                HttpResponse resp = SdkCommon.kwRequest(String.format(ARTIST_LIST_KW_API, s[5], page, limit)).execute();
                if (resp.getStatus() == HttpStatus.HTTP_OK) {
                    String artistInfoBody = resp.body();
                    JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
                    JSONObject data = artistInfoJson.getJSONObject("data");
                    t = data.getIntValue("total");
                    JSONArray artistArray = data.getJSONArray("artistList");
                    for (int i = 0, len = artistArray.size(); i < len; i++) {
                        JSONObject artistJson = artistArray.getJSONObject(i);

                        String artistId = artistJson.getString("id");
                        String artistName = artistJson.getString("name");
                        String coverImgThumbUrl = artistJson.getString("pic300");
                        Integer songNum = artistJson.getIntValue("musicNum");
                        Integer albumNum = artistJson.getIntValue("albumNum");
                        Integer mvNum = artistJson.getIntValue("mvNum");

                        NetArtistInfo artistInfo = new NetArtistInfo();
                        artistInfo.setSource(NetMusicSource.KW);
                        artistInfo.setId(artistId);
                        artistInfo.setName(artistName);
                        artistInfo.setSongNum(songNum);
                        artistInfo.setAlbumNum(albumNum);
                        artistInfo.setMvNum(mvNum);
                        artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                        GlobalExecutors.imageExecutor.execute(() -> {
                            BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                            artistInfo.setCoverImgThumb(coverImgThumb);
                        });

                        res.add(artistInfo);
                    }
                }
            }
            return new CommonResult<>(res, t);
        };
        // 全部歌手
        Callable<CommonResult<NetArtistInfo>> getAllArtistsKw = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[6])) {
                String[] sp = s[6].split(" ", -1);
                HttpResponse resp = SdkCommon.kwRequest(String.format(ALL_ARTISTS_LIST_KW_API, sp[0], sp[1], page, limit))
                        .header(Header.REFERER, StringUtil.notEmpty(sp[1]) ? "http://www.kuwo.cn/singers" : "")
                        .execute();
                if (resp.getStatus() == HttpStatus.HTTP_OK) {
                    String artistInfoBody = resp.body();
                    JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
                    JSONObject data = artistInfoJson.getJSONObject("data");
                    t = data.getIntValue("total");
                    JSONArray artistArray = data.getJSONArray("artistList");
                    for (int i = 0, len = artistArray.size(); i < len; i++) {
                        JSONObject artistJson = artistArray.getJSONObject(i);

                        String artistId = artistJson.getString("id");
                        String artistName = artistJson.getString("name");
                        String coverImgThumbUrl = artistJson.getString("pic300");
                        Integer songNum = artistJson.getIntValue("musicNum");
                        Integer albumNum = artistJson.getIntValue("albumNum");
                        Integer mvNum = artistJson.getIntValue("mvNum");

                        NetArtistInfo artistInfo = new NetArtistInfo();
                        artistInfo.setSource(NetMusicSource.KW);
                        artistInfo.setId(artistId);
                        artistInfo.setName(artistName);
                        artistInfo.setSongNum(songNum);
                        artistInfo.setAlbumNum(albumNum);
                        artistInfo.setMvNum(mvNum);
                        artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                        GlobalExecutors.imageExecutor.execute(() -> {
                            BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                            artistInfo.setCoverImgThumb(coverImgThumb);
                        });

                        res.add(artistInfo);
                    }
                }
            }
            return new CommonResult<>(res, t);
        };

        // 咪咕
        // 来电新声榜(程序分页)
        Callable<CommonResult<NetArtistInfo>> getArtistRankingMg = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            String artistInfoBody = HttpRequest.get(String.format(ARTIST_LIST_MG_API))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("columnInfo");
            t = data.getIntValue("contentsCount");
            JSONArray artistArray = data.getJSONArray("contents");
            for (int i = (page - 1) * limit, len = Math.min(artistArray.size(), page * limit); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i).getJSONObject("objectInfo");

                String artistId = artistJson.getString("singerId");
                String artistName = artistJson.getString("singer");
                String coverImgThumbUrl = artistJson.getJSONArray("imgs").getJSONObject(0).getString("img");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.MG);
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(artistInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 来电唱作榜(程序分页)
        Callable<CommonResult<NetArtistInfo>> getArtistRankingMg2 = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            String artistInfoBody = HttpRequest.get(String.format(ARTIST_LIST_MG_API_2))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("columnInfo");
            t = data.getIntValue("contentsCount");
            JSONArray artistArray = data.getJSONArray("contents");
            for (int i = (page - 1) * limit, len = Math.min(artistArray.size(), page * limit); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i).getJSONObject("objectInfo");

                String artistId = artistJson.getString("singerId");
                String artistName = artistJson.getString("singer");
                String coverImgThumbUrl = artistJson.getJSONArray("imgs").getJSONObject(0).getString("img");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.MG);
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(artistInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 千千
        // 推荐歌手
        Callable<CommonResult<NetArtistInfo>> getRecArtistsQi = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = HttpRequest.get(SdkCommon.buildQianUrl(String.format(REC_ARTISTS_LIST_QI_API, System.currentTimeMillis())))
                    .execute();
            String artistInfoBody = resp.body();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONArray("data").getJSONObject(6);
            t = data.getIntValue("module_nums");
            JSONArray artistArray = data.getJSONArray("result");
            for (int i = (page - 1) * limit, len = Math.min(artistArray.size(), page * limit); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i);

                String artistId = artistJson.getString("artistCode");
                String artistName = artistJson.getString("name");
                String coverImgUrl = artistJson.getString("pic");
                Integer songNum = artistJson.getIntValue("trackTotal");
                Integer albumNum = artistJson.getIntValue("albumTotal");
                String coverImgThumbUrl = coverImgUrl;

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.QI);
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setSongNum(songNum);
                artistInfo.setAlbumNum(albumNum);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgUrl);
                    if (coverImgThumb == null) coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(artistInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 分类歌手
        Callable<CommonResult<NetArtistInfo>> getCatArtistsQi = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[7])) {
                // 分割时保留空串
                String[] sp = s[7].split(" ", -1);
                HttpResponse resp = HttpRequest.get(SdkCommon.buildQianUrl(
                                String.format(CAT_ARTISTS_LIST_QI_API, sp[0], sp[2], sp[1], page, limit, System.currentTimeMillis())))
                        .execute();
                String artistInfoBody = resp.body();
                JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
                JSONObject data = artistInfoJson.getJSONObject("data");
                t = data.getIntValue("total");
                JSONArray artistArray = data.getJSONArray("result");
                for (int i = 0, len = artistArray.size(); i < len; i++) {
                    JSONObject artistJson = artistArray.getJSONObject(i);

                    String artistId = artistJson.getString("artistCode");
                    String artistName = artistJson.getString("name");
                    String coverImgUrl = artistJson.getString("pic");
                    String coverImgThumbUrl = coverImgUrl;

                    NetArtistInfo artistInfo = new NetArtistInfo();
                    artistInfo.setSource(NetMusicSource.QI);
                    artistInfo.setId(artistId);
                    artistInfo.setName(artistName);
                    artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgUrl);
                        if (coverImgThumb == null) coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        artistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(artistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 猫耳
        // 分类声优
        Callable<CommonResult<NetArtistInfo>> getCatCVsMe = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[8])) {
                String artistInfoBody = HttpRequest.get(String.format(CAT_CV_ME_API, s[8].trim(), page, limit))
                        .execute()
                        .body();
                JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
                JSONObject info = artistInfoJson.getJSONObject("info");
                t = info.getJSONObject("pagination").getIntValue("count");
                JSONArray artistArray = info.getJSONArray("Datas");
                for (int i = 0, len = artistArray.size(); i < len; i++) {
                    JSONObject artistJson = artistArray.getJSONObject(i);

                    String artistId = artistJson.getString("id");
                    String artistName = artistJson.getString("name");
                    String coverImgThumbUrl = artistJson.getString("avatar");

                    NetArtistInfo artistInfo = new NetArtistInfo();
                    artistInfo.setSource(NetMusicSource.ME);
                    artistInfo.setId(artistId);
                    artistInfo.setName(artistName);
                    artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);

                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        artistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(artistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 分类社团
        Callable<CommonResult<NetArtistInfo>> getCatOrganizationsMe = () -> {
            LinkedList<NetArtistInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[8])) {
                String artistInfoBody = HttpRequest.get(String.format(CAT_ORGANIZATIONS_ME_API, s[8].trim(), page, limit))
                        .execute()
                        .body();
                JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
                JSONObject info = artistInfoJson.getJSONObject("info");
                t = info.getJSONObject("pagination").getIntValue("count");
                JSONArray artistArray = info.getJSONArray("Datas");
                for (int i = 0, len = artistArray.size(); i < len; i++) {
                    JSONObject artistJson = artistArray.getJSONObject(i);

                    String artistId = artistJson.getString("id");
                    String artistName = artistJson.getString("name");
                    String coverImgThumbUrl = artistJson.getString("avatar");

                    NetArtistInfo artistInfo = new NetArtistInfo();
                    artistInfo.setSource(NetMusicSource.ME);
                    artistInfo.setOrganization(true);
                    artistInfo.setId(artistId);
                    artistInfo.setName(artistName);
                    artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);

                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        artistInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(artistInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        List<Future<CommonResult<NetArtistInfo>>> taskList = new LinkedList<>();

        boolean dt = defaultTag.equals(tag);

        if (src == NetMusicSource.NET_CLOUD || src == NetMusicSource.ALL) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getArtistRanking));
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getHotArtist));
            taskList.add(GlobalExecutors.requestExecutor.submit(getCatArtist));
            if (!dt) taskList.add(GlobalExecutors.requestExecutor.submit(getStyleArtist));
        }

        if (src == NetMusicSource.KG || src == NetMusicSource.ALL) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getHotArtistKg));
            taskList.add(GlobalExecutors.requestExecutor.submit(getUpArtistKg));
        }

        if (src == NetMusicSource.QQ || src == NetMusicSource.ALL) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getArtistRankingQq));
        }

        if (src == NetMusicSource.KW || src == NetMusicSource.ALL) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getArtistRankingKw));
            taskList.add(GlobalExecutors.requestExecutor.submit(getAllArtistsKw));
        }

        if (src == NetMusicSource.MG || src == NetMusicSource.ALL) {
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getArtistRankingMg));
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getArtistRankingMg2));
        }

        if (src == NetMusicSource.QI || src == NetMusicSource.ALL) {
            if (dt) taskList.add(GlobalExecutors.requestExecutor.submit(getRecArtistsQi));
            taskList.add(GlobalExecutors.requestExecutor.submit(getCatArtistsQi));
        }

        if (src == NetMusicSource.ME || src == NetMusicSource.ALL) {
            taskList.add(GlobalExecutors.requestExecutor.submit(getCatCVsMe));
            taskList.add(GlobalExecutors.requestExecutor.submit(getCatOrganizationsMe));
        }

        List<List<NetArtistInfo>> rl = new LinkedList<>();
        taskList.forEach(task -> {
            try {
                CommonResult<NetArtistInfo> result = task.get();
                rl.add(result.data);
                total.set(Math.max(total.get(), result.total));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        artistInfos.addAll(ListUtil.joinAll(rl));

        return new CommonResult<>(artistInfos, total.get());
    }
}
