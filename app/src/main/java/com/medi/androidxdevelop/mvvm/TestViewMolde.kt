package com.medi.androidxdevelop.mvvm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medi.androidxdevelop.mvvm.Entity.FeeEntity
import com.medi.androidxdevelop.network.ApiService
import com.medi.comm.network.result.awaitOrError
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * Created by lixiang on 2020/4/1
 * Describe:
 */
class TestViewMolde : ViewModel() {
    var fee = MutableLiveData<MutableList<FeeEntity>>()
    fun getFee(){
        viewModelScope.launch{
            val (data, netExecption) = async {  ApiService.apiService.getfee() }.awaitOrError()
            netExecption?.composeException { code, message ->
                Log.d("BBBBB","code==${code}  message==${message}")
            } ?: kotlin.run{
                fee.value = data?.data
            }
        }
    }
}

/**
 * 1.的生命周期处于 STARTED 或 RESUMED 状态，则 LiveData 会认为该观察者处于活跃状态。LiveData 只会将更新
 *   通知给活跃的观察者。为观察 LiveData 对象而注册的非活跃观察者不会收到更改通知。
 * 2.当 Activity 和 Fragment 的生命周期被销毁时，系统会立即退订它们
 */