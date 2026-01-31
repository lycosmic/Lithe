package io.github.lycosmic.data.repository

import android.content.Context
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.lycosmic.data.R
import io.github.lycosmic.domain.model.MyFontFamily
import io.github.lycosmic.domain.repository.FontFamilyRepository
import io.github.lycosmic.domain.repository.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject

class FontFamilyRepositoryImpl @Inject constructor(
    @param:ApplicationContext
    private val context: Context,
    private val jsonParser: JsonParser
) : FontFamilyRepository {

    // 字体缓存目录：/data/user/0/your.package/files/fonts/
    private val cacheDir = File(context.filesDir, "fonts").apply { if (!exists()) mkdirs() }

    // Google Fonts 提供者配置
    private val fontProvider = GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = R.array.com_google_android_gms_fonts_certs
    )

    /**
     * 获取所有可用的字体
     */
    override fun getAvailableFonts(): Flow<List<MyFontFamily>> = flow {
        // 1. 读取 Assets
        val jsonString = context.assets.open("fonts.json").bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<MyFontFamily>>() {}.type
        val rawFonts = jsonParser.fromJson(jsonString, type) ?: listOf(
            MyFontFamily.Default
        )
        // 2. TODO: 注入具体的 FontFamily 对象
        emit(rawFonts)
    }.flowOn(Dispatchers.IO)


    /**
     * 获取单个字体的 FontFamily
     */
    override fun getFontFamily(fontId: String): FontFamily {
        return when (fontId) {
            MyFontFamily.Default.id -> FontFamily.Default
            else -> {
                FontFamily(
                    Font(googleFont = GoogleFont(fontId), fontProvider = fontProvider)
                )
            }
        }
    }
}