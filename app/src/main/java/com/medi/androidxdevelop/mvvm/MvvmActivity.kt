package com.medi.androidxdevelop.mvvm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModelProviders
import com.medi.androidxdevelop.R
import com.medi.androidxdevelop.base.BaseActivity
import com.medi.androidxdevelop.mvvm.life.TestLife
import kotlinx.android.synthetic.main.activity_mvvm.*

class MvvmActivity : BaseActivity() {

    private lateinit var viewMolde: TestViewMolde;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mvvm)
        viewMolde  = ViewModelProviders.of(this).get(TestViewMolde::class.java)
        initData()
        initObserve()
        initListener()
        lifecycle.addObserver(TestLife(lifecycle))
    }

    private fun initData() {
    }

    private fun initObserve() {
        val fee = viewMolde.fee
        //转换 LiveData
        Transformations.map(fee){
            it[0].dictName = "值被转变了"
        }
        fee.observe(this, Observer {
            tv_content.text = it.get(0).dictName
        })
    }

    private fun testSwitchMap(){
       /* private fun getUser(id: String): LiveData<User> {
            ...
        }
        val userId: LiveData<String> = ...
        val user = Transformations.switchMap(userId) { id -> getUser(id) }*/
    }

    private fun initListener() {
        btn_requset.setOnClickListener {
            Log.d("BBBBB","开始请求")
            viewMolde.getFee()
        }
        tv_LifecycleOwner.setOnClickListener {
            startActivity(Intent(this,LifecycleActivity::class.java))
        }
    }


}
