package com.medi.androidxdevelop.leakcanary

import android.util.Log
import leakcanary.DefaultOnHeapAnalyzedListener
import leakcanary.OnHeapAnalyzedListener
import shark.HeapAnalysis
import shark.HeapAnalysisFailure
import shark.HeapAnalysisSuccess

/**
 * Created by lixiang on 2020/11/27
 * Describe:
 */
@Suppress("UNREACHABLE_CODE")
class LeakUploader : OnHeapAnalyzedListener {
    private val TAG: String = "LeakUploader"

    private val defaultListener = DefaultOnHeapAnalyzedListener.create()

    override fun onHeapAnalyzed(heapAnalysis: HeapAnalysis) {
        Log.d(TAG,heapAnalysis.toString())
        defaultListener.onHeapAnalyzed(heapAnalysis)
        when (heapAnalysis) {
            is HeapAnalysisSuccess -> {
                val allLeakTraces = heapAnalysis
                    .allLeaks
                    .toList()
            }

            is HeapAnalysisFailure -> {
                Log.d(TAG,heapAnalysis.exception.cause.toString())
            }
        }
    }
}
