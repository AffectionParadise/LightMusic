package net.doge.util.http.constant;

import lombok.Getter;

/**
 * 预定义的请求方法
 */
public enum Method {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    HEAD("HEAD"),
    TRACE("TRACE"),
    OPTIONS("OPTIONS"),
    PATCH("PATCH");

    @Getter
    private String value;

    Method(String value) {
        this.value = value;
    }
}
