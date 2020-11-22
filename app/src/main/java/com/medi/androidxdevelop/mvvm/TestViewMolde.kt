package com.medi.androidxdevelop.mvvm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medi.androidxdevelop.mvvm.Entity.FeeEntity
import com.medi.androidxdevelop.network.ApiService
import com.medi.comm.network.result.awaitOrError
import kotlinx.coroutines.launch

/**
 * Created by lixiang on 2020/4/1
 * Describe:
 */
class TestViewMolde : ViewModel() {
    var fee = MutableLiveData<MutableList<FeeEntity>>()
    fun getFee(){
        viewModelScope.launch{
            val (data, netExecption) = ApiService.apiService.getfee().awaitOrError()
            netExecption?.composeException { code, message ->
                Log.d("BBBBB","code==${code}  message==${message}")
            } ?: kotlin.run{
                fee.value = data?.data
            }
        }
    }
}