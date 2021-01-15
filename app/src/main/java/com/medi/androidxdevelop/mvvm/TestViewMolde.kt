package com.medi.androidxdevelop.mvvm

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.medi.androidxdevelop.mvvm.entity.FeeEntity
import com.medi.androidxdevelop.mvvm.repository.LoginRepository
import com.medi.androidxdevelop.network.ApiService
import com.medi.comm.network.result.awaitOrError
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * 1.为应用中的每个 ViewModel 定义了 ViewModelScope。如果 ViewModel 已清除，则在此范围内启动的协
 * 程都会自动取消。如果您具有仅在 ViewModel 处于活动状态时才需要完成的工作，此时协程非常有用。
 */
class TestViewModel : ViewModel() {
    var fee = MutableLiveData<MutableList<FeeEntity>>()
    var userName:MutableLiveData<String>? = null
    var password:MutableLiveData<String>? = null

    private var loginRepository: LoginRepository = LoginRepository()
    init {
        userName =MutableLiveData<String>("")
        userName?.postValue(loginRepository.getUserNameFormCache())
        password =MutableLiveData<String>("")
    }
    fun getFeeData() {
        viewModelScope.launch {
            val (data, netExecption) = async {
                ApiService.apiService.getfee()
                loginRepository.login(userName?.value.toString(), password?.value.toString())
            }.awaitOrError()
            netExecption?.composeException { code, message ->
                Log.d("BBBBB", "code==${code}  message==${message}")
            } ?: kotlin.run {
                fee.value = data?.data
                password?.postValue(data?.data?.get(1)?.dictName)
            }
        }
    }


    //初始化ViewModel
    companion object {
        fun instanceOf(activity: AppCompatActivity): TestViewModel =
            ViewModelProvider(activity, ViewModelFactory).get(TestViewModel::class.java)

        fun instanceOf(fragment: Fragment): TestViewModel =
            ViewModelProvider(fragment, ViewModelFactory)[TestViewModel::class.java]
    }

    private object ViewModelFactory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (TestViewModel::class.java.isAssignableFrom(modelClass)) {
                return modelClass.newInstance()
            }
            throw IllegalArgumentException("FollowViewModel is not assignable from $modelClass")
        }
    }
}

/**
 * 1.的生命周期处于 STARTED 或 RESUMED 状态，则 LiveData 会认为该观察者处于活跃状态。LiveData 只会将更新
 *   通知给活跃的观察者。为观察 LiveData 对象而注册的非活跃观察者不会收到更改通知。
 * 2.当 Activity 和 Fragment 的生命周期被销毁时，系统会立即退订它们
 */