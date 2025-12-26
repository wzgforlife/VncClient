package com.company.vncclient.network

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

object FrameEncoder {

    fun encodeToJpeg(
        bitmap: Bitmap,
        quality: Int = 70
    ): ByteArray {
        val output = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, output)
        return output.toByteArray()
    }
}
