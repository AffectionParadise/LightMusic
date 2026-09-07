package net.doge.util.core.http.cookiejar;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CustomCookieJar implements CookieJar {
    // 使用 Map 在内存中存储 Cookie，key 为域名
    private final Map<String, List<Cookie>> cookieStore = new HashMap<>();

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        // 当服务器返回响应时，保存其中的 Cookie
        cookieStore.put(url.host(), cookies);
    }

    @NotNull
    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        // 在发送请求时，加载该域名下存储的 Cookie
        List<Cookie> cookies = cookieStore.get(url.host());
        return cookies != null ? cookies : new LinkedList<>();
    }
}
