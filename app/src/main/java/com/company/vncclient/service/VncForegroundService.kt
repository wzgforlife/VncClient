package com.company.vncclient.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.projection.MediaProjectionManager
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import com.company.vncclient.R
import com.company.vncclient.network.FrameEncoder
import com.company.vncclient.network.VncWebSocketServer
import com.company.vncclient.projection.ScreenCapture

class VncForegroundService : Service() {

    private lateinit var webSocketServer: VncWebSocketServer

    // ✅ 正确：类级别方法
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "vnc_channel",
                "VNC Screen Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "VNC 屏幕捕获服务"
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        // ✅ 直接调用
        createNotificationChannel()

        val notification = NotificationCompat.Builder(this, "vnc_channel")
            .setContentTitle("VNC 正在运行")
            .setContentText("正在实时捕获屏幕")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()

        startForeground(1, notification)



        // ✅ 3️⃣ 取 MediaProjection 授权参数
        val resultCode = intent?.getIntExtra("resultCode", -1)
            ?: return START_NOT_STICKY
        val resultData = intent.getParcelableExtra<Intent>("resultData")
            ?: return START_NOT_STICKY

        val projectionManager =
            getSystemService(MediaProjectionManager::class.java)

        val mediaProjection =
            projectionManager.getMediaProjection(resultCode, resultData)
                ?: return START_NOT_STICKY

        // ✅ 4️⃣ 屏幕参数
        val metrics = DisplayMetrics()
        (getSystemService(WINDOW_SERVICE) as WindowManager)
            .defaultDisplay.getRealMetrics(metrics)

        // ✅ 5️⃣ WebSocket 服务（只启动一次）
        webSocketServer = VncWebSocketServer(9002)
        webSocketServer.start()

        // ✅ 6️⃣ 启动屏幕捕获
        val screenCapture = ScreenCapture(
            mediaProjection,
            metrics.widthPixels,
            metrics.heightPixels,
            metrics.densityDpi
        )

        screenCapture.start { frameBytes, _, _ ->

            val bitmap = BitmapFactory.decodeByteArray(
                frameBytes, 0, frameBytes.size
            ) ?: return@start

            val jpeg = FrameEncoder.encodeToJpeg(bitmap)

            webSocketServer.broadcastFrame(jpeg)
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
