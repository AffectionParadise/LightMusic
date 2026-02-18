package net.doge.sdk.service.comment;

import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetCommentInfo;
import net.doge.entity.service.base.NetResource;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.service.comment.impl.*;

public class CommentReq {
    private static CommentReq instance;

    private CommentReq() {
    }

    public static CommentReq getInstance() {
        if (instance == null) instance = new CommentReq();
        return instance;
    }

    /**
     * 获取评论
     */
    public CommonResult<NetCommentInfo> getComments(NetResource resource, String type, int page, int limit, String cursor) {
        int source = resource.getSource();
        switch (source) {
            case NetMusicSource.NC:
                return NcCommentReq.getInstance().getComments(resource, type, page, limit, cursor);
            case NetMusicSource.KG:
                return KgCommentReq.getInstance().getComments(resource, type, page, limit);
            case NetMusicSource.QQ:
                return QqCommentReq.getInstance().getComments(resource, type, page, limit);
            case NetMusicSource.KW:
                return KwCommentReq.getInstance().getComments(resource, type, page, limit);
            case NetMusicSource.MG:
                return MgCommentReq.getInstance().getComments(resource, type, page, limit, cursor);
            case NetMusicSource.XM:
                return XmCommentReq.getInstance().getComments(resource, type, page, limit);
            case NetMusicSource.HF:
                return HfCommentReq.getInstance().getComments(resource, page, limit);
            case NetMusicSource.GG:
                return GgCommentReq.getInstance().getComments(resource, page, limit);
            case NetMusicSource.FS:
                return FsCommentReq.getInstance().getComments(resource, type, page, limit);
            case NetMusicSource.ME:
                return MeCommentReq.getInstance().getComments(resource, type, page, limit);
            case NetMusicSource.HK:
                return HkCommentReq.getInstance().getComments(resource, page, limit);
            case NetMusicSource.DB:
                return DbCommentReq.getInstance().getComments(resource, type, page, limit);
            case NetMusicSource.BI:
                return BiCommentReq.getInstance().getComments(resource, type, page, limit);
//            case NetMusicSource.YY:
//                return YyCommentReq.getInstance().getComments(resource, type, page, limit, cursor);
            case NetMusicSource.LZ:
                return LzCommentReq.getInstance().getComments(resource, page, limit, cursor);
            default:
                return CommonResult.create();
        }
    }
}
