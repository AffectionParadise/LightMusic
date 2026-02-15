package net.doge.util.core.http;

import net.doge.util.core.exception.ExceptionUtil;
import net.doge.util.core.http.constant.ContentType;
import net.doge.util.core.http.constant.Header;
import net.doge.util.core.http.constant.Method;
import okhttp3.*;
import okhttp3.internal.http.HttpMethod;

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
    private HttpRequest(Method method, String url) {
        this.method = method;
        this.requestBuilder = new Request.Builder().url(url);
        String methodStr = method.getValue();
        requestBuilder.method(methodStr, HttpMethod.requiresRequestBody(methodStr) ? createEmptyBody() : null);
    }

    // 创建空 body
    private RequestBody createEmptyBody() {
        return RequestBody.create(new byte[0]);
    }

    // 通过请求方法构造
    public static HttpRequest get(String url) {
        return new HttpRequest(Method.GET, url);
    }

    public static HttpRequest post(String url) {
        return new HttpRequest(Method.POST, url);
    }

    public static HttpRequest put(String url) {
        return new HttpRequest(Method.PUT, url);
    }

    public static HttpRequest delete(String url) {
        return new HttpRequest(Method.DELETE, url);
    }

    public static HttpRequest patch(String url) {
        return new HttpRequest(Method.PATCH, url);
    }

    public static HttpRequest head(String url) {
        return new HttpRequest(Method.HEAD, url);
    }

    public static HttpRequest trace(String url) {
        return new HttpRequest(Method.TRACE, url);
    }

    public static HttpRequest options(String url) {
        return new HttpRequest(Method.OPTIONS, url);
    }

    public static HttpRequest createRequest(Method method, String url) {
        return new HttpRequest(method, url);
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
        if (headers != null) headers.forEach(this::header);
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

    public HttpRequest jsonBody(String json) {
        return json == null ? this : body(RequestBody.create(json, MediaType.parse(ContentType.JSON)));
    }

    public HttpRequest formBody(String form) {
        return form == null ? this : body(RequestBody.create(form, MediaType.parse(ContentType.FORM)));
    }

    // 表单
    public HttpRequest form(String name, Object value) {
        if (formBuilder == null) formBuilder = new FormBody.Builder();
        formBuilder.add(name, String.valueOf(value));
        return this;
    }

    public HttpRequest form(Map<String, ?> forms) {
        if (forms != null) forms.forEach(this::form);
        return this;
    }

    // 显式构建表单
    private void buildForm() {
        if (formBuilder != null) body(formBuilder.build());
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
            ExceptionUtil.throwRuntimeException(e);
        }
        return null;
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