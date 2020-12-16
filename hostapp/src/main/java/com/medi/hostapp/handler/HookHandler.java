package com.medi.hostapp.handler;

import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import com.medi.hostapp.activitys.StubActivity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

public class HookHandler implements InvocationHandler {

    private static final String TAG = "HookHandler";

    private Object mBase;

    public HookHandler(Object base) {
        mBase = base;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //Log.d(TAG, "hey, baby; you are hooked!!");
        //Log.d(TAG, "method:" + method.getName() + " called with args:" + Arrays.toString(args));
        if ("startActivity".equals(method.getName())){
            //拦截这个方法，替换参数，任你所为；甚至替换原始Activity启动别的Activity偷梁换柱
            //找到参数里面第一个Intent对象
            Intent raw;
            int index = 0;
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof  Intent){
                    index = i;
                    break;
                }
            }
            raw = (Intent) args[index];
            Intent newIntent = new Intent();
            String stubpackageName = raw.getComponent().getPackageName();
            ComponentName componentName = new ComponentName(stubpackageName, StubActivity.class.getName());
            newIntent.setComponent(componentName);
            //把我们原始要启动的TargetActivity先存起来
            newIntent.putExtra("extra_target_intent",raw);
            //替换掉Intent，达到欺骗AMS的目的
            args[index] = newIntent;
            Log.d(TAG, "方法名："+method.getName());
        }
        return method.invoke(mBase, args);
    }
}