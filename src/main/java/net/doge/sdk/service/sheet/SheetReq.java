package net.doge.sdk.service.sheet;

import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetSheetInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.service.sheet.impl.NcSheetReq;

public class SheetReq {
    private static SheetReq instance;

    private SheetReq() {
    }

    public static SheetReq getInstance() {
        if (instance == null) instance = new SheetReq();
        return instance;
    }

    /**
     * 获取歌曲乐谱
     */
    public CommonResult<NetSheetInfo> getSheets(NetMusicInfo musicInfo) {
        int source = musicInfo.getSource();
        switch (source) {
            case NetMusicSource.NC:
                return NcSheetReq.getInstance().getSheets(musicInfo);
            default:
                return CommonResult.create();
        }
    }

    /**
     * 获取乐谱图片链接
     */
    public CommonResult<String> getSheetImgUrls(NetSheetInfo sheetInfo) {
        int source = sheetInfo.getSource();
        switch (source) {
            case NetMusicSource.NC:
                return NcSheetReq.getInstance().getSheetImgUrls(sheetInfo);
            default:
                return CommonResult.create();
        }
    }
}
