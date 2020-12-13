package com.medi.androidxdevelop.base

import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


open class BaseActivity : AppCompatActivity(), CoroutineScope {
    private val job = Job()
    private var isUI = true
    override val coroutineContext: CoroutineContext
        get() = if (isUI) Dispatchers.Main + job else Dispatchers.IO + job

    fun launchUI(block: suspend CoroutineScope.() -> Unit) {
        isUI = true
        launch(coroutineContext, CoroutineStart.DEFAULT, block)
    }

}