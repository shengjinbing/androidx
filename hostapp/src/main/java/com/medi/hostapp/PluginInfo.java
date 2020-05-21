package com.medi.hostapp;

import dalvik.system.DexClassLoader;

/**
 * Created by lixiang on 2020/5/14
 * Describe:
 */
public class PluginInfo {
    private String dexPath;
    private DexClassLoader classLoader;

    public PluginInfo(String dexPath, DexClassLoader classLoader) {
        this.dexPath = dexPath;
        this.classLoader = classLoader;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public String getDexPath() {
        return dexPath;
    }
}
