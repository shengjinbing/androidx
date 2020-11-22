package com.medi.hostapp.base;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.medi.hostapp.Utils;
import com.medi.hostapp.hookhelper.BaseDexClassLoaderHookHelper;

import java.io.File;

/**
 * Created by lixiang on 2020/5/15
 * Describe:
 */
public class BaseApplication extends Application {
    private static final String apkName = "plugin1.apk";
    private static final String dexName = "plugin1.dex";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        initHookDexClassLoader(newBase);

    }

    private void initHookDexClassLoader(Context newBase) {
        Utils.extractAssets(newBase, apkName);
        File dexFile = getFileStreamPath(apkName);
        Log.d("BBBBB","dexFile==="+dexFile.getAbsolutePath());
        File optDexFile = getFileStreamPath(dexName);
        Log.d("BBBBB","optDexFile==="+optDexFile.getAbsolutePath());
        Log.d("BBBBB","getCacheDir==="+getCacheDir().getAbsolutePath());
        Log.d("BBBBB","getFilesDir==="+getFilesDir().getAbsolutePath());
        Log.d("BBBBB","getClassLoader==="+getClassLoader().getClass().getSimpleName());
        try {
            BaseDexClassLoaderHookHelper.patchClassLoader(getClassLoader(), dexFile, optDexFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
