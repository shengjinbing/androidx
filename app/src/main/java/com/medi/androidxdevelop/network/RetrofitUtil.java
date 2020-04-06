package com.medi.androidxdevelop.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Retrofit工具类
 */
public class RetrofitUtil {

    private static final String ENC_NAME = "utf-8";
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * 根据param 构建RequestBody
     *
     * @param params Map
     * @return String
     */
    @NonNull
    public static RequestBody requestBody(@Nullable Map params) {
        return requestBody((Object) params);
    }

    /**
     * url参数编码
     *
     * @param params Map
     * @return String
     */
    @NonNull
    public static String encodeParam(@Nullable Map params) {
        return encodeParam((Object) params);
    }


    /**
     * 根据param 构建RequestBody
     *
     * @param object Object
     * @return String
     */
    @NonNull
    public static RequestBody requestBody(@Nullable Object object) {
        String jsonParam = toJSONStr(object);
        return RequestBody.create(MEDIA_TYPE_JSON, jsonParam);
    }

    /**
     * url参数编码
     *
     * @param object Object
     * @return String
     */
    @NonNull
    public static String encodeParam(@Nullable Object object) {
        String jsonParam = toJSONStr(object);
        return encode(jsonParam);
    }

    /**
     * Translates a string into {@code application/x-www-form-urlencoded}
     * format using a specific encoding scheme[UTF-8]
     */
    @NonNull
    public static String encode(@Nullable String s) {
        try {
            if (s == null) {
                return "";
            }
            return URLEncoder.encode(s, ENC_NAME);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e.getCause());
        }
    }

    /**
     * Decodes a {@code application/x-www-form-urlencoded} string using a specific
     * encoding scheme[UTF-8]
     */
    @NonNull
    public static String decode(@Nullable String s) {
        try {
            if (s == null) {
                return "";
            }
            return URLDecoder.decode(s, ENC_NAME);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e.getCause());
        }
    }


    /**
     * 转换为json格式的字符串
     *
     * @param obj
     * @return
     */
    @NonNull
    public static String toJSONStr(@Nullable Object obj) {
        Gson g = new GsonBuilder().create();
        return g.toJson(obj);
    }

    /**
     * 基础参数
     *
     * @return map
     */
    public static HashMap<String, Object> getBaseParam() {
        HashMap<String, Object> paramMap = new HashMap<>(4);
        return paramMap;
    }
}
