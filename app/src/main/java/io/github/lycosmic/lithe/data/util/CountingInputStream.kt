package io.github.lycosmic.lithe.data.util

import java.io.FilterInputStream
import java.io.InputStream

/**
 * 统计输入流字节数
 */
class CountingInputStream(
    private val inputStream: InputStream
) : FilterInputStream(inputStream) {
    // 已读取的字节数
    private var bytesRead = 0L

    fun getCount(): Long = bytesRead

    override fun read(): Int {
        val b = super.read()
        if (b != -1) bytesRead++
        return b
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        val n = super.read(b, off, len)
        if (n != -1) bytesRead += n
        return n
    }

}