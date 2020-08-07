package com.medi.androidxdevelop.activitys

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import com.medi.androidxdevelop.R
import kotlinx.android.synthetic.main.activity_async_task.*

class AsyncTaskActivity : AppCompatActivity() {
    lateinit var textTip: TextView
    lateinit var progressBar:ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_async_task)
        textTip = text
        progressBar = progress_bar
        val myTask = MyTask()
        button.setOnClickListener {
            myTask.execute()
        }
        cancel.setOnClickListener {
            myTask.cancel(true)
        }
    }

    inner class MyTask : AsyncTask<String, Int, String>() {
        /**
         * 执行 线程任务前的操作
         */
        override fun onPreExecute() {
            textTip.setText("加载中")
        }


        /**
         * 接收输入参数、执行任务中的耗时操作、返回 线程任务执行的结果
         * @param params Array<out String?>
         * @return String
         */
        override fun doInBackground(vararg params: String?): String {
            var count = 0
            while (count < 99){
                count++
                publishProgress(count)
                Thread.sleep(50)
            }
            return ""
        }

        /**
         * 在主线程 显示线程任务执行的进度
         * @param values Array<out Integer?>
         */
        override fun onProgressUpdate(vararg progresses: Int?) {
            progressBar.setProgress(progresses[0]!!)
        }


        /**
         * 接收线程任务执行结果、将执行结果显示到UI组件
         * @param result String
         */
        override fun onPostExecute(result: String?) {
            text.text = "加载完毕"
        }

        /**
         * 将异步任务设置为：取消状态
         */
        override fun onCancelled() {
            textTip.text= "取消"
            progressBar.setProgress(0);
        }

    }
}

