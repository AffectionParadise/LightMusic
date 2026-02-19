package net.doge.sdk.service.music.search;

import net.doge.sdk.common.entity.executor.MultiListCallableExecutor;
import net.doge.sdk.service.music.search.impl.hotsearch.*;

import java.util.Set;

public class HotSearchReq {
    private static HotSearchReq instance;

    private HotSearchReq() {
    }

    public static HotSearchReq getInstance() {
        if (instance == null) instance = new HotSearchReq();
        return instance;
    }

    /**
     * 获取热搜
     *
     * @return
     */
    public Set<String> getHotSearch() {
        MultiListCallableExecutor<String> executor = new MultiListCallableExecutor<>();
        executor.submit(() -> NcHotSearchReq.getInstance().getHotSearch());
        executor.submit(() -> KgHotSearchReq.getInstance().getHotSearch());
        executor.submit(() -> QqHotSearchReq.getInstance().getHotSearch());
        executor.submit(() -> KwHotSearchReq.getInstance().getHotSearch());
        executor.submit(() -> KwHotSearchReq.getInstance().getHotSearchWeb());
        executor.submit(() -> MgHotSearchReq.getInstance().getHotSearch());
//        executor.submit(() -> FsHotSearchReq.getInstance().getHotSearch());
        return executor.getResultAsSet();
    }
}
