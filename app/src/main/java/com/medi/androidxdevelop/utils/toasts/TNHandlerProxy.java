package com.medi.androidxdevelop.utils.toasts;

import android.os.Handler;
import android.os.Message;

/**
 * Created by lixiang on 2021/2/19
 * Describe:
 */
public class TNHandlerProxy extends Handler{
    private Handler mHandler;

    public TNHandlerProxy(Handler handler) {
        this.mHandler = handler;
    }

    @Override
    public void handleMessage(Message msg) {
        try {
            mHandler.handleMessage(msg);
        } catch (Throwable throwable) {
        }
    }

}
