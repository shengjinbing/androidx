package com.medi.hostapp.hookhelper;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

import com.medi.hostapp.handler.HookHandler;
import com.medi.hostapp.handler.InstrumentationWrapper;
import com.medi.hostapp.handler.McallbackMock;
import com.medi.hostapp.handler.PMSHandler;
import com.medi.hostapp.utils.RefInvoke;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class HookHelper {

    /**
     * hook下半场的ActivityThread里面的mInstrumentation
     */
    public static void hookActivityThreadmInstrumentation(){
        Object sCurrentActivityThread = RefInvoke.getStaticFieldObject("android.app.ActivityThread", "sCurrentActivityThread");
        Object mInstrumentation = RefInvoke.getFieldObject(sCurrentActivityThread,"mInstrumentation");
        InstrumentationWrapper wrapper = new InstrumentationWrapper((Instrumentation) mInstrumentation);
        RefInvoke.setFieldObject(sCurrentActivityThread,"mInstrumentation",wrapper);
    }
    /**
     * 无法直接hook H类 因为H类不对外暴露
     */
    public static void hookmCallback(){
        //获取ActivityThread对象
        Object sCurrentActivityThread = RefInvoke.getStaticFieldObject("android.app.ActivityThread", "sCurrentActivityThread");
        Object mH = RefInvoke.getFieldObject("android.app.ActivityThread", sCurrentActivityThread, "mH");
        RefInvoke.setFieldObject("android.os.Handler",mH,"mCallback",new McallbackMock((Handler)mH));
    }

    /**
     * 这种方式只在当前activity生效,可以放在BaseActivity里面
     * @param activity
     */
    public static void hookmInstrumentation(Activity activity){
        Object mInstrumentation = RefInvoke.getFieldObject(Activity.class, activity,"mInstrumentation");
        InstrumentationWrapper wrapper = new InstrumentationWrapper((Instrumentation) mInstrumentation);
        RefInvoke.setFieldObject(Activity.class,activity,"mInstrumentation",wrapper);
    }

    public static void hookActivityManagerAndroid29() {
        try {
            //获取AMN的gDefault单例gDefault，gDefault是静态的
            Object gDefault = RefInvoke.getStaticFieldObject("android.app.ActivityTaskManager", "IActivityTaskManagerSingleton");

            // gDefault是一个 android.util.Singleton对象; 我们取出这个单例里面的mInstance字段，IActivityManager类型
            Object rawIActivityManager = RefInvoke.getFieldObject(
                    "android.util.Singleton",
                    gDefault, "mInstance");


            // 创建一个这个对象的代理对象iActivityManagerInterface, 然后替换这个字段, 让我们的代理对象帮忙干活
            Class<?> IActivityTaskManager = Class.forName("android.app.IActivityTaskManager");
            Object proxy = Proxy.newProxyInstance(
                    Thread.currentThread().getContextClassLoader(),
                    new Class<?>[] { IActivityTaskManager },
                    new HookHandler(rawIActivityManager));

            //把Singleton的mInstance替换为proxy
            RefInvoke.setFieldObject("android.util.Singleton", gDefault, "mInstance", proxy);

        } catch (Exception e) {
            Log.d("HookHandler", "Hook Failed");
            throw new RuntimeException("Hook Failed", e);

        }
    }

    /**
     * 针对android6.0版本的hook
     */
    public static void hookActivityManager() {
        try {
            //获取AMN的gDefault单例gDefault，gDefault是静态的
            Object gDefault = RefInvoke.getStaticFieldObject("android.app.ActivityManagerNative", "gDefault");

            // gDefault是一个 android.util.Singleton对象; 我们取出这个单例里面的mInstance字段，IActivityManager类型
            Object rawIActivityManager = RefInvoke.getFieldObject(
                    "android.util.Singleton",
                    gDefault, "mInstance");


            // 创建一个这个对象的代理对象iActivityManagerInterface, 然后替换这个字段, 让我们的代理对象帮忙干活
            Class<?> iActivityManagerInterface = Class.forName("android.app.IActivityManager");
            Object proxy = Proxy.newProxyInstance(
                    Thread.currentThread().getContextClassLoader(),
                    new Class<?>[] { iActivityManagerInterface },
                    new HookHandler(rawIActivityManager));

            //把Singleton的mInstance替换为proxy
            RefInvoke.setFieldObject("android.util.Singleton", gDefault, "mInstance", proxy);

        } catch (Exception e) {
            Log.d("HookHandler", "Hook Failed");
            throw new RuntimeException("Hook Failed", e);

        }
    }

    public static void hookPackageManager(Context context) {
        try {
            // 获取全局的ActivityThread对象
            Object currentActivityThread = RefInvoke.getStaticFieldObject("android.app.ActivityThread",
                    "sCurrentActivityThread");

            // 获取ActivityThread里面原始的 sPackageManager
            Object sPackageManager = RefInvoke.getFieldObject(currentActivityThread, "sPackageManager");


            // 准备好代理对象, 用来替换原始的对象
            Class<?> iPackageManagerInterface = Class.forName("android.content.pm.IPackageManager");
            Object proxy = Proxy.newProxyInstance(iPackageManagerInterface.getClassLoader(),
                    new Class<?>[] { iPackageManagerInterface },
                    new PMSHandler(sPackageManager));

            // 1. 替换掉ActivityThread里面的 sPackageManager 字段
            RefInvoke.setFieldObject(currentActivityThread, "sPackageManager", proxy);

            // 2. 替换 ApplicationPackageManager里面的 mPm对象
            PackageManager pm = context.getPackageManager();
            RefInvoke.setFieldObject(pm, "mPM", proxy);

        } catch (Exception e) {
            throw new RuntimeException("hook failed", e);
        }
    }

}
