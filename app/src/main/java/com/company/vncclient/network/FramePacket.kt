package com.company.vncclient.network

data class FramePacket(
    val width: Int,
    val height: Int,
    val timestamp: Long,
    val jpegBytes: ByteArray
)
