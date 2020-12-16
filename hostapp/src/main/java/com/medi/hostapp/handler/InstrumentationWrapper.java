package com.medi.hostapp.handler;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import com.medi.hostapp.utils.RefInvoke;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

public class InstrumentationWrapper extends Instrumentation {
    public static final String TAG = "Instrumentation_log";

    private android.app.Instrumentation mInstrumentation;

    public InstrumentationWrapper(Instrumentation mInstrumentation) {
        this.mInstrumentation = mInstrumentation;
    }

    public Activity newActivity(Class<?> clazz, Context context,
                                IBinder token, Application application, Intent intent, ActivityInfo info,
                                CharSequence title, Activity parent, String id,
                                Object lastNonConfigurationInstance) throws InstantiationException, IllegalAccessException {
        Log.d(TAG, "newActivity方法被调用");
        return mInstrumentation.newActivity(clazz, context, token, application, intent, info,
                title, parent, id, lastNonConfigurationInstance);
    }

    public Activity newActivity(ClassLoader cl, String className,
                                Intent intent) throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        Log.d(TAG, "newActivity方法被调用===参数少的");
        Intent rawIntent = intent.getParcelableExtra("extra_target_intent");
        if (intent == null){
            return mInstrumentation.newActivity(cl, className, intent);
        }
        String newClassName = rawIntent.getComponent().getClassName();
        return mInstrumentation.newActivity(cl, newClassName, intent);
    }

    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        Log.d(TAG, "callActivityOnCreate方法被调用");
        mInstrumentation.callActivityOnCreate(activity, icicle);
    }

    //{@hide}的方法，用反射调用
    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Activity target,
                                            Intent intent, int requestCode, Bundle options) {
        Log.d(TAG, "execStartActivity");
        return (ActivityResult) RefInvoke.invokeInstanceMethod(mInstrumentation, "execStartActivity",
                new Class[]{Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class, int.class, Bundle.class},
                new Object[]{who, contextThread, token, target, intent, requestCode, options});
    }

}
