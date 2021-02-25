package com.medi.androidxdevelop.utils.toasts;

import android.os.Build;
import android.os.Handler;
import android.widget.Toast;

import java.lang.reflect.Field;

/**
 * Android 7.x Toast BadTokenException处理 https://mp.weixin.qq.com/s/08cq-PkSoJiWJC-jY9w46Q
 * 当我们将targetSDK升级到26以上后，发现项目中报告了很多BadTokenException异常，查看堆栈几乎都与Toast有关
 * 1.7.x版本，对Toast添加了Token验证，这本是对的，但是调用show()显示Toast时，如果有耗时操作卡住了主线程超过5秒，
 * 就会抛出BadTokenException的异常，而8.x系统开始，Google则在内部进行了try-catch
 * 2.Toast为什么不需要权限就可以显示。
 * 自己实现的悬浮窗和Toast主要的不同点在于WindowManager.LayoutParams的type。
 * WindowManager.LayoutParams的type有很多种，包括各种系统对话框，锁屏窗口，电话窗口等等，但这些窗口基本上都是需要权限的。
 * 而我们平时使用的Toast，并不需要权限就能显示，那就可以尝试直接把悬浮窗的类型设成TYPE_TOAST，来定制一个不需要权限的悬浮窗
 */
public class TNHookHelper {
    public static void hookHandle(Toast toast){
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
            try {
                /**
                 * 获取mTN对象
                 * 并获取它的class类型
                 */
                Class<Toast> clazzToast = Toast.class;
                Field fieldTN = clazzToast.getDeclaredField("mTN");
                fieldTN.setAccessible(true);
                Object objTn = fieldTN.get(toast);
                Class clazzTn = objTn.getClass();
                /**
                 * 获取TN中的mHandler对象
                 * 然后用我们自定义的HandlerProxy类包裹它
                 * 使得它能捕获异常
                 */
                Field fieldHandler = clazzTn.getDeclaredField("mHandler");
                fieldHandler.setAccessible(true);
                fieldHandler.set(objTn, new TNHandlerProxy((Handler) fieldHandler.get(objTn)));
            } catch (Throwable throwable) {
            }
        }


    }
}
