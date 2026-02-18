package net.doge.sdk.service.user.search;

import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.entity.executor.MultiCommonResultCallableExecutor;
import net.doge.sdk.service.user.search.impl.*;

public class UserSearchReq {
    private static UserSearchReq instance;

    private UserSearchReq() {
    }

    public static UserSearchReq getInstance() {
        if (instance == null) instance = new UserSearchReq();
        return instance;
    }

    /**
     * 根据关键词获取用户
     */
    public CommonResult<NetUserInfo> searchUsers(int src, String keyword, int page, int limit) {
        MultiCommonResultCallableExecutor<NetUserInfo> executor = new MultiCommonResultCallableExecutor<>();
        if (src == NetMusicSource.NC || src == NetMusicSource.ALL)
            executor.submit(() -> NcUserSearchReq.getInstance().searchUsers(keyword, page, limit));
        if (src == NetMusicSource.QQ || src == NetMusicSource.ALL)
            executor.submit(() -> QqUserSearchReq.getInstance().searchUsers(keyword, page, limit));
        if (src == NetMusicSource.XM || src == NetMusicSource.ALL)
            executor.submit(() -> XmUserSearchReq.getInstance().searchUsers(keyword, page, limit));
        if (src == NetMusicSource.ME || src == NetMusicSource.ALL)
            executor.submit(() -> MeUserSearchReq.getInstance().searchUsers(keyword, page, limit));
        if (src == NetMusicSource.FS || src == NetMusicSource.ALL)
            executor.submit(() -> FsUserSearchReq.getInstance().searchUsers(keyword, page, limit));
        if (src == NetMusicSource.HK || src == NetMusicSource.ALL)
            executor.submit(() -> HkUserSearchReq.getInstance().searchUsers(keyword, page, limit));
        if (src == NetMusicSource.DB || src == NetMusicSource.ALL)
            executor.submit(() -> DbUserSearchReq.getInstance().searchUsers(keyword, page, limit));
        if (src == NetMusicSource.DT || src == NetMusicSource.ALL)
            executor.submit(() -> DtUserSearchReq.getInstance().searchUsers(keyword, page, limit));
        if (src == NetMusicSource.BI || src == NetMusicSource.ALL)
            executor.submit(() -> BiUserSearchReq.getInstance().searchUsers(keyword, page));
        return executor.getResult();
    }
}
