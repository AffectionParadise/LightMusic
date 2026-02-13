package net.doge.sdk.util.http;

import net.doge.sdk.util.http.constant.Header;
import net.doge.sdk.util.http.constant.Method;
import okhttp3.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * HTTP 请求封装
 */
public class HttpRequest {
    private OkHttpClient customClient;

    private Request.Builder requestBuilder;
    private FormBody.Builder formBuilder;
    private Method method;

    // 私有构造器
    private HttpRequest(String url, Method method) {
        this.method = method;
        this.requestBuilder = new Request.Builder().url(url);
        switch (method) {
            case GET:
                requestBuilder.get();
                break;
            case POST:
                requestBuilder.post(createEmptyBody());
                break;
            case PUT:
                requestBuilder.put(createEmptyBody());
                break;
            case DELETE:
                requestBuilder.delete(createEmptyBody());
                break;
            case PATCH:
                requestBuilder.patch(createEmptyBody());
                break;
            case HEAD:
                requestBuilder.head();
                break;
        }

    }

    // 创建空 body
    private RequestBody createEmptyBody() {
        return RequestBody.create(new byte[0], null);
    }

    // 通过请求方法构造
    public static HttpRequest get(String url) {
        return new HttpRequest(url, Method.GET);
    }

    public static HttpRequest post(String url) {
        return new HttpRequest(url, Method.POST);
    }

    public static HttpRequest put(String url) {
        return new HttpRequest(url, Method.PUT);
    }

    public static HttpRequest delete(String url) {
        return new HttpRequest(url, Method.DELETE);
    }

    public static HttpRequest patch(String url) {
        return new HttpRequest(url, Method.PATCH);
    }

    public static HttpRequest head(String url) {
        return new HttpRequest(url, Method.HEAD);
    }

    public static HttpRequest createRequest(String url, Method method) {
        return new HttpRequest(url, method);
    }

    // Header
    public HttpRequest header(String name, String value) {
        requestBuilder.header(name, value);
        return this;
    }

    public HttpRequest header(String name, Object value) {
        return header(name, String.valueOf(value));
    }

    public HttpRequest headers(Map<String, String> headers) {
        requestBuilder.headers(Headers.of(headers));
        return this;
    }

    public HttpRequest cookie(Object value) {
        return header(Header.COOKIE, String.valueOf(value));
    }

    // Body
    private HttpRequest body(RequestBody body) {
        requestBuilder.method(method.getValue(), body);
        return this;
    }

    public HttpRequest body(String body) {
        if (body == null) return this;
        return body(RequestBody.create(body, MediaType.parse("application/x-www-form-urlencoded; charset=utf-8")));
    }

    // 表单
    public HttpRequest form(String name, Object value) {
        if (formBuilder == null) formBuilder = new FormBody.Builder();
        formBuilder.add(name, String.valueOf(value));
        return this;
    }

    public HttpRequest form(Map<String, ?> formMap) {
        formMap.forEach(this::form);
        return this;
    }

    // 显式构建表单
    private void buildForm() {
        if (formBuilder == null) return;
        body(formBuilder.build());
        formBuilder = null;
    }

    // 超时
    public HttpRequest timeout(int seconds) {
        return timeout(seconds, seconds, seconds);
    }

    public HttpRequest timeout(int connectTimeout, int readTimeout, int writeTimeout) {
        // 基于全局 CLIENT 创建一个新的 Client，只修改超时配置
        this.customClient = HttpClient.CLIENT.newBuilder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .build();
        return this;
    }

    // 执行
    public HttpResponse execute() {
        // 自动构建未处理的 form
        buildForm();

        Request request = requestBuilder.build();
        // 优先使用自定义 Client
        OkHttpClient client = customClient == null ? HttpClient.CLIENT : customClient;
        try {
            Response response = client.newCall(request).execute();
            return HttpResponse.of(response);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String executeAsStr() {
        return execute().body();
    }

    public byte[] executeAsBytes() {
        return execute().bodyBytes();
    }

    public InputStream executeAsStream() {
        return execute().bodyStream();
    }
}