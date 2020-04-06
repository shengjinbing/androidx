package com.medi.androidxdevelop.network.interceptor;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author caichen QQ:345233199
 * @name maikun
 * @class name：com.medi.comm.network.interceptor
 * @class describe
 * @time 2020/3/21 9:18
 * @class describe
 */
public class BaseUrlManagerInterceptor implements Interceptor {
    // Retrofit初始化时候的url
    private String mOriginalBaseUrl;
    private String mNewBaseUrl;

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request oldRequest = chain.request();
        HttpUrl requestUrl = oldRequest.url();
        String oldUrl = requestUrl.toString();

        if (oldUrl.contains(mOriginalBaseUrl)&&oldUrl.startsWith(mOriginalBaseUrl)){
            return chain.proceed(chain.request());
        }else {
            Request newRequest = oldRequest
                    .newBuilder()
                    .url(oldUrl)
                    .build();
            return chain.proceed(newRequest);
        }
    }

    public void setOriginalBaseUrl(String originalBaseUrl) {
        mOriginalBaseUrl = originalBaseUrl;
    }

    public String getOriginalBaseUrl() {
        return mOriginalBaseUrl;
    }

    public String getNewBaseUrl() {
        return mNewBaseUrl;
    }

    public void setNewBaseUrl(String newBaseUrl) {
        mNewBaseUrl = newBaseUrl;
    }
}
