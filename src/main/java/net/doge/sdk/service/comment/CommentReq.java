package net.doge.sdk.service.comment;

import net.doge.constant.service.source.NetResourceSource;
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
            case NetResourceSource.NC:
                return NcCommentReq.getInstance().getComments(resource, type, page, limit, cursor);
            case NetResourceSource.KG:
                return KgCommentReq.getInstance().getComments(resource, type, page, limit);
            case NetResourceSource.QQ:
                return QqCommentReq.getInstance().getComments(resource, type, page, limit);
            case NetResourceSource.KW:
                return KwCommentReq.getInstance().getComments(resource, type, page, limit);
            case NetResourceSource.MG:
                return MgCommentReq.getInstance().getComments(resource, type, page, limit, cursor);
            case NetResourceSource.XM:
                return XmCommentReq.getInstance().getComments(resource, type, page, limit);
            case NetResourceSource.HF:
                return HfCommentReq.getInstance().getComments(resource, page, limit);
            case NetResourceSource.GG:
                return GgCommentReq.getInstance().getComments(resource, page, limit);
            case NetResourceSource.FS:
                return FsCommentReq.getInstance().getComments(resource, type, page, limit);
            case NetResourceSource.ME:
                return MeCommentReq.getInstance().getComments(resource, type, page, limit);
            case NetResourceSource.HK:
                return HkCommentReq.getInstance().getComments(resource, page, limit);
            case NetResourceSource.DB:
                return DbCommentReq.getInstance().getComments(resource, type, page, limit);
            case NetResourceSource.BI:
                return BiCommentReq.getInstance().getComments(resource, type, page, limit);
//            case NetMusicSource.YY:
//                return YyCommentReq.getInstance().getComments(resource, type, page, limit, cursor);
            case NetResourceSource.LZ:
                return LzCommentReq.getInstance().getComments(resource, page, limit, cursor);
            default:
                return CommonResult.create();
        }
    }
}
