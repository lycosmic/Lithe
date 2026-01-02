package io.github.lycosmic.lithe.util

import java.io.BufferedInputStream
import java.io.InputStream
import java.nio.charset.Charset

object CharsetUtils {

    const val GBK = "GBK"


    /**
     * 猜测文件编码
     * 策略：读取前 4KB，尝试用 UTF-8 解码，如果没有乱码特征则认为是 UTF-8，否则认为是 GBK
     */
    fun detectCharset(inputStream: InputStream): Charset {
        val bufferedInputStream = BufferedInputStream(inputStream)
        bufferedInputStream.mark(4096) // 标记位置，以便读取后重置
        val buffer = ByteArray(4096)
        val read = bufferedInputStream.read(buffer)
        bufferedInputStream.reset() // 重置流，以便后续使用

        if (read == -1) return Charsets.UTF_8

        // 简单的 UTF-8 验证逻辑
        if (isValidUtf8(buffer, read)) {
            return Charsets.UTF_8
        }

        // 默认回退到 GBK (支持中文简体/繁体)
        return Charset.forName(GBK)
    }

    private fun isValidUtf8(input: ByteArray, length: Int): Boolean {
        var i = 0
        while (i < length) {
            val byte = input[i].toInt()
            if ((byte and 0x80) == 0) { // 0xxxxxxx (ASCII)
                i++
                continue
            }
            val expectedLen: Int = if ((byte and 0xE0) == 0xC0) 1      // 110xxxxx
            else if ((byte and 0xF0) == 0xE0) 2 // 1110xxxx
            else if ((byte and 0xF8) == 0xF0) 3 // 11110xxx
            else return false

            i++
            (0 until expectedLen).forEach { _ ->
                if (i >= length) return true // 数据截断，暂且认为是真
                if ((input[i].toInt() and 0xC0) != 0x80) return false // 必须是 10xxxxxx
                i++
            }
        }
        return true
    }
}