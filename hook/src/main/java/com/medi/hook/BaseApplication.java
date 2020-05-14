package com.medi.hook;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.medi.hook.utils.AMSHookHelper;

/**
 * Created by lixiang on 2020/5/14
 * Describe:
 */
public class BaseApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        /*try {
            AMSHookHelper.hookAMN();
            AMSHookHelper.attachBaseContext();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.d(AMSHookHelper.TAG, String.valueOf(e.getCause()));
        }*/

    }
}
