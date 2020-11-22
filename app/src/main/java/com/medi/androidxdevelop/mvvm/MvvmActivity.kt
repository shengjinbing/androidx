package com.medi.androidxdevelop.mvvm

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.medi.androidxdevelop.R
import com.medi.androidxdevelop.network.ApiService
import com.medi.comm.network.result.awaitOrError
import kotlinx.android.synthetic.main.activity_mvvm.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MvvmActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var viewMolde:TestViewMolde

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mvvm)
        viewMolde = ViewModelProviders.of(this).get(TestViewMolde::class.java)
        initData()
        initObserve()
        initListener()
    }

    private fun initData() {
        launchUI{
            try {
                val (data, netExecption) =
                    ApiService.apiService.getfee().awaitOrError()
                netExecption?.composeException { code, message ->
                    Log.d("BBBBB","code==${code}  message==${message}")
                } ?: kotlin.run{
                }
            }catch (e:Exception){
                Log.d("BBBBB",e.message)
            }

        }
    }

    private fun initObserve() {
        viewMolde.fee.observe(this, Observer {
            tv_content.text = it.get(0).dictName
        })

    }

    private fun initListener() {
        btn_requset.setOnClickListener {
            viewMolde.getFee()
        }
    }

    fun launchUI(block: suspend CoroutineScope.() -> Unit) {
        isUI = true
        launch(coroutineContext, CoroutineStart.DEFAULT, block)
    }


    private val job = Job()
    private var isUI = true
    override val coroutineContext: CoroutineContext
        get() = if (isUI) Dispatchers.Main + job else Dispatchers.IO + job
}
