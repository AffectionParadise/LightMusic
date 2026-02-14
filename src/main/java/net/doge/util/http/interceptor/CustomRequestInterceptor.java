package net.doge.util.http.interceptor;

import net.doge.sdk.common.SdkCommon;
import net.doge.util.core.StringUtil;
import net.doge.util.http.constant.Header;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义请求拦截器
 */
public class CustomRequestInterceptor implements Interceptor {
    // 只在不存在时才添加的 Header
    private Map<String, String> conditionalHeaders;

    public CustomRequestInterceptor() {
        this.conditionalHeaders = new HashMap<>();
        // 初始化默认值
        initConditionalHeaders();
    }

    private void initConditionalHeaders() {
        conditionalHeaders.put(Header.USER_AGENT, SdkCommon.USER_AGENT);
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder builder = original.newBuilder();

        // 添加条件 Header (只在不存在时)
        for (Map.Entry<String, String> entry : conditionalHeaders.entrySet()) {
            String name = entry.getKey();
            if (StringUtil.notEmpty(original.header(name))) continue;
            String value = entry.getValue();
            builder.header(name, value);
        }

        return chain.proceed(builder.build());
    }
}
