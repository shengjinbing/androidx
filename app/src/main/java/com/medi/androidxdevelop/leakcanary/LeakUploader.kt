package com.medi.androidxdevelop.leakcanary

import leakcanary.DefaultOnHeapAnalyzedListener
import leakcanary.OnHeapAnalyzedListener
import shark.HeapAnalysis

/**
 * Created by lixiang on 2020/11/27
 * Describe:
 */
class LeakUploader : OnHeapAnalyzedListener {

    private val defaultListener = DefaultOnHeapAnalyzedListener.create()

    override fun onHeapAnalyzed(heapAnalysis: HeapAnalysis) {
        TODO("Upload heap analysis to server")
        // Delegate to default behavior (notification and saving result)
        defaultListener.onHeapAnalyzed(heapAnalysis)
    }
}
