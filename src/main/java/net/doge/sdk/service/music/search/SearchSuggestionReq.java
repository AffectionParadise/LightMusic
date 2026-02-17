package net.doge.sdk.service.music.search;

import net.doge.sdk.common.entity.executor.MultiListCallableExecutor;
import net.doge.sdk.service.music.search.impl.searchsuggestion.*;
import net.doge.util.core.StringUtil;

import java.util.LinkedHashSet;
import java.util.Set;

public class SearchSuggestionReq {
    private static SearchSuggestionReq instance;

    private SearchSuggestionReq() {
    }

    public static SearchSuggestionReq getInstance() {
        if (instance == null) instance = new SearchSuggestionReq();
        return instance;
    }

    /**
     * 获取搜索建议
     *
     * @return
     */
    public Set<String> getSearchSuggestion(String keyword) {
        // 关键词为空时直接跳出
        if (StringUtil.isEmpty(keyword.trim())) return new LinkedHashSet<>();
        MultiListCallableExecutor<String> executor = new MultiListCallableExecutor<>();
        executor.submit(() -> NcSearchSuggestionReq.getInstance().getSimpleSearchSuggestion(keyword));
        executor.submit(() -> NcSearchSuggestionReq.getInstance().getSearchSuggestion(keyword));
        executor.submit(() -> KgSearchSuggestionReq.getInstance().getSearchSuggestion(keyword));
        executor.submit(() -> QqSearchSuggestionReq.getInstance().getSearchSuggestion(keyword));
        executor.submit(() -> KwSearchSuggestionReq.getInstance().getSearchSuggestion(keyword));
        executor.submit(() -> MgSearchSuggestionReq.getInstance().getSearchSuggestion(keyword));
        executor.submit(() -> QiSearchSuggestionReq.getInstance().getSearchSuggestion(keyword));
        return executor.getResultAsSet();
    }
}
