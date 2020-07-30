package com.medi.daggerdemo.di.module

import com.medi.daggerdemo.login.LoginRetrofitService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

/**
 * Created by lixiang on 2020/7/28
 * Describe:
 */
/*除了 @Inject 注释之外，还有一种方法可告知 Dagger 如何提供类实例，即使用 Dagger 模块中的信息。
Dagger 模块是一个带有 @Module 注释的类。您可以在其中使用 @Provides 注释定义依赖项

为什么会有module
在本例中，您使用的是 Retrofit 网络库。UserRemoteDataSource 依赖于 LoginRetrofitService。
不过，创建 LoginRetrofitService 实例的方法与您到目前为止一直执行的操作有所不同。它不是类实例化，
而是调用 Retrofit.Builder() 并传入不同参数以配置登录服务的结果。
*/
@Module
class NetworkModule {
    @Provides
    fun provideLoginRetrofitService(): LoginRetrofitService {
        // Whenever Dagger needs to provide an instance of type LoginRetrofitService,
        // this code (the one inside the @Provides method) is run.
        return Retrofit.Builder()
            .baseUrl("https://example.com")
            .build()
            .create(LoginRetrofitService::class.java)
    }
}