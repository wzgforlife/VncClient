package com.company.vncclient.projection

import android.graphics.Bitmap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 全局屏幕帧缓存
 * Service 写，UI 读
 */
object ScreenFrameBus {

    private val _frameFlow = MutableStateFlow<Bitmap?>(null)

    val frameFlow = _frameFlow.asStateFlow()

    fun updateFrame(bitmap: Bitmap) {
        _frameFlow.value = bitmap
    }
}
