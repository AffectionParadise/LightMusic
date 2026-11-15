package net.doge.util.core.http;

import net.doge.util.core.http.constant.Method;
import net.doge.util.core.http.cookiejar.CustomCookieJar;
import okhttp3.OkHttpClient;

/**
 * HTTP 会话封装
 */
public class HttpSession {
    private OkHttpClient client;

    public HttpSession() {
        // 共用同一个 CookieJar
        this.client = HttpClient.CLIENT.newBuilder()
                .cookieJar(new CustomCookieJar())
                .build();
    }

    // 通过请求方法构造
    public HttpRequest createRequest(Method method, String url) {
        return new HttpRequest(client, method, url);
    }

    public HttpRequest get(String url) {
        return createRequest(Method.GET, url);
    }

    public HttpRequest post(String url) {
        return createRequest(Method.POST, url);
    }

    public HttpRequest put(String url) {
        return createRequest(Method.PUT, url);
    }

    public HttpRequest delete(String url) {
        return createRequest(Method.DELETE, url);
    }

    public HttpRequest patch(String url) {
        return createRequest(Method.PATCH, url);
    }

    public HttpRequest head(String url) {
        return createRequest(Method.HEAD, url);
    }

    public HttpRequest trace(String url) {
        return createRequest(Method.TRACE, url);
    }

    public HttpRequest options(String url) {
        return createRequest(Method.OPTIONS, url);
    }
}