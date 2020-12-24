package com.medi.androidxdevelop.mvvm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.*
import com.medi.androidxdevelop.R
import com.medi.androidxdevelop.activitys.DataBindingActivity
import com.medi.androidxdevelop.base.BaseActivity
import com.medi.androidxdevelop.mvvm.life.TestLife
import kotlinx.android.synthetic.main.activity_mvvm.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MvvmActivity : BaseActivity() {

    private val viewModel: TestViewModel by lazy {
        TestViewModel.instanceOf(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mvvm)
        initData()
        initObserve()
        initListener()
        lifecycle.addObserver(TestLife(lifecycle))
        val model: TestViewModel = ViewModelProvider(this).get(TestViewModel::class.java)
        if (model == viewModel){
            Toast.makeText(applicationContext,"相等",Toast.LENGTH_SHORT).show()
        }
        initLifecycleScope()

    }

    /**
     * 为每个 Lifecycle 对象定义了 LifecycleScope
     * 1.即使 CoroutineScope 提供了适当的方法来自动取消长时间运行的操作，在某些情况下，您可能需要暂停执行代码块（
     * 除非 Lifecycle 处于特定状态）。例如，要运行 FragmentTransaction，您必须等到 Lifecycle 至少为 STARTED。
     * 对于这些情况，Lifecycle 提供了其他方法：lifecycle.whenCreated、lifecycle.whenStarted 和 lifecycle.whenResumed。
     * 如果 Lifecycle 未至少处于所需的最低状态，则会暂停在这些块内运行的任何协程。
     */
    private fun initLifecycleScope() {
        lifecycleScope.launch {
            //异步创建预计算文本：
            whenStarted {

            }
        }

        lifecycleScope.launchWhenStarted {
            try {
                // Call some suspend functions.
            } finally {
                // This line might execute after Lifecycle is DESTROYED.
                if (lifecycle.currentState >= Lifecycle.State.STARTED) {
                    // Here, since we've checked, it is safe to run any
                    // Fragment transactions.
                }
            }
        }

    }

    private fun initData() {
    }

    private fun initObserve() {
        val fee = viewModel.fee
        //转换 LiveData
        Transformations.map(fee) {
            it[0].dictName = "值被转变了"
        }
        fee.observe(this, Observer {
            tv_content.text = it.get(0).dictName
        })
    }

    private fun testSwitchMap() {
        /* private fun getUser(id: String): LiveData<User> {
             ...
         }
         val userId: LiveData<String> = ...
         val user = Transformations.switchMap(userId) { id -> getUser(id) }*/
    }

    private fun initListener() {
        btn_requset.setOnClickListener {
            Log.d("BBBBB", "开始请求")
            viewModel.getFee()
        }
        tv_LifecycleOwner.setOnClickListener {
            startActivity(Intent(this, LifecycleActivity::class.java))
        }
        tv_work.setOnClickListener {
            startActivity(Intent(this, WorkActivity::class.java))
        }

        tv_databinding.setOnClickListener {
            startActivity(Intent(this, DataBindingActivity::class.java))
        }

    }


}
