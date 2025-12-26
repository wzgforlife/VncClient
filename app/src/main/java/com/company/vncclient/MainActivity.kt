package com.company.vncclient

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.company.vncclient.projection.ScreenCaptureManager
import com.company.vncclient.ui.screen.HomeScreen
import com.company.vncclient.ui.theme.VncClientTheme

class MainActivity : ComponentActivity() {

    private lateinit var captureManager: ScreenCaptureManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        captureManager = ScreenCaptureManager(this)

        setContent {
            VncClientTheme {
                HomeScreen(
                    onStartClick = {
                        // 👉 只负责“请求授权”
                        captureManager.requestPermission()
                    }
                )
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (
            requestCode == ScreenCaptureManager.REQUEST_CODE_CAPTURE &&
            resultCode == Activity.RESULT_OK &&
            data != null
        ) {
            // 👉 真正开始屏幕采集
            captureManager.startCapture(resultCode, data)
        }
    }
}
