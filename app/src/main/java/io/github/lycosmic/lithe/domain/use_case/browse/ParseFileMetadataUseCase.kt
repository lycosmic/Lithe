package io.github.lycosmic.lithe.domain.use_case.browse

import io.github.lycosmic.lithe.data.parser.metadata.BookMetadataParserFactory
import io.github.lycosmic.lithe.domain.model.FileItem
import io.github.lycosmic.lithe.extension.logD
import io.github.lycosmic.lithe.extension.logI
import io.github.lycosmic.lithe.extension.logW
import io.github.lycosmic.lithe.presentation.browse.model.ParsedBook
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * 解析文件元数据
 */
class ParseFileMetadataUseCase @Inject constructor(
    private val parserFactory: BookMetadataParserFactory,
) {
    suspend operator fun invoke(
        selectedFiles: List<FileItem>
    ): List<ParsedBook> =
        withContext(Dispatchers.IO) {
            logI {
                "开始解析文件元数据"
            }

            val parsedBooks = selectedFiles.map { file ->
                async {
                    logD {
                        "当前正在解析文件: ${file.name}"
                    }
                    val parser = parserFactory.getMetadataParser(file.type)
                    if (parser == null) {
                        logW {
                            "找不到对应的解析器，忽略该文件: ${file.name}"
                        }
                        return@async null
                    }

                    val fileMetadata = parser.parse(file.uri)

                    logD {
                        "解析文件元数据成功，详情如下：${fileMetadata}"
                    }

                    ParsedBook(
                        file = file,
                        metadata = fileMetadata
                    )
                }
            }.awaitAll()

            logD {
                "解析文件元数据完成，共有 ${parsedBooks.size} 本书"
            }
            parsedBooks.filterNotNull()
        }
}