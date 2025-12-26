package com.company.vncclient.projection

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.util.DisplayMetrics

class ScreenCaptureManager(
    private val activity: Activity
) {

    companion object {
        const val REQUEST_CODE_CAPTURE = 1000
    }

    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

    private val projectionManager =
        activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE)
                as MediaProjectionManager

    /** 第一步：请求系统授权（会弹系统对话框） */
    fun requestPermission() {
        activity.startActivityForResult(
            projectionManager.createScreenCaptureIntent(),
            REQUEST_CODE_CAPTURE
        )
    }

    /** 第二步：在 onActivityResult 中调用 */
    fun startCapture(resultCode: Int, data: Intent) {
        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getRealMetrics(metrics)

        imageReader = ImageReader.newInstance(
            metrics.widthPixels,
            metrics.heightPixels,
            android.graphics.PixelFormat.RGBA_8888,
            2
        )

        mediaProjection = projectionManager.getMediaProjection(resultCode, data)

        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "VNC-Capture",
            metrics.widthPixels,
            metrics.heightPixels,
            metrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader!!.surface,
            null,
            null
        )
    }

    fun getImageReader(): ImageReader? = imageReader

    fun stop() {
        virtualDisplay?.release()
        mediaProjection?.stop()
    }
}
