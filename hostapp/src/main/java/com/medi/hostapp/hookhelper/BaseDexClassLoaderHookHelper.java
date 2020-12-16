package com.medi.hostapp.hookhelper;

import com.medi.hostapp.utils.RefInvoke;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;

/**
 *
 * /**
 *  * 由于应用程序使用的ClassLoader为PathClassLoader
 *  * 最终继承自 BaseDexClassLoader
 *  * 查看源码得知,这个BaseDexClassLoader加载代码根据一个叫做
 *  * dexElements的数组进行, 因此我们把包含代码的dex文件插入这个数组
 *  * 系统的classLoader就能帮助我们找到这个类
 *  *
 *  * 这个类用来进行对于BaseDexClassLoader的Hook
 *  * 类名太长, 不要吐槽.
 *  * @author lixiang
 *  * @date 2020/5/15
 *  */

public class BaseDexClassLoaderHookHelper {

    /**
     * hook宿主的classLoader可以将插件的dex和宿主的合并，省去一个插件需要创建一个classLoader(可以自己创建一个DexclassLoader实现)
     * @param cl 这里是PathClassLoader
     * @param apkFile
     * @param optDexFile
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws IOException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws NoSuchFieldException
     */
    public static void patchClassLoader(ClassLoader cl, File apkFile, File optDexFile)
            throws IllegalAccessException, NoSuchMethodException, IOException, InvocationTargetException, InstantiationException, NoSuchFieldException {
        // 获取 BaseDexClassLoader : pathList
        Object pathListObj = RefInvoke.getFieldObject(DexClassLoader.class.getSuperclass(), cl, "pathList");

        // 获取 PathList: Element[] dexElements
        Object[] dexElements = (Object[]) RefInvoke.getFieldObject(pathListObj, "dexElements");

        // Element 类型
        Class<?> elementClass = dexElements.getClass().getComponentType();

        // 创建一个数组, 用来替换原始的数组
        Object[] newElements = (Object[]) Array.newInstance(elementClass, dexElements.length + 1);

        // 构造插件Element(File file, boolean isDirectory, File zip, DexFile dexFile) 这个构造函数
        Class[] p1 = {File.class, boolean.class, File.class, DexFile.class};
        Object[] v1 = {apkFile, false, apkFile, DexFile.loadDex(apkFile.getCanonicalPath(), optDexFile.getAbsolutePath(), 0)};
        Object o = RefInvoke.createObject(elementClass, p1, v1);

        Object[] toAddElementArray = new Object[]{o};
        // 把原始的elements复制进去
        System.arraycopy(dexElements, 0, newElements, 0, dexElements.length);
        // 插件的那个element复制进去
        System.arraycopy(toAddElementArray, 0, newElements, dexElements.length, toAddElementArray.length);

        // 替换
        RefInvoke.setFieldObject(pathListObj, "dexElements", newElements);
    }
    }
