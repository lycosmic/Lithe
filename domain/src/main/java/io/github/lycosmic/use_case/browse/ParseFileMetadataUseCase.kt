package io.github.lycosmic.use_case.browse


import io.github.lycosmic.model.FileItem
import io.github.lycosmic.model.ParsedBook
import io.github.lycosmic.repository.BookMetadataParser
import io.github.lycosmic.util.DomainLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * 解析文件元数据
 */
class ParseFileMetadataUseCase @Inject constructor(
    private val logger: DomainLogger,
    private val bookMetadataParser: BookMetadataParser
) {
    suspend operator fun invoke(
        selectedFiles: List<FileItem>
    ): Result<List<ParsedBook>> =
        withContext(Dispatchers.IO) {
            logger.i {
                "开始解析书籍元数据"
            }

            val parsedBooks = try {
                selectedFiles.map { file ->
                    async {
                        logger.d {
                            "当前正在解析文件: ${file.name}"
                        }

                        val metadataResult = bookMetadataParser.parse(file.uriString, file.format)

                        metadataResult.onSuccess { bookMetadata ->
                            logger.d {
                                "解析书籍元数据成功，详情如下：${bookMetadata}"
                            }

                            return@async ParsedBook(
                                file = file,
                                metadata = bookMetadata
                            )
                        }.onFailure { e ->
                            logger.e(throwable = e) {
                                "解析书籍元数据失败，详情如下：${e.message}"
                            }
                            return@async null
                        }

                        return@async null
                    }
                }.awaitAll()
            } catch (e: Exception) {
                return@withContext Result.failure(e)
            }

            logger.d {
                "解析文件元数据完成，共有 ${parsedBooks.size} 本书"
            }

            Result.success(parsedBooks.filterNotNull())
        }
}