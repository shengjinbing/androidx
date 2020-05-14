package com.medi.hook.utils;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.medi.hook.StubActivity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


/**
 * Created by lixiang on 2020/5/13
 * Describe:
 */
public class AMSHookHelper {
    public static final String TAG = "AMSHookHelper";

    public static final String EXTRA_TARGET_INTENT = "extra_target_intent";

    public static void hookAMN() throws ClassNotFoundException {
        Log.d(TAG,"hookAMN");
        Object gDefault = RefInvoke.getStaticFieldObject(
                "android.app.ActivityManagerNative",
                "gDefault"
        );
        //取出单例里面的实例
        Object mInstance = RefInvoke.getFieldObject(
                "android.util.Singleton",
                gDefault,
                "mInstance");

        Class<?> classB2Interface = Class.forName("android.app.IActivityManager");
        Object proxy = Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[]{classB2Interface},
                new MockClass1(mInstance)
        );

        //把gDefault的mInstance替换成proxy
        RefInvoke.setFieldObject(
                "android.util.Singleton",
                gDefault,
                "mInstance",
                proxy);

    }


    static class MockClass1 implements InvocationHandler {
        private Object mInstance;

        public MockClass1(Object mInstance) {
            this.mInstance = mInstance;
        }

        /**
         * @param proxy
         * @param method
         * @param args 方法参数
         * @return
         * @throws Throwable
         */
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Log.d(TAG,"MockClass1_invoke");
            Intent raw;
            int index = 0;
            if ("startActivity".equals(method.getName())) {
                //启动的地方
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof Intent) {
                        index = i;
                        break;
                    }
                }
            }
            raw = (Intent) args[index];
            //替身的包名，也就是我们自己的包名
            String packageName = raw.getComponent().getPackageName();
            //创建一个新的intent
            Intent newIntent = new Intent();
            ComponentName componentName = new ComponentName(packageName, StubActivity.class.getName());
            newIntent.setComponent(componentName);
            newIntent.putExtra(EXTRA_TARGET_INTENT,raw);

            //替换掉Intent以欺骗AMS为目的
            args[index] = newIntent;

            return method.invoke(mInstance,args);
        }
    }

    /**
     * 通过hook  mH的mCallback对象
     */
    public static void attachBaseContext(){
        Log.d(TAG,"attachBaseContext");

        //获取当前的ActivityThread对象
        Object sCurrentActivityThread = RefInvoke.getStaticFieldObject("android.app.ActivityThread", "sCurrentActivityThread");

        Handler mH = (Handler)RefInvoke.getFieldObject(sCurrentActivityThread, "mH");

        RefInvoke.setFieldObject(Handler.class,mH,"mCallback",new MockClass2(mH));

    }


    static class MockClass2 implements android.os.Handler.Callback{
        Handler mH;

        public MockClass2(Handler mH) {
            this.mH = mH;
        }

        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Log.d(TAG,"attachBaseContext_MockClass2");
            switch (msg.arg1){
                case 100:{
                    //启动activity
                    handleMessage(msg);
                }
                break;
            }
            mH.handleMessage(msg);
            return false;
        }

        private void handleLaunchActivity(Message msg){
            //ActivityClientRecord里面
            Object obj = msg.obj;
            Intent intent = (Intent)RefInvoke.getFieldObject(obj, "intent");
            Intent tragetIntent = intent.getParcelableExtra(EXTRA_TARGET_INTENT);
            intent.setComponent(tragetIntent.getComponent());
        }
    }

    /**
     * hook Intrumentation的newActivity
     */
    public static void hookIntrumentation(){
        Object sCurrentActivityThread = RefInvoke.getStaticFieldObject("android.app.ActivityThread", "sCurrentActivityThread");
        Instrumentation mInstrumentation = (Instrumentation)RefInvoke.getFieldObject(sCurrentActivityThread, "mInstrumentation");
        RefInvoke.setFieldObject(sCurrentActivityThread,"mInstrumentation",new EvilInstrumentation(mInstrumentation));
    }


    public static class EvilInstrumentation extends Instrumentation{
        Instrumentation mInstrumentation;

        public EvilInstrumentation(Instrumentation mInstrumentation) {
            this.mInstrumentation = mInstrumentation;
        }

        @Override
        public Activity newActivity(ClassLoader cl, String className, Intent intent) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
            Intent tragetIntent = intent.getParcelableExtra(EXTRA_TARGET_INTENT);
            if (tragetIntent == null){
                mInstrumentation.newActivity(cl,className,intent);
            }
            return mInstrumentation.newActivity(cl,tragetIntent.getComponent().getClassName(),tragetIntent);
        }
    }
}
