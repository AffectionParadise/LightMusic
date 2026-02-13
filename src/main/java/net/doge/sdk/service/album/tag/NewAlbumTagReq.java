package net.doge.sdk.service.album.tag;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.data.Tags;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.http.HttpRequest;
import net.doge.sdk.util.http.constant.Method;
import net.doge.util.core.JsonUtil;
import net.doge.util.core.RegexUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class NewAlbumTagReq {
    private static NewAlbumTagReq instance;

    private NewAlbumTagReq() {
    }

    public static NewAlbumTagReq getInstance() {
        if (instance == null) instance = new NewAlbumTagReq();
        return instance;
    }

    // 曲风 API
    private final String STYLE_API = "https://music.163.com/api/tag/list/get";
    // 编辑精选标签 API (酷狗)
    private final String IP_TAG_KG_API = "/v1/zone/index";
    // 专辑标签 API (豆瓣)
    private final String ALBUM_TAG_DB_API = "https://music.douban.com/tag/";

    /**
     * 加载新碟标签
     *
     * @return
     */
    public void initNewAlbumTag() {
        // 网易云 网易云 网易云 酷狗 酷狗 QQ 豆瓣 堆糖
        Tags.newAlbumTag.put("默认", new String[]{"ALL", "", "", "chn", "", "1", "", ""});

        Tags.newAlbumTag.put("华语", new String[]{"ZH", "Z_H", "", "chn", "", "", "", ""});
        Tags.newAlbumTag.put("内地", new String[]{"", "", "", "", "", "1", "", ""});
        Tags.newAlbumTag.put("港台", new String[]{"", "", "", "", "", "2", "", ""});
        Tags.newAlbumTag.put("欧美", new String[]{"EA", "E_A", "", "eur", "", "3", "", ""});
        Tags.newAlbumTag.put("韩国", new String[]{"KR", "KR", "", "kor", "", "4", "", ""});
        Tags.newAlbumTag.put("日本", new String[]{"JP", "JP", "", "jpn", "", "5", "", ""});
        Tags.newAlbumTag.put("其他", new String[]{"", "", "", "", "", "6", "", ""});

        // 堆糖
        Tags.newAlbumTag.put("家居生活", new String[]{"", "", "", "", "", "", "", "家居生活"});
        Tags.newAlbumTag.put("家居生活 - 客厅", new String[]{"", "", "", "", "", "", "", "家居生活_客厅"});
        Tags.newAlbumTag.put("家居生活 - 卧室", new String[]{"", "", "", "", "", "", "", "家居生活_卧室"});
        Tags.newAlbumTag.put("家居生活 - 楼梯", new String[]{"", "", "", "", "", "", "", "家居生活_楼梯"});
        Tags.newAlbumTag.put("家居生活 - 阁楼", new String[]{"", "", "", "", "", "", "", "家居生活_阁楼"});
        Tags.newAlbumTag.put("家居生活 - 儿童房", new String[]{"", "", "", "", "", "", "", "家居生活_儿童房"});
        Tags.newAlbumTag.put("家居生活 - 厨房", new String[]{"", "", "", "", "", "", "", "家居生活_厨房"});
        Tags.newAlbumTag.put("家居生活 - 浴室", new String[]{"", "", "", "", "", "", "", "家居生活_浴室"});
        Tags.newAlbumTag.put("家居生活 - 阳台", new String[]{"", "", "", "", "", "", "", "家居生活_阳台"});
        Tags.newAlbumTag.put("家居生活 - 飘窗", new String[]{"", "", "", "", "", "", "", "家居生活_飘窗"});
        Tags.newAlbumTag.put("家居生活 - 工作间", new String[]{"", "", "", "", "", "", "", "家居生活_工作间"});
        Tags.newAlbumTag.put("家居生活 - 沙发", new String[]{"", "", "", "", "", "", "", "家居生活_沙发"});
        Tags.newAlbumTag.put("家居生活 - 椅子", new String[]{"", "", "", "", "", "", "", "家居生活_椅子"});
        Tags.newAlbumTag.put("家居生活 - 桌子", new String[]{"", "", "", "", "", "", "", "家居生活_桌子"});
        Tags.newAlbumTag.put("家居生活 - 柜子", new String[]{"", "", "", "", "", "", "", "家居生活_柜子"});
        Tags.newAlbumTag.put("家居生活 - 灯饰", new String[]{"", "", "", "", "", "", "", "家居生活_灯饰"});
        Tags.newAlbumTag.put("家居生活 - 收纳", new String[]{"", "", "", "", "", "", "", "家居生活_收纳"});
        Tags.newAlbumTag.put("家居生活 - 置物架", new String[]{"", "", "", "", "", "", "", "家居生活_置物架"});
        Tags.newAlbumTag.put("家居生活 - 照片墙", new String[]{"", "", "", "", "", "", "", "家居生活_照片墙"});
        Tags.newAlbumTag.put("家居生活 - zakka", new String[]{"", "", "", "", "", "", "", "家居生活_zakka"});
        Tags.newAlbumTag.put("家居生活 - 北欧", new String[]{"", "", "", "", "", "", "", "家居生活_北欧"});
        Tags.newAlbumTag.put("家居生活 - 地中海", new String[]{"", "", "", "", "", "", "", "家居生活_地中海"});
        Tags.newAlbumTag.put("家居生活 - 简约", new String[]{"", "", "", "", "", "", "", "家居生活_简约"});
        Tags.newAlbumTag.put("家居生活 - 宜家", new String[]{"", "", "", "", "", "", "", "家居生活_宜家"});
        Tags.newAlbumTag.put("家居生活 - 田园", new String[]{"", "", "", "", "", "", "", "家居生活_田园"});
        Tags.newAlbumTag.put("美食菜谱", new String[]{"", "", "", "", "", "", "", "美食菜谱"});
        Tags.newAlbumTag.put("美食菜谱 - 菜谱", new String[]{"", "", "", "", "", "", "", "美食菜谱_菜谱"});
        Tags.newAlbumTag.put("美食菜谱 - 家常菜", new String[]{"", "", "", "", "", "", "", "美食菜谱_家常菜"});
        Tags.newAlbumTag.put("美食菜谱 - 主食", new String[]{"", "", "", "", "", "", "", "美食菜谱_主食"});
        Tags.newAlbumTag.put("美食菜谱 - 汤粥羹", new String[]{"", "", "", "", "", "", "", "美食菜谱_汤粥羹"});
        Tags.newAlbumTag.put("美食菜谱 - 西餐", new String[]{"", "", "", "", "", "", "", "美食菜谱_西餐"});
        Tags.newAlbumTag.put("美食菜谱 - 日料", new String[]{"", "", "", "", "", "", "", "美食菜谱_日料"});
        Tags.newAlbumTag.put("美食菜谱 - 早餐", new String[]{"", "", "", "", "", "", "", "美食菜谱_早餐"});
        Tags.newAlbumTag.put("美食菜谱 - 便当", new String[]{"", "", "", "", "", "", "", "美食菜谱_便当"});
        Tags.newAlbumTag.put("美食菜谱 - 甜点", new String[]{"", "", "", "", "", "", "", "美食菜谱_甜点"});
        Tags.newAlbumTag.put("美食菜谱 - 糖果", new String[]{"", "", "", "", "", "", "", "美食菜谱_糖果"});
        Tags.newAlbumTag.put("美食菜谱 - 饼干", new String[]{"", "", "", "", "", "", "", "美食菜谱_饼干"});
        Tags.newAlbumTag.put("美食菜谱 - 蛋糕", new String[]{"", "", "", "", "", "", "", "美食菜谱_蛋糕"});
        Tags.newAlbumTag.put("美食菜谱 - 翻糖", new String[]{"", "", "", "", "", "", "", "美食菜谱_翻糖"});
        Tags.newAlbumTag.put("美食菜谱 - 马卡龙", new String[]{"", "", "", "", "", "", "", "美食菜谱_马卡龙"});
        Tags.newAlbumTag.put("美食菜谱 - 冰淇淋", new String[]{"", "", "", "", "", "", "", "美食菜谱_冰淇淋"});
        Tags.newAlbumTag.put("美食菜谱 - 水果", new String[]{"", "", "", "", "", "", "", "美食菜谱_水果"});
        Tags.newAlbumTag.put("美食菜谱 - 冰品", new String[]{"", "", "", "", "", "", "", "美食菜谱_冰品"});
        Tags.newAlbumTag.put("美食菜谱 - 咖啡", new String[]{"", "", "", "", "", "", "", "美食菜谱_咖啡"});
        Tags.newAlbumTag.put("美食菜谱 - 调酒", new String[]{"", "", "", "", "", "", "", "美食菜谱_调酒"});
        Tags.newAlbumTag.put("手工DIY", new String[]{"", "", "", "", "", "", "", "手工DIY"});
        Tags.newAlbumTag.put("手工DIY - 废物利用", new String[]{"", "", "", "", "", "", "", "手工DIY_废物利用"});
        Tags.newAlbumTag.put("手工DIY - 手工本子", new String[]{"", "", "", "", "", "", "", "手工DIY_手工本子"});
        Tags.newAlbumTag.put("手工DIY - 橡皮章", new String[]{"", "", "", "", "", "", "", "手工DIY_橡皮章"});
        Tags.newAlbumTag.put("手工DIY - 粘土", new String[]{"", "", "", "", "", "", "", "手工DIY_粘土"});
        Tags.newAlbumTag.put("手工DIY - 羊毛毡", new String[]{"", "", "", "", "", "", "", "手工DIY_羊毛毡"});
        Tags.newAlbumTag.put("手工DIY - 纽扣", new String[]{"", "", "", "", "", "", "", "手工DIY_纽扣"});
        Tags.newAlbumTag.put("手工DIY - 拼豆", new String[]{"", "", "", "", "", "", "", "手工DIY_拼豆"});
        Tags.newAlbumTag.put("手工DIY - 石绘", new String[]{"", "", "", "", "", "", "", "手工DIY_石绘"});
        Tags.newAlbumTag.put("手工DIY - 针织钩花", new String[]{"", "", "", "", "", "", "", "手工DIY_针织钩花"});
        Tags.newAlbumTag.put("手工DIY - 刺绣", new String[]{"", "", "", "", "", "", "", "手工DIY_刺绣"});
        Tags.newAlbumTag.put("手工DIY - 十字绣", new String[]{"", "", "", "", "", "", "", "手工DIY_十字绣"});
        Tags.newAlbumTag.put("手工DIY - 拼布", new String[]{"", "", "", "", "", "", "", "手工DIY_拼布"});
        Tags.newAlbumTag.put("手工DIY - 不织布", new String[]{"", "", "", "", "", "", "", "手工DIY_不织布"});
        Tags.newAlbumTag.put("手工DIY - 绳结", new String[]{"", "", "", "", "", "", "", "手工DIY_绳结"});
        Tags.newAlbumTag.put("手工DIY - 串珠", new String[]{"", "", "", "", "", "", "", "手工DIY_串珠"});
        Tags.newAlbumTag.put("手工DIY - 绕线", new String[]{"", "", "", "", "", "", "", "手工DIY_绕线"});
        Tags.newAlbumTag.put("手工DIY - 折纸", new String[]{"", "", "", "", "", "", "", "手工DIY_折纸"});
        Tags.newAlbumTag.put("手工DIY - 剪纸", new String[]{"", "", "", "", "", "", "", "手工DIY_剪纸"});
        Tags.newAlbumTag.put("手工DIY - 衍纸", new String[]{"", "", "", "", "", "", "", "手工DIY_衍纸"});
        Tags.newAlbumTag.put("手工DIY - 纸模", new String[]{"", "", "", "", "", "", "", "手工DIY_纸模"});
        Tags.newAlbumTag.put("手工DIY - 卡片", new String[]{"", "", "", "", "", "", "", "手工DIY_卡片"});
        Tags.newAlbumTag.put("时尚搭配", new String[]{"", "", "", "", "", "", "", "时尚搭配"});
        Tags.newAlbumTag.put("时尚搭配 - 搭配达人", new String[]{"", "", "", "", "", "", "", "时尚搭配_搭配达人"});
        Tags.newAlbumTag.put("时尚搭配 - 街拍", new String[]{"", "", "", "", "", "", "", "时尚搭配_街拍"});
        Tags.newAlbumTag.put("时尚搭配 - 穿搭", new String[]{"", "", "", "", "", "", "", "时尚搭配_穿搭"});
        Tags.newAlbumTag.put("时尚搭配 - 秀场", new String[]{"", "", "", "", "", "", "", "时尚搭配_秀场"});
        Tags.newAlbumTag.put("时尚搭配 - 大片", new String[]{"", "", "", "", "", "", "", "时尚搭配_大片"});
        Tags.newAlbumTag.put("时尚搭配 - 模特", new String[]{"", "", "", "", "", "", "", "时尚搭配_模特"});
        Tags.newAlbumTag.put("时尚搭配 - 时尚博主", new String[]{"", "", "", "", "", "", "", "时尚搭配_时尚博主"});
        Tags.newAlbumTag.put("时尚搭配 - 型男搭配", new String[]{"", "", "", "", "", "", "", "时尚搭配_型男搭配"});
        Tags.newAlbumTag.put("时尚搭配 - 奢侈品", new String[]{"", "", "", "", "", "", "", "时尚搭配_奢侈品"});
        Tags.newAlbumTag.put("时尚搭配 - 韩风", new String[]{"", "", "", "", "", "", "", "时尚搭配_韩风"});
        Tags.newAlbumTag.put("时尚搭配 - 日系", new String[]{"", "", "", "", "", "", "", "时尚搭配_日系"});
        Tags.newAlbumTag.put("时尚搭配 - 欧美", new String[]{"", "", "", "", "", "", "", "时尚搭配_欧美"});
        Tags.newAlbumTag.put("时尚搭配 - 文艺", new String[]{"", "", "", "", "", "", "", "时尚搭配_文艺"});
        Tags.newAlbumTag.put("时尚搭配 - 简约", new String[]{"", "", "", "", "", "", "", "时尚搭配_简约"});
        Tags.newAlbumTag.put("时尚搭配 - 英伦", new String[]{"", "", "", "", "", "", "", "时尚搭配_英伦"});
        Tags.newAlbumTag.put("时尚搭配 - 混搭", new String[]{"", "", "", "", "", "", "", "时尚搭配_混搭"});
        Tags.newAlbumTag.put("时尚搭配 - 复古", new String[]{"", "", "", "", "", "", "", "时尚搭配_复古"});
        Tags.newAlbumTag.put("时尚搭配 - 朋克", new String[]{"", "", "", "", "", "", "", "时尚搭配_朋克"});
        Tags.newAlbumTag.put("时尚搭配 - 森系", new String[]{"", "", "", "", "", "", "", "时尚搭配_森系"});
        Tags.newAlbumTag.put("时尚搭配 - 中性", new String[]{"", "", "", "", "", "", "", "时尚搭配_中性"});
        Tags.newAlbumTag.put("时尚搭配 - OL", new String[]{"", "", "", "", "", "", "", "时尚搭配_OL"});
        Tags.newAlbumTag.put("时尚搭配 - 民族风", new String[]{"", "", "", "", "", "", "", "时尚搭配_民族风"});
        Tags.newAlbumTag.put("时尚搭配 - 明星款", new String[]{"", "", "", "", "", "", "", "时尚搭配_明星款"});
        Tags.newAlbumTag.put("时尚搭配 - 碎花", new String[]{"", "", "", "", "", "", "", "时尚搭配_碎花"});
        Tags.newAlbumTag.put("时尚搭配 - 蕾丝", new String[]{"", "", "", "", "", "", "", "时尚搭配_蕾丝"});
        Tags.newAlbumTag.put("时尚搭配 - 豹纹", new String[]{"", "", "", "", "", "", "", "时尚搭配_豹纹"});
        Tags.newAlbumTag.put("时尚搭配 - 波点", new String[]{"", "", "", "", "", "", "", "时尚搭配_波点"});
        Tags.newAlbumTag.put("时尚搭配 - 条纹", new String[]{"", "", "", "", "", "", "", "时尚搭配_条纹"});
        Tags.newAlbumTag.put("时尚搭配 - 撞色", new String[]{"", "", "", "", "", "", "", "时尚搭配_撞色"});
        Tags.newAlbumTag.put("时尚搭配 - 黑色", new String[]{"", "", "", "", "", "", "", "时尚搭配_黑色"});
        Tags.newAlbumTag.put("时尚搭配 - 红色", new String[]{"", "", "", "", "", "", "", "时尚搭配_红色"});
        Tags.newAlbumTag.put("时尚搭配 - 紫色", new String[]{"", "", "", "", "", "", "", "时尚搭配_紫色"});
        Tags.newAlbumTag.put("时尚搭配 - 粉色", new String[]{"", "", "", "", "", "", "", "时尚搭配_粉色"});
        Tags.newAlbumTag.put("时尚搭配 - 彩色", new String[]{"", "", "", "", "", "", "", "时尚搭配_彩色"});
        Tags.newAlbumTag.put("时尚搭配 - 渐变色", new String[]{"", "", "", "", "", "", "", "时尚搭配_渐变色"});
        Tags.newAlbumTag.put("美妆造型", new String[]{"", "", "", "", "", "", "", "美妆造型"});
        Tags.newAlbumTag.put("美妆造型 - 彩妆", new String[]{"", "", "", "", "", "", "", "美妆造型_彩妆"});
        Tags.newAlbumTag.put("美妆造型 - 眼妆", new String[]{"", "", "", "", "", "", "", "美妆造型_眼妆"});
        Tags.newAlbumTag.put("美妆造型 - 唇妆", new String[]{"", "", "", "", "", "", "", "美妆造型_唇妆"});
        Tags.newAlbumTag.put("美妆造型 - 美甲", new String[]{"", "", "", "", "", "", "", "美妆造型_美甲"});
        Tags.newAlbumTag.put("美妆造型 - 香水", new String[]{"", "", "", "", "", "", "", "美妆造型_香水"});
        Tags.newAlbumTag.put("美妆造型 - 文身", new String[]{"", "", "", "", "", "", "", "美妆造型_文身"});
        Tags.newAlbumTag.put("美妆造型 - 发型", new String[]{"", "", "", "", "", "", "", "美妆造型_发型"});
        Tags.newAlbumTag.put("美妆造型 - 编发", new String[]{"", "", "", "", "", "", "", "美妆造型_编发"});
        Tags.newAlbumTag.put("美妆造型 - 马尾", new String[]{"", "", "", "", "", "", "", "美妆造型_马尾"});
        Tags.newAlbumTag.put("美妆造型 - 长发", new String[]{"", "", "", "", "", "", "", "美妆造型_长发"});
        Tags.newAlbumTag.put("美妆造型 - 短发", new String[]{"", "", "", "", "", "", "", "美妆造型_短发"});
        Tags.newAlbumTag.put("美妆造型 - 卷发", new String[]{"", "", "", "", "", "", "", "美妆造型_卷发"});
        Tags.newAlbumTag.put("美妆造型 - 染发", new String[]{"", "", "", "", "", "", "", "美妆造型_染发"});
        Tags.newAlbumTag.put("婚纱婚礼", new String[]{"", "", "", "", "", "", "", "婚纱婚礼"});
        Tags.newAlbumTag.put("婚纱婚礼 - 婚礼布置", new String[]{"", "", "", "", "", "", "", "婚纱婚礼_婚礼布置"});
        Tags.newAlbumTag.put("婚纱婚礼 - 结婚蛋糕", new String[]{"", "", "", "", "", "", "", "婚纱婚礼_结婚蛋糕"});
        Tags.newAlbumTag.put("婚纱婚礼 - 请柬", new String[]{"", "", "", "", "", "", "", "婚纱婚礼_请柬"});
        Tags.newAlbumTag.put("婚纱婚礼 - 喜糖", new String[]{"", "", "", "", "", "", "", "婚纱婚礼_喜糖"});
        Tags.newAlbumTag.put("婚纱婚礼 - 捧花", new String[]{"", "", "", "", "", "", "", "婚纱婚礼_捧花"});
        Tags.newAlbumTag.put("婚纱婚礼 - 结婚照", new String[]{"", "", "", "", "", "", "", "婚纱婚礼_结婚照"});
        Tags.newAlbumTag.put("婚纱婚礼 - 婚纱", new String[]{"", "", "", "", "", "", "", "婚纱婚礼_婚纱"});
        Tags.newAlbumTag.put("婚纱婚礼 - 伴娘服", new String[]{"", "", "", "", "", "", "", "婚纱婚礼_伴娘服"});
        Tags.newAlbumTag.put("婚纱婚礼 - 新娘发型", new String[]{"", "", "", "", "", "", "", "婚纱婚礼_新娘发型"});
        Tags.newAlbumTag.put("婚纱婚礼 - 婚戒", new String[]{"", "", "", "", "", "", "", "婚纱婚礼_婚戒"});
        Tags.newAlbumTag.put("婚纱婚礼 - 婚鞋", new String[]{"", "", "", "", "", "", "", "婚纱婚礼_婚鞋"});
        Tags.newAlbumTag.put("文字句子", new String[]{"", "", "", "", "", "", "", "文字句子"});
        Tags.newAlbumTag.put("文字句子 - 手写", new String[]{"", "", "", "", "", "", "", "文字句子_手写"});
        Tags.newAlbumTag.put("文字句子 - 语录", new String[]{"", "", "", "", "", "", "", "文字句子_语录"});
        Tags.newAlbumTag.put("文字句子 - 情感", new String[]{"", "", "", "", "", "", "", "文字句子_情感"});
        Tags.newAlbumTag.put("文字句子 - 英文", new String[]{"", "", "", "", "", "", "", "文字句子_英文"});
        Tags.newAlbumTag.put("插画绘画", new String[]{"", "", "", "", "", "", "", "插画绘画"});
        Tags.newAlbumTag.put("插画绘画 - 手绘", new String[]{"", "", "", "", "", "", "", "插画绘画_手绘"});
        Tags.newAlbumTag.put("插画绘画 - 素描", new String[]{"", "", "", "", "", "", "", "插画绘画_素描"});
        Tags.newAlbumTag.put("插画绘画 - 水彩", new String[]{"", "", "", "", "", "", "", "插画绘画_水彩"});
        Tags.newAlbumTag.put("插画绘画 - 彩铅", new String[]{"", "", "", "", "", "", "", "插画绘画_彩铅"});
        Tags.newAlbumTag.put("插画绘画 - 油画", new String[]{"", "", "", "", "", "", "", "插画绘画_油画"});
        Tags.newAlbumTag.put("插画绘画 - 版画", new String[]{"", "", "", "", "", "", "", "插画绘画_版画"});
        Tags.newAlbumTag.put("插画绘画 - 绘画教程", new String[]{"", "", "", "", "", "", "", "插画绘画_绘画教程"});
        Tags.newAlbumTag.put("插画绘画 - Q版", new String[]{"", "", "", "", "", "", "", "插画绘画_Q版"});
        Tags.newAlbumTag.put("插画绘画 - CG", new String[]{"", "", "", "", "", "", "", "插画绘画_CG"});
        Tags.newAlbumTag.put("插画绘画 - 人物", new String[]{"", "", "", "", "", "", "", "插画绘画_人物"});
        Tags.newAlbumTag.put("插画绘画 - 时装", new String[]{"", "", "", "", "", "", "", "插画绘画_时装"});
        Tags.newAlbumTag.put("插画绘画 - 动物", new String[]{"", "", "", "", "", "", "", "插画绘画_动物"});
        Tags.newAlbumTag.put("插画绘画 - 植物", new String[]{"", "", "", "", "", "", "", "插画绘画_植物"});
        Tags.newAlbumTag.put("插画绘画 - 美食", new String[]{"", "", "", "", "", "", "", "插画绘画_美食"});
        Tags.newAlbumTag.put("插画绘画 - 风景", new String[]{"", "", "", "", "", "", "", "插画绘画_风景"});
        Tags.newAlbumTag.put("影音书籍", new String[]{"", "", "", "", "", "", "", "影音书籍"});
        Tags.newAlbumTag.put("影音书籍 - 冰雪奇缘", new String[]{"", "", "", "", "", "", "", "影音书_冰雪奇缘"});
        Tags.newAlbumTag.put("影音书籍 - 来自星星的你", new String[]{"", "", "", "", "", "", "", "影音书_来自星星的你"});
        Tags.newAlbumTag.put("影音书籍 - 神偷奶爸", new String[]{"", "", "", "", "", "", "", "影音书_神偷奶爸"});
        Tags.newAlbumTag.put("影音书籍 - 破产姐妹", new String[]{"", "", "", "", "", "", "", "影音书_破产姐妹"});
        Tags.newAlbumTag.put("影音书籍 - 小时代", new String[]{"", "", "", "", "", "", "", "影音书_小时代"});
        Tags.newAlbumTag.put("影音书籍 - 继承者们", new String[]{"", "", "", "", "", "", "", "影音书_继承者们"});
        Tags.newAlbumTag.put("影音书籍 - 生活大爆炸", new String[]{"", "", "", "", "", "", "", "影音书_生活大爆炸"});
        Tags.newAlbumTag.put("影音书籍 - 神探夏洛克", new String[]{"", "", "", "", "", "", "", "影音书_神探夏洛克"});
        Tags.newAlbumTag.put("影音书籍 - 电影", new String[]{"", "", "", "", "", "", "", "影音书_电影"});
        Tags.newAlbumTag.put("影音书籍 - 电视剧", new String[]{"", "", "", "", "", "", "", "影音书_电视剧"});
        Tags.newAlbumTag.put("影音书籍 - 台词", new String[]{"", "", "", "", "", "", "", "影音书_台词"});
        Tags.newAlbumTag.put("影音书籍 - 电影海报", new String[]{"", "", "", "", "", "", "", "影音书_电影海报"});
        Tags.newAlbumTag.put("影音书籍 - 剧照", new String[]{"", "", "", "", "", "", "", "搞笑萌宠_奥莉"});
        Tags.newAlbumTag.put("影音书籍 - 截图", new String[]{"", "", "", "", "", "", "", "影音书_截图"});
        Tags.newAlbumTag.put("人物明星", new String[]{"", "", "", "", "", "", "", "人物明星"});
        Tags.newAlbumTag.put("人物明星 - 美女", new String[]{"", "", "", "", "", "", "", "人物明星_美女"});
        Tags.newAlbumTag.put("人物明星 - 美男", new String[]{"", "", "", "", "", "", "", "人物明星_美男"});
        Tags.newAlbumTag.put("人物明星 - 演员", new String[]{"", "", "", "", "", "", "", "人物明星_演员"});
        Tags.newAlbumTag.put("人物明星 - 赫本", new String[]{"", "", "", "", "", "", "", "人物明星_赫本"});
        Tags.newAlbumTag.put("人物明星 - 安妮海瑟薇", new String[]{"", "", "", "", "", "", "", "人物明星_安妮海瑟薇"});
        Tags.newAlbumTag.put("人物明星 - Taylor Swift", new String[]{"", "", "", "", "", "", "", "人物明星_Taylor Swift"});
        Tags.newAlbumTag.put("人物明星 - 范冰冰", new String[]{"", "", "", "", "", "", "", "人物明星_范冰冰"});
        Tags.newAlbumTag.put("人物明星 - Angelababy", new String[]{"", "", "", "", "", "", "", "人物明星_Angelababy"});
        Tags.newAlbumTag.put("人物明星 - 水原希子", new String[]{"", "", "", "", "", "", "", "人物明星_水原希子"});
        Tags.newAlbumTag.put("人物明星 - 张辛苑", new String[]{"", "", "", "", "", "", "", "人物明星_张辛苑"});
        Tags.newAlbumTag.put("人物明星 - 张国荣", new String[]{"", "", "", "", "", "", "", "人物明星_张国荣"});
        Tags.newAlbumTag.put("人物明星 - 张根硕", new String[]{"", "", "", "", "", "", "", "人物明星_张根硕"});
        Tags.newAlbumTag.put("人物明星 - 李敏镐", new String[]{"", "", "", "", "", "", "", "人物明星_李敏镐"});
        Tags.newAlbumTag.put("植物多肉", new String[]{"", "", "", "", "", "", "", "植物多肉"});
        Tags.newAlbumTag.put("植物多肉 - 图鉴", new String[]{"", "", "", "", "", "", "", "植物多肉_图鉴"});
        Tags.newAlbumTag.put("植物多肉 - 花卉", new String[]{"", "", "", "", "", "", "", "植物多肉_花卉"});
        Tags.newAlbumTag.put("植物多肉 - 多肉", new String[]{"", "", "", "", "", "", "", "植物多肉_多肉"});
        Tags.newAlbumTag.put("植物多肉 - 苔藓", new String[]{"", "", "", "", "", "", "", "植物多肉_苔藓"});
        Tags.newAlbumTag.put("植物多肉 - 园艺", new String[]{"", "", "", "", "", "", "", "植物多肉_园艺"});
        Tags.newAlbumTag.put("植物多肉 - 盆栽", new String[]{"", "", "", "", "", "", "", "植物多肉_盆栽"});
        Tags.newAlbumTag.put("生活百科", new String[]{"", "", "", "", "", "", "", "生活百科"});
        Tags.newAlbumTag.put("生活百科 - 小妙招", new String[]{"", "", "", "", "", "", "", "生活百科_小妙招"});
        Tags.newAlbumTag.put("生活百科 - 日常清洁", new String[]{"", "", "", "", "", "", "", "生活百科_日常清洁"});
        Tags.newAlbumTag.put("生活百科 - 孕产", new String[]{"", "", "", "", "", "", "", "生活百科_孕产"});
        Tags.newAlbumTag.put("生活百科 - 育儿", new String[]{"", "", "", "", "", "", "", "生活百科_育儿"});
        Tags.newAlbumTag.put("生活百科 - 养生", new String[]{"", "", "", "", "", "", "", "生活百科_养生"});
        Tags.newAlbumTag.put("生活百科 - 美容保养", new String[]{"", "", "", "", "", "", "", "生活百科_美白"});
        Tags.newAlbumTag.put("生活百科 - 健身", new String[]{"", "", "", "", "", "", "", "生活百科_健身"});
        Tags.newAlbumTag.put("搞笑萌宠", new String[]{"", "", "", "", "", "", "", "搞笑萌宠"});
        Tags.newAlbumTag.put("搞笑萌宠 - 汪星人", new String[]{"", "", "", "", "", "", "", "搞笑萌宠_汪星人"});
        Tags.newAlbumTag.put("搞笑萌宠 - 喵星人", new String[]{"", "", "", "", "", "", "", "搞笑萌宠_喵星人"});
        Tags.newAlbumTag.put("搞笑萌宠 - 熊猫", new String[]{"", "", "", "", "", "", "", "搞笑萌宠_熊猫"});
        Tags.newAlbumTag.put("搞笑萌宠 - 刺猬", new String[]{"", "", "", "", "", "", "", "搞笑萌宠_刺猬"});
        Tags.newAlbumTag.put("搞笑萌宠 - 考拉", new String[]{"", "", "", "", "", "", "", "搞笑萌宠_考拉"});
        Tags.newAlbumTag.put("搞笑萌宠 - 长颈鹿", new String[]{"", "", "", "", "", "", "", "搞笑萌宠_长颈鹿"});
        Tags.newAlbumTag.put("搞笑萌宠 - 吱星人", new String[]{"", "", "", "", "", "", "", "搞笑萌宠_吱星人"});
        Tags.newAlbumTag.put("搞笑萌宠 - 袋鼠", new String[]{"", "", "", "", "", "", "", "搞笑萌宠_袋鼠"});
        Tags.newAlbumTag.put("搞笑萌宠 - 大象", new String[]{"", "", "", "", "", "", "", "搞笑萌宠_大象"});
        Tags.newAlbumTag.put("搞笑萌宠 - 鸟", new String[]{"", "", "", "", "", "", "", "搞笑萌宠_鸟"});
        Tags.newAlbumTag.put("搞笑萌宠 - 干脆面", new String[]{"", "", "", "", "", "", "", "搞笑萌宠_干脆面"});
        Tags.newAlbumTag.put("搞笑萌宠 - 兔子", new String[]{"", "", "", "", "", "", "", "搞笑萌宠_兔子"});
        Tags.newAlbumTag.put("搞笑萌宠 - 鱼", new String[]{"", "", "", "", "", "", "", "搞笑萌宠_鱼"});
        Tags.newAlbumTag.put("搞笑萌宠 - 萌娃", new String[]{"", "", "", "", "", "", "", "搞笑萌宠_萌娃"});
        Tags.newAlbumTag.put("搞笑萌宠 - 萝莉", new String[]{"", "", "", "", "", "", "", "搞笑萌宠_萝莉"});
        Tags.newAlbumTag.put("搞笑萌宠 - 正太", new String[]{"", "", "", "", "", "", "", "搞笑萌宠_正太"});
        Tags.newAlbumTag.put("搞笑萌宠 - 嘎蒙", new String[]{"", "", "", "", "", "", "", "搞笑萌宠_嘎蒙"});
        Tags.newAlbumTag.put("搞笑萌宠 - 奥莉", new String[]{"", "", "", "", "", "", "", "搞笑萌宠_奥莉"});
        Tags.newAlbumTag.put("搞笑萌宠 - 爸爸去哪儿", new String[]{"", "", "", "", "", "", "", "搞笑萌宠_爸爸去哪儿"});
        Tags.newAlbumTag.put("搞笑萌宠 - 萨摩耶", new String[]{"", "", "", "", "", "", "", "搞笑萌宠_萨摩耶"});
        Tags.newAlbumTag.put("搞笑萌宠 - 哈士奇", new String[]{"", "", "", "", "", "", "", "搞笑萌宠_哈士奇"});
        Tags.newAlbumTag.put("搞笑萌宠 - 治愈", new String[]{"", "", "", "", "", "", "", "搞笑萌宠_治愈"});
        Tags.newAlbumTag.put("搞笑萌宠 - 金毛", new String[]{"", "", "", "", "", "", "", "搞笑萌宠_金毛"});
        Tags.newAlbumTag.put("搞笑萌宠 - 折耳", new String[]{"", "", "", "", "", "", "", "搞笑萌宠_折耳"});
        Tags.newAlbumTag.put("搞笑萌宠 - 加菲猫", new String[]{"", "", "", "", "", "", "", "搞笑萌宠_加菲猫"});
        Tags.newAlbumTag.put("搞笑萌宠 - grace", new String[]{"", "", "", "", "", "", "", "搞笑萌宠_grace"});
        Tags.newAlbumTag.put("搞笑萌宠 - feynman", new String[]{"", "", "", "", "", "", "", "搞笑萌宠_feynman"});
        Tags.newAlbumTag.put("搞笑萌宠 - 鹿", new String[]{"", "", "", "", "", "", "", "搞笑萌宠_鹿"});
        Tags.newAlbumTag.put("人文艺术", new String[]{"", "", "", "", "", "", "", "人文艺术"});
        Tags.newAlbumTag.put("人文艺术 - 打字机", new String[]{"", "", "", "", "", "", "", "人文艺术_打字机"});
        Tags.newAlbumTag.put("人文艺术 - 老爷车", new String[]{"", "", "", "", "", "", "", "人文艺术_老爷车"});
        Tags.newAlbumTag.put("人文艺术 - 铁皮", new String[]{"", "", "", "", "", "", "", "人文艺术_铁皮"});
        Tags.newAlbumTag.put("人文艺术 - 音乐盒", new String[]{"", "", "", "", "", "", "", "人文艺术_音乐盒"});
        Tags.newAlbumTag.put("人文艺术 - 招贴画", new String[]{"", "", "", "", "", "", "", "人文艺术_招贴画"});
        Tags.newAlbumTag.put("人文艺术 - 国画", new String[]{"", "", "", "", "", "", "", "人文艺术_国画"});
        Tags.newAlbumTag.put("人文艺术 - 书法", new String[]{"", "", "", "", "", "", "", "人文艺术_书法"});
        Tags.newAlbumTag.put("人文艺术 - 戏曲", new String[]{"", "", "", "", "", "", "", "人文艺术_戏曲"});
        Tags.newAlbumTag.put("人文艺术 - 陶艺", new String[]{"", "", "", "", "", "", "", "人文艺术_陶艺"});
        Tags.newAlbumTag.put("人文艺术 - 文物", new String[]{"", "", "", "", "", "", "", "人文艺术_文物"});
        Tags.newAlbumTag.put("人文艺术 - 珠宝", new String[]{"", "", "", "", "", "", "", "人文艺术_珠宝"});
        Tags.newAlbumTag.put("人文艺术 - 人偶", new String[]{"", "", "", "", "", "", "", "人文艺术_人偶"});
        Tags.newAlbumTag.put("设计", new String[]{"", "", "", "", "", "", "", "设计"});
        Tags.newAlbumTag.put("设计 - 平面设计", new String[]{"", "", "", "", "", "", "", "设计_平面设计"});
        Tags.newAlbumTag.put("设计 - 海报设计", new String[]{"", "", "", "", "", "", "", "设计_海报设计"});
        Tags.newAlbumTag.put("设计 - 广告设计", new String[]{"", "", "", "", "", "", "", "设计_广告设计"});
        Tags.newAlbumTag.put("设计 - 包装设计", new String[]{"", "", "", "", "", "", "", "设计_包装设计"});
        Tags.newAlbumTag.put("设计 - VI设计", new String[]{"", "", "", "", "", "", "", "设计_VI设计"});
        Tags.newAlbumTag.put("设计 - 雕塑", new String[]{"", "", "", "", "", "", "", "设计_雕塑"});
        Tags.newAlbumTag.put("设计 - 建筑", new String[]{"", "", "", "", "", "", "", "设计_建筑"});
        Tags.newAlbumTag.put("设计 - 配色", new String[]{"", "", "", "", "", "", "", "设计_配色"});
        Tags.newAlbumTag.put("设计 - LOGO", new String[]{"", "", "", "", "", "", "", "设计_LOGO"});
        Tags.newAlbumTag.put("设计 - 字体", new String[]{"", "", "", "", "", "", "", "设计_字体"});
        Tags.newAlbumTag.put("设计 - 矢量素材", new String[]{"", "", "", "", "", "", "", "设计_矢量素材"});
        Tags.newAlbumTag.put("古风", new String[]{"", "", "", "", "", "", "", "古风"});
        Tags.newAlbumTag.put("古风 - 汉服", new String[]{"", "", "", "", "", "", "", "古风_汉服"});
        Tags.newAlbumTag.put("古风 - 发簪", new String[]{"", "", "", "", "", "", "", "古风_发簪"});
        Tags.newAlbumTag.put("古风 - 仕女", new String[]{"", "", "", "", "", "", "", "古风_仕女"});
        Tags.newAlbumTag.put("古风 - 美男", new String[]{"", "", "", "", "", "", "", "古风_美男"});
        Tags.newAlbumTag.put("古风 - 京剧", new String[]{"", "", "", "", "", "", "", "古风_京剧"});
        Tags.newAlbumTag.put("古风 - 古建筑", new String[]{"", "", "", "", "", "", "", "古风_古建筑"});
        Tags.newAlbumTag.put("壁纸", new String[]{"", "", "", "", "", "", "", "壁纸"});
        Tags.newAlbumTag.put("壁纸 - 颜色", new String[]{"", "", "", "", "", "", "", "壁纸-颜色"});
        Tags.newAlbumTag.put("壁纸 - 渐变", new String[]{"", "", "", "", "", "", "", "壁纸-渐变"});
        Tags.newAlbumTag.put("壁纸 - 可爱", new String[]{"", "", "", "", "", "", "", "壁纸-可爱"});
        Tags.newAlbumTag.put("壁纸 - 情侣", new String[]{"", "", "", "", "", "", "", "壁纸-情侣"});
        Tags.newAlbumTag.put("壁纸 - 少女心", new String[]{"", "", "", "", "", "", "", "壁纸-少女心"});
        Tags.newAlbumTag.put("壁纸 - 小清新", new String[]{"", "", "", "", "", "", "", "壁纸-小清新"});
        Tags.newAlbumTag.put("壁纸 - 动漫", new String[]{"", "", "", "", "", "", "", "壁纸-动漫"});
        Tags.newAlbumTag.put("壁纸 - 文字", new String[]{"", "", "", "", "", "", "", "壁纸-文字"});
        Tags.newAlbumTag.put("壁纸 - 锁屏", new String[]{"", "", "", "", "", "", "", "壁纸-锁屏"});
        Tags.newAlbumTag.put("壁纸 - 朋友圈", new String[]{"", "", "", "", "", "", "", "壁纸-朋友圈背景"});
        Tags.newAlbumTag.put("壁纸 - 爱豆", new String[]{"", "", "", "", "", "", "", "壁纸-爱豆"});
        Tags.newAlbumTag.put("壁纸 - 影视剧", new String[]{"", "", "", "", "", "", "", "壁纸-影视剧"});
        Tags.newAlbumTag.put("壁纸 - 三屏壁纸", new String[]{"", "", "", "", "", "", "", "壁纸-三屏"});
        Tags.newAlbumTag.put("壁纸 - 考试", new String[]{"", "", "", "", "", "", "", "壁纸-考试"});
        Tags.newAlbumTag.put("壁纸 - 电脑", new String[]{"", "", "", "", "", "", "", "壁纸_电脑壁纸"});
        Tags.newAlbumTag.put("壁纸 - 减肥", new String[]{"", "", "", "", "", "", "", "壁纸-减肥"});
        Tags.newAlbumTag.put("旅行", new String[]{"", "", "", "", "", "", "", "旅行"});
        Tags.newAlbumTag.put("旅行 - 城堡", new String[]{"", "", "", "", "", "", "", "旅行_城堡"});
        Tags.newAlbumTag.put("旅行 - 岛屿", new String[]{"", "", "", "", "", "", "", "旅行_岛屿"});
        Tags.newAlbumTag.put("旅行 - 教堂", new String[]{"", "", "", "", "", "", "", "旅行_教堂"});
        Tags.newAlbumTag.put("旅行 - 小镇", new String[]{"", "", "", "", "", "", "", "旅行_小镇"});
        Tags.newAlbumTag.put("旅行 - 夜景", new String[]{"", "", "", "", "", "", "", "旅行_夜景"});
        Tags.newAlbumTag.put("旅行 - 森林", new String[]{"", "", "", "", "", "", "", "旅行_森林"});
        Tags.newAlbumTag.put("旅行 - 赏花", new String[]{"", "", "", "", "", "", "", "旅行_赏花"});
        Tags.newAlbumTag.put("旅行 - 欧洲", new String[]{"", "", "", "", "", "", "", "旅行_欧洲"});
        Tags.newAlbumTag.put("旅行 - 日本", new String[]{"", "", "", "", "", "", "", "旅行_日本"});
        Tags.newAlbumTag.put("旅行 - 马尔代夫", new String[]{"", "", "", "", "", "", "", "旅行_马尔代夫"});
        Tags.newAlbumTag.put("旅行 - 西藏", new String[]{"", "", "", "", "", "", "", "旅行_西藏"});
        Tags.newAlbumTag.put("旅行 - 云南", new String[]{"", "", "", "", "", "", "", "旅行_云南"});
        Tags.newAlbumTag.put("旅行 - 四川", new String[]{"", "", "", "", "", "", "", "旅行_四川"});
        Tags.newAlbumTag.put("旅行 - 厦门", new String[]{"", "", "", "", "", "", "", "旅行_厦门"});
        Tags.newAlbumTag.put("头像", new String[]{"", "", "", "", "", "", "", "头像"});
        Tags.newAlbumTag.put("头像 - 沙雕", new String[]{"", "", "", "", "", "", "", "表情-沙雕"});
        Tags.newAlbumTag.put("头像 - 文字", new String[]{"", "", "", "", "", "", "", "表情包-文字"});
        Tags.newAlbumTag.put("头像 - 可爱", new String[]{"", "", "", "", "", "", "", "表情包-可爱"});
        Tags.newAlbumTag.put("头像 - 萌宠", new String[]{"", "", "", "", "", "", "", "表情包-萌宠"});
        Tags.newAlbumTag.put("头像 - gif", new String[]{"", "", "", "", "", "", "", "表情包-gif"});
        Tags.newAlbumTag.put("头像 - emoji", new String[]{"", "", "", "", "", "", "", "表情包-emoji"});
        Tags.newAlbumTag.put("头像 - 女生", new String[]{"", "", "", "", "", "", "", "头像_女生"});
        Tags.newAlbumTag.put("头像 - 男生", new String[]{"", "", "", "", "", "", "", "头像_男生"});
        Tags.newAlbumTag.put("头像 - 情侣", new String[]{"", "", "", "", "", "", "", "头像_情侣"});
        Tags.newAlbumTag.put("头像 - 欧美", new String[]{"", "", "", "", "", "", "", "头像_欧美"});
        Tags.newAlbumTag.put("头像 - 文字", new String[]{"", "", "", "", "", "", "", "头像_文字"});
        Tags.newAlbumTag.put("头像 - 个性", new String[]{"", "", "", "", "", "", "", "头像_个性"});
        Tags.newAlbumTag.put("头像 - 卡通", new String[]{"", "", "", "", "", "", "", "头像_卡通"});
        Tags.newAlbumTag.put("头像 - 沙雕", new String[]{"", "", "", "", "", "", "", "头像-沙雕"});
        Tags.newAlbumTag.put("头像 - 欧美", new String[]{"", "", "", "", "", "", "", "头像-欧美"});
        Tags.newAlbumTag.put("头像 - 团体", new String[]{"", "", "", "", "", "", "", "头像-团体"});
        Tags.newAlbumTag.put("头像 - 想念熊", new String[]{"", "", "", "", "", "", "", "表情_想念熊"});
        Tags.newAlbumTag.put("头像 - 炉石娘", new String[]{"", "", "", "", "", "", "", "表情_炉石娘"});
        Tags.newAlbumTag.put("头像 - 牛轰轰", new String[]{"", "", "", "", "", "", "", "表情_牛轰轰"});
        Tags.newAlbumTag.put("头像 - 偶系小Q", new String[]{"", "", "", "", "", "", "", "表情_偶系小Q"});
        Tags.newAlbumTag.put("头像 - 小希与阿树", new String[]{"", "", "", "", "", "", "", "表情_小希与阿树"});
        Tags.newAlbumTag.put("头像 - 长草的颜文字君", new String[]{"", "", "", "", "", "", "", "表情_长草的颜文字君"});
        Tags.newAlbumTag.put("头像 - 豆包兔", new String[]{"", "", "", "", "", "", "", "表情_豆包兔"});
        Tags.newAlbumTag.put("头像 - 梁阿渣", new String[]{"", "", "", "", "", "", "", "表情_梁阿渣"});
        Tags.newAlbumTag.put("头像 - 冷先森漫画", new String[]{"", "", "", "", "", "", "", "表情_冷先森漫画"});
        Tags.newAlbumTag.put("头像 - 贱婊情", new String[]{"", "", "", "", "", "", "", "表情_贱婊情"});
        Tags.newAlbumTag.put("头像 - 小崽子", new String[]{"", "", "", "", "", "", "", "表情_小崽子"});
        Tags.newAlbumTag.put("头像 - 肥志", new String[]{"", "", "", "", "", "", "", "表情_肥志"});
        Tags.newAlbumTag.put("头像 - 花园夜", new String[]{"", "", "", "", "", "", "", "表情_花园夜"});
        Tags.newAlbumTag.put("头像 - 制冷少女", new String[]{"", "", "", "", "", "", "", "表情_制冷少女"});
        Tags.newAlbumTag.put("头像 - 虽虽酱", new String[]{"", "", "", "", "", "", "", "表情_虽虽酱"});
        Tags.newAlbumTag.put("摄影", new String[]{"", "", "", "", "", "", "", "摄影"});
        Tags.newAlbumTag.put("摄影 - 胶片", new String[]{"", "", "", "", "", "", "", "摄影_胶片"});
        Tags.newAlbumTag.put("摄影 - LOMO", new String[]{"", "", "", "", "", "", "", "摄影_LOMO"});
        Tags.newAlbumTag.put("摄影 - 移轴", new String[]{"", "", "", "", "", "", "", "摄影_移轴"});
        Tags.newAlbumTag.put("摄影 - 创意", new String[]{"", "", "", "", "", "", "", "摄影_创意"});
        Tags.newAlbumTag.put("摄影 - 人像", new String[]{"", "", "", "", "", "", "", "摄影_人像"});
        Tags.newAlbumTag.put("摄影 - 静物", new String[]{"", "", "", "", "", "", "", "摄影_静物"});
        Tags.newAlbumTag.put("摄影 - 风光", new String[]{"", "", "", "", "", "", "", "摄影_风光"});
        Tags.newAlbumTag.put("摄影 - 黑白", new String[]{"", "", "", "", "", "", "", "摄影_黑白"});
        Tags.newAlbumTag.put("摄影 - 水下摄影", new String[]{"", "", "", "", "", "", "", "摄影_水下摄影"});
        Tags.newAlbumTag.put("表情", new String[]{"", "", "", "", "", "", "", "表情"});
        Tags.newAlbumTag.put("素材", new String[]{"", "", "", "", "", "", "", "素材"});
        Tags.newAlbumTag.put("动图", new String[]{"", "", "", "", "", "", "", "动图"});

        final int c = 8;
        // 网易云曲风
        Runnable initAlbumTag = () -> {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String tagBody = SdkCommon.ncRequest(Method.POST, STYLE_API, "{}", options)
                    .executeAsStr();
            JSONObject tagJson = JSONObject.parseObject(tagBody);
            JSONArray tags = tagJson.getJSONArray("data");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONObject tag = tags.getJSONObject(i);

                String name = tag.getString("tagName");
                String id = tag.getString("tagId");

                if (!Tags.newAlbumTag.containsKey(name)) Tags.newAlbumTag.put(name, new String[c]);
                Tags.newAlbumTag.get(name)[2] = id;
                // 子标签
                JSONArray subTags = tag.getJSONArray("childrenTags");
                if (JsonUtil.isEmpty(subTags)) continue;
                for (int j = 0, s = subTags.size(); j < s; j++) {
                    JSONObject subTag = subTags.getJSONObject(j);

                    String subName = subTag.getString("tagName");
                    String subId = subTag.getString("tagId");

                    if (!Tags.newAlbumTag.containsKey(subName)) Tags.newAlbumTag.put(subName, new String[c]);
                    Tags.newAlbumTag.get(subName)[2] = subId;
                    // 孙子标签
                    JSONArray ssTags = subTag.getJSONArray("childrenTags");
                    if (JsonUtil.isEmpty(ssTags)) continue;
                    for (int k = 0, l = ssTags.size(); k < l; k++) {
                        JSONObject ssTag = ssTags.getJSONObject(k);

                        String ssName = ssTag.getString("tagName");
                        String ssId = ssTag.getString("tagId");

                        if (!Tags.newAlbumTag.containsKey(ssName)) Tags.newAlbumTag.put(ssName, new String[c]);
                        Tags.newAlbumTag.get(ssName)[2] = ssId;
                    }
                }
            }
        };

        // 酷狗
        // 编辑精选标签
        Runnable initIpTagKg = () -> {
            Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidGet(IP_TAG_KG_API);
            String tagBody = SdkCommon.kgRequest(null, null, options)
                    .header("x-router", "yuekucategory.kugou.com")
                    .executeAsStr();
            JSONArray tags = JSONObject.parseObject(tagBody).getJSONObject("data").getJSONArray("list");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONObject tag = tags.getJSONObject(i);

                String id = RegexUtil.getGroup1("ip_id%3D(\\d+)", tag.getString("special_link"));
                String name = tag.getString("name");

                if (!Tags.newAlbumTag.containsKey(name)) Tags.newAlbumTag.put(name, new String[c]);
                Tags.newAlbumTag.get(name)[4] = id;
            }
        };

        // 豆瓣
        // 分类专辑标签
        Runnable initAlbumTagDb = () -> {
            String albumTagBody = HttpRequest.get(ALBUM_TAG_DB_API)
                    .executeAsStr();
            Document doc = Jsoup.parse(albumTagBody);
            Elements tags = doc.select("tbody tr td a");
            for (int i = 0, len = tags.size(); i < len; i++) {
                Element tag = tags.get(i);

                String name = tag.text();
                String id = tag.text();

                if (!Tags.newAlbumTag.containsKey(name)) Tags.newAlbumTag.put(name, new String[c]);
                Tags.newAlbumTag.get(name)[6] = id;
            }
        };

        List<Future<?>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(initAlbumTag));
        taskList.add(GlobalExecutors.requestExecutor.submit(initIpTagKg));
        taskList.add(GlobalExecutors.requestExecutor.submit(initAlbumTagDb));

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
