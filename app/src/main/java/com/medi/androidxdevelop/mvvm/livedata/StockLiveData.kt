package com.medi.androidxdevelop.mvvm.livedata

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import java.math.BigDecimal

class StockLiveData(symbol: String) : LiveData<BigDecimal>() {
   // private val stockManager: StockManager = StockManager(symbol)

    private val listener = { price: BigDecimal ->
        value = price
    }

    override fun onActive() {
        //stockManager.requestPriceUpdates(listener)
    }

    override fun onInactive() {
        //stockManager.removeUpdates(listener)
    }

    //LiveData 对象具有生命周期感知能力，这一事实意味着您可以在多个 Activity、Fragment 和 Service 之间共
    // 享这些对象。为使示例保持简单，您可以将 LiveData 类实现为单一实例
    companion object {
        private lateinit var sInstance: StockLiveData

        @MainThread
        fun get(symbol: String): StockLiveData {
            sInstance = if (::sInstance.isInitialized) sInstance else StockLiveData(symbol)
            return sInstance
        }
    }
}