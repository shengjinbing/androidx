“终于懂了“系列：Jetpack完整解析，ViewModel 全面掌握！
https://mp.weixin.qq.com/s/I38ZvOyyzlJ9q2BRHmBsbw 重点
3.2 ViewModelStore的存储和获取
回到上面的疑问，看看 Activity/Fragment 是怎样实现 获取ViewModelStore的，先来看ComponentActivity中对ViewModelStoreOwner的实现：
//ComponentActivity.java
public ViewModelStore getViewModelStore() {
    if (getApplication() == null) {
    //activity还没关联Application，即不能在onCreate之前去获取viewModel
        throw new IllegalStateException("Your activity is not yet attached to the "
                + "Application instance. You can't request ViewModel before onCreate call.");
    }
    if (mViewModelStore == null) {
    //如果存储器是空，就先尝试 从lastNonConfigurationInstance从获取
        NonConfigurationInstances nc =
                (NonConfigurationInstances) getLastNonConfigurationInstance();
        if (nc != null) {
            mViewModelStore = nc.viewModelStore;
        }
        if (mViewModelStore == null) {
        //如果lastNonConfigurationInstance不存在，就new一个
            mViewModelStore = new ViewModelStore();
        }
    }
    return mViewModelStore;
}
这里就是重点了。先尝试 从NonConfigurationInstance从获取 ViewModelStore实例，如果NonConfigurationInstance不存在，
就new一个mViewModelStore。并且还注意到，在onRetainNonConfigurationInstance()方法中 会把mViewModelStore赋值给NonConfigurationInstances：
//在Activity因配置改变 而正要销毁时，且新Activity会立即创建，那么系统就会调用此方法
public final Object onRetainNonConfigurationInstance() {
    Object custom = onRetainCustomNonConfigurationInstance();

    ViewModelStore viewModelStore = mViewModelStore;
    ...
    if (viewModelStore == null && custom == null) {
        return null;
    }

//new了一个NonConfigurationInstances，mViewModelStore赋值过来
    NonConfigurationInstances nci = new NonConfigurationInstances();
    nci.custom = custom;
    nci.viewModelStore = viewModelStore;
    return nci;
}

onRetainNonConfigurationInstance()方法很重要：在Activity因配置改变 而正要销毁时，且新Activity会立即创建，那么系统就会调用此方法。
也就说，配置改变时 系统把viewModelStore存在了NonConfigurationInstances中。
NonConfigurationInstances是个啥呢？
//ComponentActivity
static final class NonConfigurationInstances {
    Object custom;
    ViewModelStore viewModelStore;
}

方法是在Acticity.java中，它返回的是Acticity.java中的NonConfigurationInstances的属性activity，也就
是onRetainNonConfigurationInstance()方法返回的实例。（注意上面那个是ComponentActivity中的NonConfigurationInstances，是两个类）
来继续看mLastNonConfigurationInstances是哪来的，通过寻找调用找到在attach()方法中：
final void attach(Context context, ActivityThread aThread, ...
    NonConfigurationInstances lastNonConfigurationInstances,... ) {
    ...
    mLastNonConfigurationInstances = lastNonConfigurationInstances;
    ...
}

mLastNonConfigurationInstances是在Activity的attach方法中赋值。在《Activity的启动过程详解》中我们分析过，attach
方法是为Activity关联上下文环境，是在Activity 启动的核心流程——ActivityThread的performLaunchActivity方法中调用，
这里的lastNonConfigurationInstances是存在 ActivityClientRecord中的一个组件信息。(重点)
https://mp.weixin.qq.com/s/5k00vaDGCd7zlj1z-EIGSg
ActivityClientRecord是存在ActivityThread的mActivities中：
//ActivityThrtead.java
final ArrayMap<IBinder, ActivityClientRecord> mActivities = new ArrayMap<>();