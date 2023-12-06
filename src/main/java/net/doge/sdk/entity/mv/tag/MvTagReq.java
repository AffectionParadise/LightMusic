package net.doge.sdk.entity.mv.tag;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.async.GlobalExecutors;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.Tags;
import net.doge.util.common.RegexUtil;
import net.doge.util.common.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MvTagReq {
    private static MvTagReq instance;

    private MvTagReq() {
    }

    public static MvTagReq getInstance() {
        if (instance == null) instance = new MvTagReq();
        return instance;
    }

    // MV 标签 API (酷狗)
    private final String MV_TAG_KG_API = "http://mobileservice.kugou.com/api/v5/video/recommend_channel?version=9108&type=2";
    // 视频标签 API (发姐)
    private final String VIDEO_TAG_FA_API = "https://www.chatcyf.com/video/";
    // 直播标签 API (发姐)
    private final String LIVE_TAG_FA_API = "https://www.chatcyf.com/teaparty/";
    // 视频标签 API (李志)
    private final String VIDEO_TAG_LZ_API = "https://www.lizhinb.com/sp/";

    /**
     * 加载 MV 标签
     *
     * @return
     */
    public void initMvTag() {
        // 网易云 网易云 酷狗 QQ QQ QQ 酷我 好看 哔哩哔哩 发姐 李志
        Tags.mvTag.put("默认", new String[]{"全部", "全部", "0", "15", "7", "all", "236682871", "", "", " ", ""});

        Tags.mvTag.put("精选", new String[]{"", "", "", "", "", "all", "", "", "", "", ""});
        Tags.mvTag.put("内地", new String[]{"内地", "全部", "", "16", "7", "neidi", "", "", "", "", ""});
        Tags.mvTag.put("港台", new String[]{"港台", "全部", "", "17", "7", "gangtai", "", "", "", "", ""});
        Tags.mvTag.put("欧美", new String[]{"欧美", "全部", "", "18", "7", "oumei", "", "", "", "", ""});
        Tags.mvTag.put("韩国", new String[]{"韩国", "全部", "", "19", "7", "korea", "", "", "", "", ""});
        Tags.mvTag.put("日本", new String[]{"日本", "全部", "", "20", "7", "janpan", "", "", "", "", ""});
        Tags.mvTag.put("官方版", new String[]{"", "官方版", "", "", "", "", "", "", "", "", ""});
//        Tags.mvTag.put("原生", new String[]{"", "原生", "", "", "", "","", "", "", "",""});
        Tags.mvTag.put("现场版", new String[]{"", "现场版", "", "", "", "", "", "", "", "", ""});
        Tags.mvTag.put("网易出品", new String[]{"", "网易出品", "", "", "", "", "", "", "", "", ""});

        // 酷我
        Tags.mvTag.put("华语", new String[]{"", "", "", "", "", "", "236682731", "", "", "", ""});
        Tags.mvTag.put("日韩", new String[]{"", "", "", "", "", "", "236742444", "", "", "", ""});
        Tags.mvTag.put("网络", new String[]{"", "", "", "", "", "", "236682773", "", "", "", ""});
        Tags.mvTag.put("欧美", new String[]{"", "", "", "", "", "", "236682735", "", "", "", ""});
        Tags.mvTag.put("现场", new String[]{"", "", "", "", "", "", "236742576", "", "", "", ""});
        Tags.mvTag.put("热舞", new String[]{"", "", "", "", "", "", "236682777", "", "", "", ""});
        Tags.mvTag.put("伤感", new String[]{"", "", "", "", "", "", "236742508", "", "", "", ""});
        Tags.mvTag.put("剧情", new String[]{"", "", "", "", "", "", "236742578", "", "", "", ""});

        // 好看(部分标签与 b 站合并)
        Tags.mvTag.put("影视", new String[]{"", "", "", "", "", "", "", "yingshi_new", "181", "", ""});
        Tags.mvTag.put("音乐", new String[]{"", "", "", "", "", "", "", "yinyue_new", "3", "", ""});
        Tags.mvTag.put("VLOG", new String[]{"", "", "", "", "", "", "", "yunying_vlog", "", "", ""});
        Tags.mvTag.put("游戏", new String[]{"", "", "", "", "", "", "", "youxi_new", "4", "", ""});
        Tags.mvTag.put("搞笑", new String[]{"", "", "", "", "", "", "", "gaoxiao_new", "138", "", ""});
        Tags.mvTag.put("综艺", new String[]{"", "", "", "", "", "", "", "zongyi_new", "71", "", ""});
        Tags.mvTag.put("娱乐", new String[]{"", "", "", "", "", "", "", "yule_new", "5", "", ""});
        Tags.mvTag.put("动漫", new String[]{"", "", "", "", "", "", "", "dongman_new", "", "", ""});
        Tags.mvTag.put("生活", new String[]{"", "", "", "", "", "", "", "shenghuo_new", "160", "", ""});
        Tags.mvTag.put("广场舞", new String[]{"", "", "", "", "", "", "", "guangchuangwu_new", "", "", ""});
        Tags.mvTag.put("美食", new String[]{"", "", "", "", "", "", "", "meishi_new", "211", "", ""});
        Tags.mvTag.put("宠物", new String[]{"", "", "", "", "", "", "", "chongwu_new", "", "", ""});
        Tags.mvTag.put("三农", new String[]{"", "", "", "", "", "", "", "sannong_new", "251", "", ""});
        Tags.mvTag.put("军事", new String[]{"", "", "", "", "", "", "", "junshi_new", "179", "", ""});
        Tags.mvTag.put("社会", new String[]{"", "", "", "", "", "", "", "shehui_new", "205", "", ""});
        Tags.mvTag.put("体育", new String[]{"", "", "", "", "", "", "", "tiyu_new", "", "", ""});
        Tags.mvTag.put("科技", new String[]{"", "", "", "", "", "", "", "keji_new", "188", "", ""});
        Tags.mvTag.put("时尚", new String[]{"", "", "", "", "", "", "", "shishang_new", "155", "", ""});
        Tags.mvTag.put("汽车", new String[]{"", "", "", "", "", "", "", "qiche_new", "223", "", ""});
        Tags.mvTag.put("亲子", new String[]{"", "", "", "", "", "", "", "qinzi_new", "", "", ""});
        Tags.mvTag.put("文化", new String[]{"", "", "", "", "", "", "", "wenhua_new", "", "", ""});
        Tags.mvTag.put("旅游", new String[]{"", "", "", "", "", "", "", "lvyou_new", "", "", ""});
        Tags.mvTag.put("秒懂", new String[]{"", "", "", "", "", "", "", "yunying_miaodong", "", "", ""});

        // 哔哩哔哩
        Tags.mvTag.put("动画", new String[]{"", "", "", "", "", "", "", "", "1", "", ""});
        Tags.mvTag.put("MAD·AMV", new String[]{"", "", "", "", "", "", "", "", "24", "", ""});
        Tags.mvTag.put("MMD·3D", new String[]{"", "", "", "", "", "", "", "", "25", "", ""});
        Tags.mvTag.put("短片·手书·配音", new String[]{"", "", "", "", "", "", "", "", "47", "", ""});
        Tags.mvTag.put("手办·模玩", new String[]{"", "", "", "", "", "", "", "", "210", "", ""});
        Tags.mvTag.put("特摄", new String[]{"", "", "", "", "", "", "", "", "86", "", ""});
        Tags.mvTag.put("综合", new String[]{"", "", "", "", "", "", "", "", "27", "", ""});
        Tags.mvTag.put("番剧", new String[]{"", "", "", "", "", "", "", "", "13", "", ""});
        Tags.mvTag.put("资讯", new String[]{"", "", "", "", "", "", "", "", "51", "", ""});
        Tags.mvTag.put("官方延伸", new String[]{"", "", "", "", "", "", "", "", "152", "", ""});
        Tags.mvTag.put("完结动画", new String[]{"", "", "", "", "", "", "", "", "32", "", ""});
        Tags.mvTag.put("连载动画", new String[]{"", "", "", "", "", "", "", "", "33", "", ""});
        Tags.mvTag.put("国创", new String[]{"", "", "", "", "", "", "", "", "167", "", ""});
        Tags.mvTag.put("国产动画", new String[]{"", "", "", "", "", "", "", "", "153", "", ""});
        Tags.mvTag.put("国产原创相关", new String[]{"", "", "", "", "", "", "", "", "168", "", ""});
        Tags.mvTag.put("布袋戏", new String[]{"", "", "", "", "", "", "", "", "169", "", ""});
        Tags.mvTag.put("资讯", new String[]{"", "", "", "", "", "", "", "", "170", "", ""});
        Tags.mvTag.put("动态漫·广播剧", new String[]{"", "", "", "", "", "", "", "", "195", "", ""});
//        Tags.mvTag.put("音乐", new String[]{"", "", "", "", "", "","", "", "3", "",""});
        Tags.mvTag.put("原创音乐", new String[]{"", "", "", "", "", "", "", "", "28", "", ""});
        Tags.mvTag.put("翻唱", new String[]{"", "", "", "", "", "", "", "", "31", "", ""});
        Tags.mvTag.put("VOCALOID·UTAU", new String[]{"", "", "", "", "", "", "", "", "30", "", ""});
//        Tags.mvTag.put("电音 (已下线)", new String[]{"", "", "", "", "", "","", "", "194", "",""});
        Tags.mvTag.put("演奏", new String[]{"", "", "", "", "", "", "", "", "59", "", ""});
        Tags.mvTag.put("MV", new String[]{"", "", "", "", "", "", "", "", "193", "", ""});
        Tags.mvTag.put("音乐现场", new String[]{"", "", "", "", "", "", "", "", "29", "", ""});
        Tags.mvTag.put("音乐综合", new String[]{"", "", "", "", "", "", "", "", "130", "", ""});
        Tags.mvTag.put("乐评盘点", new String[]{"", "", "", "", "", "", "", "", "243", "", ""});
        Tags.mvTag.put("音乐教学", new String[]{"", "", "", "", "", "", "", "", "244", "", ""});
        Tags.mvTag.put("舞蹈", new String[]{"", "", "", "", "", "", "", "", "129", "", ""});
        Tags.mvTag.put("宅舞", new String[]{"", "", "", "", "", "", "", "", "20", "", ""});
        Tags.mvTag.put("舞蹈综合", new String[]{"", "", "", "", "", "", "", "", "154", "", ""});
        Tags.mvTag.put("舞蹈教程", new String[]{"", "", "", "", "", "", "", "", "156", "", ""});
        Tags.mvTag.put("街舞", new String[]{"", "", "", "", "", "", "", "", "198", "", ""});
        Tags.mvTag.put("明星舞蹈", new String[]{"", "", "", "", "", "", "", "", "199", "", ""});
        Tags.mvTag.put("中国舞", new String[]{"", "", "", "", "", "", "", "", "200", "", ""});
//        Tags.mvTag.put("游戏", new String[]{"", "", "", "", "", "","", "", "4", "",""});
        Tags.mvTag.put("单机游戏", new String[]{"", "", "", "", "", "", "", "", "17", "", ""});
        Tags.mvTag.put("电子竞技", new String[]{"", "", "", "", "", "", "", "", "171", "", ""});
        Tags.mvTag.put("手机游戏", new String[]{"", "", "", "", "", "", "", "", "172", "", ""});
        Tags.mvTag.put("网络游戏", new String[]{"", "", "", "", "", "", "", "", "65", "", ""});
        Tags.mvTag.put("桌游棋牌", new String[]{"", "", "", "", "", "", "", "", "173", "", ""});
        Tags.mvTag.put("GMV", new String[]{"", "", "", "", "", "", "", "", "121", "", ""});
        Tags.mvTag.put("音游", new String[]{"", "", "", "", "", "", "", "", "136", "", ""});
        Tags.mvTag.put("Mugen", new String[]{"", "", "", "", "", "", "", "", "19", "", ""});
        Tags.mvTag.put("知识", new String[]{"", "", "", "", "", "", "", "", "36", "", ""});
        Tags.mvTag.put("科学科普", new String[]{"", "", "", "", "", "", "", "", "201", "", ""});
        Tags.mvTag.put("社科·法律·心理", new String[]{"", "", "", "", "", "", "", "", "124", "", ""});
        Tags.mvTag.put("人文历史", new String[]{"", "", "", "", "", "", "", "", "228", "", ""});
        Tags.mvTag.put("财经商业", new String[]{"", "", "", "", "", "", "", "", "207", "", ""});
        Tags.mvTag.put("校园学习", new String[]{"", "", "", "", "", "", "", "", "208", "", ""});
        Tags.mvTag.put("职业职场", new String[]{"", "", "", "", "", "", "", "", "209", "", ""});
        Tags.mvTag.put("设计·创意", new String[]{"", "", "", "", "", "", "", "", "229", "", ""});
        Tags.mvTag.put("野生技术协会", new String[]{"", "", "", "", "", "", "", "", "122", "", ""});
//        Tags.mvTag.put("演讲·公开课 (已下线)", new String[]{"", "", "", "", "", "","", "", "39", "",""});
//        Tags.mvTag.put("星海 (已下线)", new String[]{"", "", "", "", "", "","", "", "96", "",""});
//        Tags.mvTag.put("机械 (已下线)", new String[]{"", "", "", "", "", "","", "", "98", "",""});
//        Tags.mvTag.put("科技", new String[]{"", "", "", "", "", "","", "", "188", "",""});
        Tags.mvTag.put("数码", new String[]{"", "", "", "", "", "", "", "", "95", "", ""});
        Tags.mvTag.put("软件应用", new String[]{"", "", "", "", "", "", "", "", "230", "", ""});
        Tags.mvTag.put("计算机技术", new String[]{"", "", "", "", "", "", "", "", "231", "", ""});
        Tags.mvTag.put("工业·工程·机械", new String[]{"", "", "", "", "", "", "", "", "232", "", ""});
        Tags.mvTag.put("极客DIY", new String[]{"", "", "", "", "", "", "", "", "233", "", ""});
//        Tags.mvTag.put("电脑装机 (已下线)", new String[]{"", "", "", "", "", "","", "", "189", "",""});
//        Tags.mvTag.put("摄影摄像 (已下线)", new String[]{"", "", "", "", "", "","", "", "190", "",""});
//        Tags.mvTag.put("影音智能 (已下线)", new String[]{"", "", "", "", "", "","", "", "191", "",""});
        Tags.mvTag.put("运动", new String[]{"", "", "", "", "", "", "", "", "234", "", ""});
        Tags.mvTag.put("篮球", new String[]{"", "", "", "", "", "", "", "", "235", "", ""});
        Tags.mvTag.put("足球", new String[]{"", "", "", "", "", "", "", "", "249", "", ""});
        Tags.mvTag.put("健身", new String[]{"", "", "", "", "", "", "", "", "164", "", ""});
        Tags.mvTag.put("竞技体育", new String[]{"", "", "", "", "", "", "", "", "236", "", ""});
        Tags.mvTag.put("运动文化", new String[]{"", "", "", "", "", "", "", "", "237", "", ""});
        Tags.mvTag.put("运动综合", new String[]{"", "", "", "", "", "", "", "", "238", "", ""});
//        Tags.mvTag.put("汽车", new String[]{"", "", "", "", "", "","", "", "223", "",""});
        Tags.mvTag.put("赛车", new String[]{"", "", "", "", "", "", "", "", "245", "", ""});
        Tags.mvTag.put("改装玩车", new String[]{"", "", "", "", "", "", "", "", "246", "", ""});
        Tags.mvTag.put("新能源车", new String[]{"", "", "", "", "", "", "", "", "247", "", ""});
        Tags.mvTag.put("房车", new String[]{"", "", "", "", "", "", "", "", "248", "", ""});
        Tags.mvTag.put("摩托车", new String[]{"", "", "", "", "", "", "", "", "240", "", ""});
        Tags.mvTag.put("购车攻略", new String[]{"", "", "", "", "", "", "", "", "227", "", ""});
        Tags.mvTag.put("汽车生活", new String[]{"", "", "", "", "", "", "", "", "176", "", ""});
//        Tags.mvTag.put("汽车文化 (已下线)", new String[]{"", "", "", "", "", "","", "", "224", "", ""});
//        Tags.mvTag.put("汽车极客 (已下线)", new String[]{"", "", "", "", "", "","", "", "225", "", ""});
//        Tags.mvTag.put("智能出行 (已下线)", new String[]{"", "", "", "", "", "","", "", "226", "", ""});
//        Tags.mvTag.put("生活", new String[]{"", "", "", "", "", "","", "", "160", "", ""});
//        Tags.mvTag.put("搞笑", new String[]{"", "", "", "", "", "","", "", "138", "", ""});
        Tags.mvTag.put("出行", new String[]{"", "", "", "", "", "", "", "", "250", "", ""});
//        Tags.mvTag.put("三农", new String[]{"", "", "", "", "", "","", "", "251", "", ""});
        Tags.mvTag.put("家居房产", new String[]{"", "", "", "", "", "", "", "", "239", "", ""});
        Tags.mvTag.put("手工", new String[]{"", "", "", "", "", "", "", "", "161", "", ""});
        Tags.mvTag.put("绘画", new String[]{"", "", "", "", "", "", "", "", "162", "", ""});
        Tags.mvTag.put("日常", new String[]{"", "", "", "", "", "", "", "", "21", "", ""});
//        Tags.mvTag.put("美食圈 (重定向)", new String[]{"", "", "", "", "", "","", "", "76", "", ""});
//        Tags.mvTag.put("动物圈 (重定向)", new String[]{"", "", "", "", "", "","", "", "75", "", ""});
//        Tags.mvTag.put("运动 (重定向)", new String[]{"", "", "", "", "", "","", "", "163", "", ""});
//        Tags.mvTag.put("汽车 (重定向)", new String[]{"", "", "", "", "", "","", "", "176", "", ""});
//        Tags.mvTag.put("其他 (已下线)", new String[]{"", "", "", "", "", "","", "", "174", "", ""});
//        Tags.mvTag.put("美食", new String[]{"", "", "", "", "", "","", "", "211", "", ""});
        Tags.mvTag.put("美食制作", new String[]{"", "", "", "", "", "", "", "", "76", "", ""});
        Tags.mvTag.put("美食侦探", new String[]{"", "", "", "", "", "", "", "", "212", "", ""});
        Tags.mvTag.put("美食测评", new String[]{"", "", "", "", "", "", "", "", "213", "", ""});
        Tags.mvTag.put("田园美食", new String[]{"", "", "", "", "", "", "", "", "214", "", ""});
        Tags.mvTag.put("美食记录", new String[]{"", "", "", "", "", "", "", "", "215", "", ""});
        Tags.mvTag.put("动物圈", new String[]{"", "", "", "", "", "", "", "", "217", "", ""});
        Tags.mvTag.put("喵星人", new String[]{"", "", "", "", "", "", "", "", "218", "", ""});
        Tags.mvTag.put("汪星人", new String[]{"", "", "", "", "", "", "", "", "219", "", ""});
        Tags.mvTag.put("野生动物", new String[]{"", "", "", "", "", "", "", "", "221", "", ""});
        Tags.mvTag.put("爬宠", new String[]{"", "", "", "", "", "", "", "", "222", "", ""});
        Tags.mvTag.put("大熊猫", new String[]{"", "", "", "", "", "", "", "", "220", "", ""});
        Tags.mvTag.put("动物综合", new String[]{"", "", "", "", "", "", "", "", "75", "", ""});
        Tags.mvTag.put("鬼畜", new String[]{"", "", "", "", "", "", "", "", "119", "", ""});
        Tags.mvTag.put("鬼畜调教", new String[]{"", "", "", "", "", "", "", "", "22", "", ""});
        Tags.mvTag.put("音MAD", new String[]{"", "", "", "", "", "", "", "", "26", "", ""});
        Tags.mvTag.put("人力VOCALOID", new String[]{"", "", "", "", "", "", "", "", "126", "", ""});
        Tags.mvTag.put("鬼畜剧场", new String[]{"", "", "", "", "", "", "", "", "216", "", ""});
        Tags.mvTag.put("教程演示", new String[]{"", "", "", "", "", "", "", "", "127", "", ""});
//        Tags.mvTag.put("时尚", new String[]{"", "", "", "", "", "","", "", "155", "", ""});
        Tags.mvTag.put("美妆护肤", new String[]{"", "", "", "", "", "", "", "", "157", "", ""});
        Tags.mvTag.put("仿妆cos", new String[]{"", "", "", "", "", "", "", "", "252", "", ""});
        Tags.mvTag.put("穿搭", new String[]{"", "", "", "", "", "", "", "", "158", "", ""});
//        Tags.mvTag.put("健身 (重定向)", new String[]{"", "", "", "", "", "","", "", "164", "", ""});
        Tags.mvTag.put("时尚潮流", new String[]{"", "", "", "", "", "", "", "", "159", "", ""});
//        Tags.mvTag.put("风尚标 (已下线)", new String[]{"", "", "", "", "", "","", "", "192", "", ""});
        Tags.mvTag.put("资讯", new String[]{"", "", "", "", "", "", "", "", "202", "", ""});
        Tags.mvTag.put("热点", new String[]{"", "", "", "", "", "", "", "", "203", "", ""});
        Tags.mvTag.put("环球", new String[]{"", "", "", "", "", "", "", "", "204", "", ""});
//        Tags.mvTag.put("社会", new String[]{"", "", "", "", "", "","", "", "205", "", ""});
        Tags.mvTag.put("综合", new String[]{"", "", "", "", "", "", "", "", "206", "", ""});
//        Tags.mvTag.put("广告", new String[]{"", "", "", "", "", "","", "", "165", "", ""});
//        Tags.mvTag.put("广告 (已下线)", new String[]{"", "", "", "", "", "","", "", "166", "", ""});
//        Tags.mvTag.put("娱乐", new String[]{"", "", "", "", "", "","", "", "5", "", ""});
//        Tags.mvTag.put("综艺", new String[]{"", "", "", "", "", "","", "", "71", "", ""});
        Tags.mvTag.put("娱乐杂谈", new String[]{"", "", "", "", "", "", "", "", "241", "", ""});
        Tags.mvTag.put("粉丝创作", new String[]{"", "", "", "", "", "", "", "", "242", "", ""});
        Tags.mvTag.put("明星综合", new String[]{"", "", "", "", "", "", "", "", "137", "", ""});
//        Tags.mvTag.put("Korea相关 (已下线)", new String[]{"", "", "", "", "", "","", "", "131", "", ""});
//        Tags.mvTag.put("影视", new String[]{"", "", "", "", "", "","", "", "181", "", ""});
        Tags.mvTag.put("影视杂谈", new String[]{"", "", "", "", "", "", "", "", "182", "", ""});
        Tags.mvTag.put("影视剪辑", new String[]{"", "", "", "", "", "", "", "", "183", "", ""});
        Tags.mvTag.put("小剧场", new String[]{"", "", "", "", "", "", "", "", "85", "", ""});
        Tags.mvTag.put("预告·资讯", new String[]{"", "", "", "", "", "", "", "", "184", "", ""});
        Tags.mvTag.put("纪录片", new String[]{"", "", "", "", "", "", "", "", "177", "", ""});
        Tags.mvTag.put("人文·历史", new String[]{"", "", "", "", "", "", "", "", "37", "", ""});
        Tags.mvTag.put("科学·探索·自然", new String[]{"", "", "", "", "", "", "", "", "178", "", ""});
//        Tags.mvTag.put("军事", new String[]{"", "", "", "", "", "","", "", "179", "", ""});
        Tags.mvTag.put("社会·美食·旅行", new String[]{"", "", "", "", "", "", "", "", "180", "", ""});
        Tags.mvTag.put("电影", new String[]{"", "", "", "", "", "", "", "", "23", "", ""});
        Tags.mvTag.put("华语电影", new String[]{"", "", "", "", "", "", "", "", "147", "", ""});
        Tags.mvTag.put("欧美电影", new String[]{"", "", "", "", "", "", "", "", "145", "", ""});
        Tags.mvTag.put("日本电影", new String[]{"", "", "", "", "", "", "", "", "146", "", ""});
        Tags.mvTag.put("其他国家", new String[]{"", "", "", "", "", "", "", "", "83", "", ""});
        Tags.mvTag.put("电视剧", new String[]{"", "", "", "", "", "", "", "", "11", "", ""});
        Tags.mvTag.put("国产剧", new String[]{"", "", "", "", "", "", "", "", "185", "", ""});
        Tags.mvTag.put("海外剧", new String[]{"", "", "", "", "", "", "", "", "187", "", ""});

        final int c = 11;
        // 酷狗
        // MV 标签
        Runnable initMvTagKg = () -> {
            String mvTagBody = HttpRequest.get(MV_TAG_KG_API)
                    .executeAsync()
                    .body();
            JSONObject mvTagJson = JSONObject.parseObject(mvTagBody);
            JSONArray tags = mvTagJson.getJSONObject("data").getJSONArray("list");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONObject tagJson = tags.getJSONObject(i);

                String name = tagJson.getString("name");
                String id = tagJson.getString("channel_id");

                if (!Tags.mvTag.containsKey(name)) Tags.mvTag.put(name, new String[c]);
                Tags.mvTag.get(name)[2] = id;
            }
        };
        // QQ
        // MV 标签
        Runnable initMvTagQq = () -> {
            String mvTagBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                    .body("{\"comm\":{\"ct\":24},\"mv_tag\":{\"module\":\"MvService.MvInfoProServer\",\"method\":\"GetAllocTag\",\"param\":{}}}")
                    .executeAsync()
                    .body();
            JSONObject mvTagJson = JSONObject.parseObject(mvTagBody);
            JSONArray tags = mvTagJson.getJSONObject("mv_tag").getJSONObject("data").getJSONArray("version");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONObject tagJson = tags.getJSONObject(i);

                String name = tagJson.getString("name");
                if ("全部".equals(name)) continue;
                String id = tagJson.getString("id");

                if (!Tags.mvTag.containsKey(name)) Tags.mvTag.put(name, new String[c]);
                Tags.mvTag.get(name)[3] = "15";
                Tags.mvTag.get(name)[4] = id;
            }
        };
        // 发姐
        // 视频标签
        Runnable initVideoTagFa = () -> {
            String mvTagBody = HttpRequest.get(VIDEO_TAG_FA_API)
                    .executeAsync()
                    .body();
            Document doc = Jsoup.parse(mvTagBody);
            Elements tags = doc.select(".filter-item .filter a");
            for (int i = 0, len = tags.size(); i < len; i++) {
                Element a = tags.get(i);

                String id = RegexUtil.getGroup1("c2=(\\d+)", a.attr("href"));
                if (StringUtil.isEmpty(id)) continue;
                String name = a.text();

                if (!Tags.mvTag.containsKey(name)) Tags.mvTag.put(name, new String[c]);
                Tags.mvTag.get(name)[9] = id + " ";
            }
        };
        // 直播标签
        Runnable initLiveTagFa = () -> {
            String mvTagBody = HttpRequest.get(LIVE_TAG_FA_API)
                    .executeAsync()
                    .body();
            Document doc = Jsoup.parse(mvTagBody);
            Elements tags = doc.select(".filter-item .filter a");
            for (int i = 0, len = tags.size(); i < len; i++) {
                Element a = tags.get(i);

                String id = RegexUtil.getGroup1("c2=(\\d+)", a.attr("href"));
                if (StringUtil.isEmpty(id)) continue;
                String name = a.text();

                if (!Tags.mvTag.containsKey(name)) Tags.mvTag.put(name, new String[c]);
                Tags.mvTag.get(name)[9] = " " + id;
            }
        };

        // 李志
        Runnable initVideoTagLz = () -> {
            String mvTagBody = HttpRequest.get(VIDEO_TAG_LZ_API)
                    .executeAsync()
                    .body();
            Document doc = Jsoup.parse(mvTagBody);
            Elements tags = doc.select(".zaxu-friendly-link-content");
            for (int i = 0, len = tags.size(); i < len; i++) {
                Element content = tags.get(i);
                Elements a = content.select("a");
                Elements n = content.select(".zaxu-friendly-link-name");

                String id = RegexUtil.getGroup1("/live-category/(.*?)/", a.attr("href"));
                String name = n.text();

                if (!Tags.mvTag.containsKey(name)) Tags.mvTag.put(name, new String[c]);
                Tags.mvTag.get(name)[10] = id;
            }
        };

        List<Future<?>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(initMvTagKg));
        taskList.add(GlobalExecutors.requestExecutor.submit(initMvTagQq));
        taskList.add(GlobalExecutors.requestExecutor.submit(initVideoTagFa));
        taskList.add(GlobalExecutors.requestExecutor.submit(initLiveTagFa));
        taskList.add(GlobalExecutors.requestExecutor.submit(initVideoTagLz));

        taskList.forEach(task -> {
            try {
                task.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
}
