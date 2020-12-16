package com.medi.hostapp.handler;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

public class McallbackMock implements Handler.Callback {
    private Handler mH;
    public McallbackMock(Handler mH){
        this.mH = mH;
    }
    @Override
    public boolean handleMessage(@NonNull Message msg) {
        Log.d("BBBBB",msg.what+"");
        if (msg.what == 100) {
            //启动activity
            handleLaunchActivity(msg);
        }
        mH.handleMessage(msg);
        return true;
    }
    private void handleLaunchActivity(Message msg){
        Object obj = msg.obj;
        Log.d("BBBBB",obj.toString());
    }
}
