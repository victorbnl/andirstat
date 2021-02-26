package com.victorb.andirstat

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun runOnMainThread(callback: () -> Unit) {
    Handler(Looper.getMainLooper()).post {
        callback.invoke()
    }
}

fun runInCoroutine(callback: () -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        callback.invoke()
    }
}