package net.doge.sdk.service.mv.tag;

import net.doge.constant.core.data.Tags;
import net.doge.sdk.common.entity.executor.MultiRunnableExecutor;
import net.doge.sdk.service.mv.tag.impl.FaMvTagReq;
import net.doge.sdk.service.mv.tag.impl.KgMvTagReq;
import net.doge.sdk.service.mv.tag.impl.LzMvTagReq;
import net.doge.sdk.service.mv.tag.impl.QqMvTagReq;

public class MvTagReq {
    private static MvTagReq instance;

    private MvTagReq() {
    }

    public static MvTagReq getInstance() {
        if (instance == null) instance = new MvTagReq();
        return instance;
    }

    /**
     * 加载 MV 标签
     *
     * @return
     */
    public void initMvTag() {
        // 网易云 网易云 酷狗 酷狗 QQ QQ QQ 酷我 好看 哔哩哔哩 发姐 李志
        Tags.mvTags.put("默认", new String[]{"全部", "全部", "0", "", "15", "7", "all", "236682871", "", "", " ", ""});

        Tags.mvTags.put("精选", new String[]{"", "", "", "", "", "", "all", "", "", "", "", ""});
        Tags.mvTags.put("内地", new String[]{"内地", "全部", "", "", "16", "7", "neidi", "", "", "", "", ""});
        Tags.mvTags.put("港台", new String[]{"港台", "全部", "", "", "17", "7", "gangtai", "", "", "", "", ""});
        Tags.mvTags.put("欧美", new String[]{"欧美", "全部", "", "", "18", "7", "oumei", "236682735", "", "", "", ""});
        Tags.mvTags.put("韩国", new String[]{"韩国", "全部", "", "", "19", "7", "korea", "", "", "", "", ""});
        Tags.mvTags.put("日本", new String[]{"日本", "全部", "", "", "20", "7", "janpan", "", "", "", "", ""});
        Tags.mvTags.put("官方版", new String[]{"", "官方版", "", "", "", "", "", "", "", "", "", ""});
//        Tags.mvTag.put("原生", new String[]{"", "原生", "", "", "", "", "","", "", "", "", ""});
        Tags.mvTags.put("现场版", new String[]{"", "现场版", "", "", "", "", "", "", "", "", "", ""});
        Tags.mvTags.put("网易出品", new String[]{"", "网易出品", "", "", "", "", "", "", "", "", "", ""});

        // 酷我
        Tags.mvTags.put("华语", new String[]{"", "", "", "", "", "", "", "236682731", "", "", "", ""});
        Tags.mvTags.put("日韩", new String[]{"", "", "", "", "", "", "", "236742444", "", "", "", ""});
        Tags.mvTags.put("网络", new String[]{"", "", "", "", "", "", "", "236682773", "", "", "", ""});
//        Tags.mvTag.put("欧美", new String[]{"", "", "", "", "", "", "", "236682735", "", "", "", ""});
        Tags.mvTags.put("现场", new String[]{"", "", "", "", "", "", "", "236742576", "", "", "", ""});
        Tags.mvTags.put("热舞", new String[]{"", "", "", "", "", "", "", "236682777", "", "", "", ""});
        Tags.mvTags.put("伤感", new String[]{"", "", "", "", "", "", "", "236742508", "", "", "", ""});
        Tags.mvTags.put("剧情", new String[]{"", "", "", "", "", "", "", "236742578", "", "", "", ""});

        // 好看(部分标签与 b 站合并)
        Tags.mvTags.put("影视", new String[]{"", "", "", "", "", "", "", "", "yingshi_new", "181", "", ""});
        Tags.mvTags.put("音乐", new String[]{"", "", "", "", "", "", "", "", "yinyue_new", "3", "", ""});
        Tags.mvTags.put("VLOG", new String[]{"", "", "", "", "", "", "", "", "yunying_vlog", "", "", ""});
        Tags.mvTags.put("游戏", new String[]{"", "", "", "", "", "", "", "", "youxi_new", "4", "", ""});
        Tags.mvTags.put("搞笑", new String[]{"", "", "", "", "", "", "", "", "gaoxiao_new", "138", "", ""});
        Tags.mvTags.put("综艺", new String[]{"", "", "", "", "", "", "", "", "zongyi_new", "71", "", ""});
        Tags.mvTags.put("娱乐", new String[]{"", "", "", "", "", "", "", "", "yule_new", "5", "", ""});
        Tags.mvTags.put("动漫", new String[]{"", "", "", "", "", "", "", "", "dongman_new", "", "", ""});
        Tags.mvTags.put("生活", new String[]{"", "", "", "", "", "", "", "", "shenghuo_new", "160", "", ""});
        Tags.mvTags.put("广场舞", new String[]{"", "", "", "", "", "", "", "", "guangchuangwu_new", "", "", ""});
        Tags.mvTags.put("美食", new String[]{"", "", "", "", "", "", "", "", "meishi_new", "211", "", ""});
        Tags.mvTags.put("宠物", new String[]{"", "", "", "", "", "", "", "", "chongwu_new", "", "", ""});
        Tags.mvTags.put("三农", new String[]{"", "", "", "", "", "", "", "", "sannong_new", "251", "", ""});
        Tags.mvTags.put("军事", new String[]{"", "", "", "", "", "", "", "", "junshi_new", "179", "", ""});
        Tags.mvTags.put("社会", new String[]{"", "", "", "", "", "", "", "", "shehui_new", "205", "", ""});
        Tags.mvTags.put("体育", new String[]{"", "", "", "", "", "", "", "", "tiyu_new", "", "", ""});
        Tags.mvTags.put("科技", new String[]{"", "", "", "", "", "", "", "", "keji_new", "188", "", ""});
        Tags.mvTags.put("时尚", new String[]{"", "", "", "", "", "", "", "", "shishang_new", "155", "", ""});
        Tags.mvTags.put("汽车", new String[]{"", "", "", "", "", "", "", "", "qiche_new", "223", "", ""});
        Tags.mvTags.put("亲子", new String[]{"", "", "", "", "", "", "", "", "qinzi_new", "", "", ""});
        Tags.mvTags.put("文化", new String[]{"", "", "", "", "", "", "", "", "wenhua_new", "", "", ""});
        Tags.mvTags.put("旅游", new String[]{"", "", "", "", "", "", "", "", "lvyou_new", "", "", ""});
        Tags.mvTags.put("秒懂", new String[]{"", "", "", "", "", "", "", "", "yunying_miaodong", "", "", ""});

        // 哔哩哔哩
        Tags.mvTags.put("动画", new String[]{"", "", "", "", "", "", "", "", "", "1", "", ""});
        Tags.mvTags.put("MAD·AMV", new String[]{"", "", "", "", "", "", "", "", "", "24", "", ""});
        Tags.mvTags.put("MMD·3D", new String[]{"", "", "", "", "", "", "", "", "", "25", "", ""});
        Tags.mvTags.put("短片·手书·配音", new String[]{"", "", "", "", "", "", "", "", "", "47", "", ""});
        Tags.mvTags.put("手办·模玩", new String[]{"", "", "", "", "", "", "", "", "", "210", "", ""});
        Tags.mvTags.put("特摄", new String[]{"", "", "", "", "", "", "", "", "", "86", "", ""});
        Tags.mvTags.put("综合", new String[]{"", "", "", "", "", "", "", "", "", "27", "", ""});
        Tags.mvTags.put("番剧", new String[]{"", "", "", "", "", "", "", "", "", "13", "", ""});
        Tags.mvTags.put("资讯", new String[]{"", "", "", "", "", "", "", "", "", "51", "", ""});
        Tags.mvTags.put("官方延伸", new String[]{"", "", "", "", "", "", "", "", "", "152", "", ""});
        Tags.mvTags.put("完结动画", new String[]{"", "", "", "", "", "", "", "", "", "32", "", ""});
        Tags.mvTags.put("连载动画", new String[]{"", "", "", "", "", "", "", "", "", "33", "", ""});
        Tags.mvTags.put("国创", new String[]{"", "", "", "", "", "", "", "", "", "167", "", ""});
        Tags.mvTags.put("国产动画", new String[]{"", "", "", "", "", "", "", "", "", "153", "", ""});
        Tags.mvTags.put("国产原创相关", new String[]{"", "", "", "", "", "", "", "", "", "168", "", ""});
        Tags.mvTags.put("布袋戏", new String[]{"", "", "", "", "", "", "", "", "", "169", "", ""});
        Tags.mvTags.put("资讯", new String[]{"", "", "", "", "", "", "", "", "", "170", "", ""});
        Tags.mvTags.put("动态漫·广播剧", new String[]{"", "", "", "", "", "", "", "", "", "195", "", ""});
//        Tags.mvTag.put("音乐", new String[]{"", "", "", "", "", "", "", "", "", "3", "",""});
        Tags.mvTags.put("原创音乐", new String[]{"", "", "", "", "", "", "", "", "", "28", "", ""});
        Tags.mvTags.put("翻唱", new String[]{"", "", "", "", "", "", "", "", "", "31", "", ""});
        Tags.mvTags.put("VOCALOID·UTAU", new String[]{"", "", "", "", "", "", "", "", "", "30", "", ""});
//        Tags.mvTag.put("电音 (已下线)", new String[]{"", "", "", "", "", "", "", "", "", "194", "",""});
        Tags.mvTags.put("演奏", new String[]{"", "", "", "", "", "", "", "", "", "59", "", ""});
        Tags.mvTags.put("MV", new String[]{"", "", "", "", "", "", "", "", "", "193", "", ""});
        Tags.mvTags.put("音乐现场", new String[]{"", "", "", "", "", "", "", "", "", "29", "", ""});
        Tags.mvTags.put("音乐综合", new String[]{"", "", "", "", "", "", "", "", "", "130", "", ""});
        Tags.mvTags.put("乐评盘点", new String[]{"", "", "", "", "", "", "", "", "", "243", "", ""});
        Tags.mvTags.put("音乐教学", new String[]{"", "", "", "", "", "", "", "", "", "244", "", ""});
        Tags.mvTags.put("舞蹈", new String[]{"", "", "", "", "", "", "", "", "", "129", "", ""});
        Tags.mvTags.put("宅舞", new String[]{"", "", "", "", "", "", "", "", "", "20", "", ""});
        Tags.mvTags.put("舞蹈综合", new String[]{"", "", "", "", "", "", "", "", "", "154", "", ""});
        Tags.mvTags.put("舞蹈教程", new String[]{"", "", "", "", "", "", "", "", "", "156", "", ""});
        Tags.mvTags.put("街舞", new String[]{"", "", "", "", "", "", "", "", "", "198", "", ""});
        Tags.mvTags.put("明星舞蹈", new String[]{"", "", "", "", "", "", "", "", "", "199", "", ""});
        Tags.mvTags.put("中国舞", new String[]{"", "", "", "", "", "", "", "", "", "200", "", ""});
//        Tags.mvTag.put("游戏", new String[]{"", "", "", "", "", "", "", "", "", "4", "",""});
        Tags.mvTags.put("单机游戏", new String[]{"", "", "", "", "", "", "", "", "", "17", "", ""});
        Tags.mvTags.put("电子竞技", new String[]{"", "", "", "", "", "", "", "", "", "171", "", ""});
        Tags.mvTags.put("手机游戏", new String[]{"", "", "", "", "", "", "", "", "", "172", "", ""});
        Tags.mvTags.put("网络游戏", new String[]{"", "", "", "", "", "", "", "", "", "65", "", ""});
        Tags.mvTags.put("桌游棋牌", new String[]{"", "", "", "", "", "", "", "", "", "173", "", ""});
        Tags.mvTags.put("GMV", new String[]{"", "", "", "", "", "", "", "", "", "121", "", ""});
        Tags.mvTags.put("音游", new String[]{"", "", "", "", "", "", "", "", "", "136", "", ""});
        Tags.mvTags.put("Mugen", new String[]{"", "", "", "", "", "", "", "", "", "19", "", ""});
        Tags.mvTags.put("知识", new String[]{"", "", "", "", "", "", "", "", "", "36", "", ""});
        Tags.mvTags.put("科学科普", new String[]{"", "", "", "", "", "", "", "", "", "201", "", ""});
        Tags.mvTags.put("社科·法律·心理", new String[]{"", "", "", "", "", "", "", "", "", "124", "", ""});
        Tags.mvTags.put("人文历史", new String[]{"", "", "", "", "", "", "", "", "", "228", "", ""});
        Tags.mvTags.put("财经商业", new String[]{"", "", "", "", "", "", "", "", "", "207", "", ""});
        Tags.mvTags.put("校园学习", new String[]{"", "", "", "", "", "", "", "", "", "208", "", ""});
        Tags.mvTags.put("职业职场", new String[]{"", "", "", "", "", "", "", "", "", "209", "", ""});
        Tags.mvTags.put("设计·创意", new String[]{"", "", "", "", "", "", "", "", "", "229", "", ""});
        Tags.mvTags.put("野生技术协会", new String[]{"", "", "", "", "", "", "", "", "", "122", "", ""});
//        Tags.mvTag.put("演讲·公开课 (已下线)", new String[]{"", "", "", "", "", "", "", "", "", "39", "",""});
//        Tags.mvTag.put("星海 (已下线)", new String[]{"", "", "", "", "", "", "", "", "", "96", "",""});
//        Tags.mvTag.put("机械 (已下线)", new String[]{"", "", "", "", "", "", "", "", "", "98", "",""});
//        Tags.mvTag.put("科技", new String[]{"", "", "", "", "", "", "", "", "", "188", "",""});
        Tags.mvTags.put("数码", new String[]{"", "", "", "", "", "", "", "", "", "95", "", ""});
        Tags.mvTags.put("软件应用", new String[]{"", "", "", "", "", "", "", "", "", "230", "", ""});
        Tags.mvTags.put("计算机技术", new String[]{"", "", "", "", "", "", "", "", "", "231", "", ""});
        Tags.mvTags.put("工业·工程·机械", new String[]{"", "", "", "", "", "", "", "", "", "232", "", ""});
        Tags.mvTags.put("极客DIY", new String[]{"", "", "", "", "", "", "", "", "", "233", "", ""});
//        Tags.mvTag.put("电脑装机 (已下线)", new String[]{"", "", "", "", "", "", "", "", "", "189", "",""});
//        Tags.mvTag.put("摄影摄像 (已下线)", new String[]{"", "", "", "", "", "", "", "", "", "190", "",""});
//        Tags.mvTag.put("影音智能 (已下线)", new String[]{"", "", "", "", "", "", "", "", "", "191", "",""});
        Tags.mvTags.put("运动", new String[]{"", "", "", "", "", "", "", "", "", "234", "", ""});
        Tags.mvTags.put("篮球", new String[]{"", "", "", "", "", "", "", "", "", "235", "", ""});
        Tags.mvTags.put("足球", new String[]{"", "", "", "", "", "", "", "", "", "249", "", ""});
        Tags.mvTags.put("健身", new String[]{"", "", "", "", "", "", "", "", "", "164", "", ""});
        Tags.mvTags.put("竞技体育", new String[]{"", "", "", "", "", "", "", "", "", "236", "", ""});
        Tags.mvTags.put("运动文化", new String[]{"", "", "", "", "", "", "", "", "", "237", "", ""});
        Tags.mvTags.put("运动综合", new String[]{"", "", "", "", "", "", "", "", "", "238", "", ""});
//        Tags.mvTag.put("汽车", new String[]{"", "", "", "", "", "", "", "", "", "223", "",""});
        Tags.mvTags.put("赛车", new String[]{"", "", "", "", "", "", "", "", "", "245", "", ""});
        Tags.mvTags.put("改装玩车", new String[]{"", "", "", "", "", "", "", "", "", "246", "", ""});
        Tags.mvTags.put("新能源车", new String[]{"", "", "", "", "", "", "", "", "", "247", "", ""});
        Tags.mvTags.put("房车", new String[]{"", "", "", "", "", "", "", "", "", "248", "", ""});
        Tags.mvTags.put("摩托车", new String[]{"", "", "", "", "", "", "", "", "", "240", "", ""});
        Tags.mvTags.put("购车攻略", new String[]{"", "", "", "", "", "", "", "", "", "227", "", ""});
        Tags.mvTags.put("汽车生活", new String[]{"", "", "", "", "", "", "", "", "", "176", "", ""});
//        Tags.mvTag.put("汽车文化 (已下线)", new String[]{"", "", "", "", "", "", "", "", "", "224", "", ""});
//        Tags.mvTag.put("汽车极客 (已下线)", new String[]{"", "", "", "", "", "", "", "", "", "225", "", ""});
//        Tags.mvTag.put("智能出行 (已下线)", new String[]{"", "", "", "", "", "", "", "", "", "226", "", ""});
//        Tags.mvTag.put("生活", new String[]{"", "", "", "", "", "", "", "", "", "160", "", ""});
//        Tags.mvTag.put("搞笑", new String[]{"", "", "", "", "", "", "", "", "", "138", "", ""});
        Tags.mvTags.put("出行", new String[]{"", "", "", "", "", "", "", "", "", "250", "", ""});
//        Tags.mvTag.put("三农", new String[]{"", "", "", "", "", "", "", "", "", "251", "", ""});
        Tags.mvTags.put("家居房产", new String[]{"", "", "", "", "", "", "", "", "", "239", "", ""});
        Tags.mvTags.put("手工", new String[]{"", "", "", "", "", "", "", "", "", "161", "", ""});
        Tags.mvTags.put("绘画", new String[]{"", "", "", "", "", "", "", "", "", "162", "", ""});
        Tags.mvTags.put("日常", new String[]{"", "", "", "", "", "", "", "", "", "21", "", ""});
//        Tags.mvTag.put("美食圈 (重定向)", new String[]{"", "", "", "", "", "", "", "", "", "76", "", ""});
//        Tags.mvTag.put("动物圈 (重定向)", new String[]{"", "", "", "", "", "", "", "", "", "75", "", ""});
//        Tags.mvTag.put("运动 (重定向)", new String[]{"", "", "", "", "", "", "", "", "", "163", "", ""});
//        Tags.mvTag.put("汽车 (重定向)", new String[]{"", "", "", "", "", "", "", "", "", "176", "", ""});
//        Tags.mvTag.put("其他 (已下线)", new String[]{"", "", "", "", "", "", "", "", "", "174", "", ""});
//        Tags.mvTag.put("美食", new String[]{"", "", "", "", "", "", "", "", "", "211", "", ""});
        Tags.mvTags.put("美食制作", new String[]{"", "", "", "", "", "", "", "", "", "76", "", ""});
        Tags.mvTags.put("美食侦探", new String[]{"", "", "", "", "", "", "", "", "", "212", "", ""});
        Tags.mvTags.put("美食测评", new String[]{"", "", "", "", "", "", "", "", "", "213", "", ""});
        Tags.mvTags.put("田园美食", new String[]{"", "", "", "", "", "", "", "", "", "214", "", ""});
        Tags.mvTags.put("美食记录", new String[]{"", "", "", "", "", "", "", "", "", "215", "", ""});
        Tags.mvTags.put("动物圈", new String[]{"", "", "", "", "", "", "", "", "", "217", "", ""});
        Tags.mvTags.put("喵星人", new String[]{"", "", "", "", "", "", "", "", "", "218", "", ""});
        Tags.mvTags.put("汪星人", new String[]{"", "", "", "", "", "", "", "", "", "219", "", ""});
        Tags.mvTags.put("野生动物", new String[]{"", "", "", "", "", "", "", "", "", "221", "", ""});
        Tags.mvTags.put("爬宠", new String[]{"", "", "", "", "", "", "", "", "", "222", "", ""});
        Tags.mvTags.put("大熊猫", new String[]{"", "", "", "", "", "", "", "", "", "220", "", ""});
        Tags.mvTags.put("动物综合", new String[]{"", "", "", "", "", "", "", "", "", "75", "", ""});
        Tags.mvTags.put("鬼畜", new String[]{"", "", "", "", "", "", "", "", "", "119", "", ""});
        Tags.mvTags.put("鬼畜调教", new String[]{"", "", "", "", "", "", "", "", "", "22", "", ""});
        Tags.mvTags.put("音MAD", new String[]{"", "", "", "", "", "", "", "", "", "26", "", ""});
        Tags.mvTags.put("人力VOCALOID", new String[]{"", "", "", "", "", "", "", "", "", "126", "", ""});
        Tags.mvTags.put("鬼畜剧场", new String[]{"", "", "", "", "", "", "", "", "", "216", "", ""});
        Tags.mvTags.put("教程演示", new String[]{"", "", "", "", "", "", "", "", "", "127", "", ""});
//        Tags.mvTag.put("时尚", new String[]{"", "", "", "", "", "", "", "", "", "155", "", ""});
        Tags.mvTags.put("美妆护肤", new String[]{"", "", "", "", "", "", "", "", "", "157", "", ""});
        Tags.mvTags.put("仿妆cos", new String[]{"", "", "", "", "", "", "", "", "", "252", "", ""});
        Tags.mvTags.put("穿搭", new String[]{"", "", "", "", "", "", "", "", "", "158", "", ""});
//        Tags.mvTag.put("健身 (重定向)", new String[]{"", "", "", "", "", "", "", "", "", "164", "", ""});
        Tags.mvTags.put("时尚潮流", new String[]{"", "", "", "", "", "", "", "", "", "159", "", ""});
//        Tags.mvTag.put("风尚标 (已下线)", new String[]{"", "", "", "", "", "", "", "", "", "192", "", ""});
        Tags.mvTags.put("资讯", new String[]{"", "", "", "", "", "", "", "", "", "202", "", ""});
        Tags.mvTags.put("热点", new String[]{"", "", "", "", "", "", "", "", "", "203", "", ""});
        Tags.mvTags.put("环球", new String[]{"", "", "", "", "", "", "", "", "", "204", "", ""});
//        Tags.mvTag.put("社会", new String[]{"", "", "", "", "", "", "", "", "", "205", "", ""});
        Tags.mvTags.put("综合", new String[]{"", "", "", "", "", "", "", "", "", "206", "", ""});
//        Tags.mvTag.put("广告", new String[]{"", "", "", "", "", "", "", "", "", "165", "", ""});
//        Tags.mvTag.put("广告 (已下线)", new String[]{"", "", "", "", "", "", "", "", "", "166", "", ""});
//        Tags.mvTag.put("娱乐", new String[]{"", "", "", "", "", "", "", "", "", "5", "", ""});
//        Tags.mvTag.put("综艺", new String[]{"", "", "", "", "", "", "", "", "", "71", "", ""});
        Tags.mvTags.put("娱乐杂谈", new String[]{"", "", "", "", "", "", "", "", "", "241", "", ""});
        Tags.mvTags.put("粉丝创作", new String[]{"", "", "", "", "", "", "", "", "", "242", "", ""});
        Tags.mvTags.put("明星综合", new String[]{"", "", "", "", "", "", "", "", "", "137", "", ""});
//        Tags.mvTag.put("Korea相关 (已下线)", new String[]{"", "", "", "", "", "", "", "", "", "131", "", ""});
//        Tags.mvTag.put("影视", new String[]{"", "", "", "", "", "", "", "", "", "181", "", ""});
        Tags.mvTags.put("影视杂谈", new String[]{"", "", "", "", "", "", "", "", "", "182", "", ""});
        Tags.mvTags.put("影视剪辑", new String[]{"", "", "", "", "", "", "", "", "", "183", "", ""});
        Tags.mvTags.put("小剧场", new String[]{"", "", "", "", "", "", "", "", "", "85", "", ""});
        Tags.mvTags.put("预告·资讯", new String[]{"", "", "", "", "", "", "", "", "", "184", "", ""});
        Tags.mvTags.put("纪录片", new String[]{"", "", "", "", "", "", "", "", "", "177", "", ""});
        Tags.mvTags.put("人文·历史", new String[]{"", "", "", "", "", "", "", "", "", "37", "", ""});
        Tags.mvTags.put("科学·探索·自然", new String[]{"", "", "", "", "", "", "", "", "", "178", "", ""});
//        Tags.mvTag.put("军事", new String[]{"", "", "", "", "", "", "", "", "", "179", "", ""});
        Tags.mvTags.put("社会·美食·旅行", new String[]{"", "", "", "", "", "", "", "", "", "180", "", ""});
        Tags.mvTags.put("电影", new String[]{"", "", "", "", "", "", "", "", "", "23", "", ""});
        Tags.mvTags.put("华语电影", new String[]{"", "", "", "", "", "", "", "", "", "147", "", ""});
        Tags.mvTags.put("欧美电影", new String[]{"", "", "", "", "", "", "", "", "", "145", "", ""});
        Tags.mvTags.put("日本电影", new String[]{"", "", "", "", "", "", "", "", "", "146", "", ""});
        Tags.mvTags.put("其他国家", new String[]{"", "", "", "", "", "", "", "", "", "83", "", ""});
        Tags.mvTags.put("电视剧", new String[]{"", "", "", "", "", "", "", "", "", "11", "", ""});
        Tags.mvTags.put("国产剧", new String[]{"", "", "", "", "", "", "", "", "", "185", "", ""});
        Tags.mvTags.put("海外剧", new String[]{"", "", "", "", "", "", "", "", "", "187", "", ""});

        MultiRunnableExecutor executor = new MultiRunnableExecutor();
        executor.submit(() -> KgMvTagReq.getInstance().initMvTag());
        executor.submit(() -> KgMvTagReq.getInstance().initIpTag());
        executor.submit(() -> QqMvTagReq.getInstance().initMvTag());
        executor.submit(() -> FaMvTagReq.getInstance().initVideoTag());
        executor.submit(() -> FaMvTagReq.getInstance().initLiveTag());
        executor.submit(() -> LzMvTagReq.getInstance().initVideoTag());
        executor.await();
    }
}
