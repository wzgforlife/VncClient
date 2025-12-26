package com.company.vncclient.network

import android.util.Log
import org.java_websocket.server.WebSocketServer
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import java.net.InetSocketAddress

class VncWebSocketServer(
    port: Int = 9002
) : WebSocketServer(InetSocketAddress(port)) {

    private val clients = mutableSetOf<WebSocket>()

    override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
        clients.add(conn)
        Log.d("VNC-WS", "Client connected: ${conn.remoteSocketAddress}")
    }

    override fun onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean) {
        clients.remove(conn)
        Log.d("VNC-WS", "Client disconnected")
    }

    override fun onMessage(conn: WebSocket, message: String) {
        // 后面用于接收点击 / 滑动指令
    }

    override fun onError(conn: WebSocket?, ex: Exception) {
        Log.e("VNC-WS", "Error", ex)
    }

    override fun onStart() {
        Log.d("VNC-WS", "WebSocket started")
    }

    fun broadcastFrame(jpegBytes: ByteArray) {
        for (client in clients) {
            client.send(jpegBytes)
        }
    }
}
