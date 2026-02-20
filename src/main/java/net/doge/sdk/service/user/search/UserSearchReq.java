package net.doge.sdk.service.user.search;

import net.doge.constant.service.source.NetResourceSource;
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
        if (src == NetResourceSource.NC || src == NetResourceSource.ALL)
            executor.submit(() -> NcUserSearchReq.getInstance().searchUsers(keyword, page, limit));
        if (src == NetResourceSource.QQ || src == NetResourceSource.ALL)
            executor.submit(() -> QqUserSearchReq.getInstance().searchUsers(keyword, page, limit));
        if (src == NetResourceSource.XM || src == NetResourceSource.ALL)
            executor.submit(() -> XmUserSearchReq.getInstance().searchUsers(keyword, page, limit));
        if (src == NetResourceSource.ME || src == NetResourceSource.ALL)
            executor.submit(() -> MeUserSearchReq.getInstance().searchUsers(keyword, page, limit));
        if (src == NetResourceSource.FS || src == NetResourceSource.ALL)
            executor.submit(() -> FsUserSearchReq.getInstance().searchUsers(keyword, page, limit));
        if (src == NetResourceSource.HK || src == NetResourceSource.ALL)
            executor.submit(() -> HkUserSearchReq.getInstance().searchUsers(keyword, page, limit));
        if (src == NetResourceSource.DB || src == NetResourceSource.ALL)
            executor.submit(() -> DbUserSearchReq.getInstance().searchUsers(keyword, page, limit));
        if (src == NetResourceSource.DT || src == NetResourceSource.ALL)
            executor.submit(() -> DtUserSearchReq.getInstance().searchUsers(keyword, page, limit));
        if (src == NetResourceSource.BI || src == NetResourceSource.ALL)
            executor.submit(() -> BiUserSearchReq.getInstance().searchUsers(keyword, page));
        return executor.getResult();
    }
}
