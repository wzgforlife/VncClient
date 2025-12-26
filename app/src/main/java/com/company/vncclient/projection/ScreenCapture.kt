package com.company.vncclient.projection

import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.util.Log

class ScreenCapture(
    private val mediaProjection: MediaProjection,
    private val width: Int,
    private val height: Int,
    private val dpi: Int
) {

    private var imageReader: ImageReader? = null
    private var virtualDisplay: VirtualDisplay? = null

    fun start(onFrame: (ByteArray, Int, Int) -> Unit) {

        imageReader = ImageReader.newInstance(
            width,
            height,
            PixelFormat.RGBA_8888,
            2
        )

        virtualDisplay = mediaProjection.createVirtualDisplay(
            "VNC-Screen",
            width,
            height,
            dpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader!!.surface,
            null,
            null
        )

        imageReader!!.setOnImageAvailableListener({ reader ->
            val image = reader.acquireLatestImage() ?: return@setOnImageAvailableListener

            val planes = image.planes
            val buffer = planes[0].buffer
            val pixelStride = planes[0].pixelStride
            val rowStride = planes[0].rowStride
            val rowPadding = rowStride - pixelStride * width

            val realWidth = width + rowPadding / pixelStride

            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)

            image.close()

            // ⭐ 把每一帧交出去（后面用于编码 / WebSocket）
            onFrame(bytes, realWidth, height)

        }, null)

        Log.d("ScreenCapture", "Screen capture started")
    }

    fun stop() {
        virtualDisplay?.release()
        imageReader?.close()
        mediaProjection.stop()
    }
}
