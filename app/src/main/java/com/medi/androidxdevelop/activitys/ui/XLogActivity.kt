package com.medi.androidxdevelop.activitys.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.medi.androidxdevelop.R

/**
 * https://juejin.cn/post/6850418121279438855
 * 1.cacheDir 设置缓存目录
 *缓存目录，当 logDir 不可写时候会写进这个目录，可选项，不选用请给 ""， 如若要给，建议给应用的 /data/data/packname/files/log 目录。
 *会在目录下生成后缀为 .mmap3 的缓存文件，
 *2.logDir 设置写入的文件目录
 *真正的日志，后缀为 .xlog。日志写入目录，请给单独的目录，除了日志文件不要把其他文件放入该目录，不然可能会被日志的自动清理功能清理掉。
 *3.save private key
 * e0c23aee232bd8371a26da5148d78531dc7913ad875f7de4783ba9c0ce0a158c
 * appender_open's parameter:
 * 4d5529e4c5d2ae103cc713e73ab9ab7c91557e1c245d9407c40601f4ee7da22669dfd0b1186dc7005c836635ca2d2c9cd38b9e5bd0bc568410a5b98f614f97ed
 *
 */
class XLogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_x_log)
    }
}