package com.medi.plugin1;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by lixiang on 2020/5/14
 * Describe:
 */
public class UIUtil {
    public static String getTextString(Context ctx){
        return ctx.getResources().getString(R.string.plugin_text);
    }

    public static Drawable getImageDrawable(Context ctx){
        return ctx.getResources().getDrawable(R.drawable.ic_recover);
    }

    public static View getLayout(Context ctx){
        return LayoutInflater.from(ctx).inflate(R.layout.activity_main, null);
    }
}
