package io.github.lycosmic.use_case.reader

import io.github.lycosmic.model.BookChapter
import io.github.lycosmic.model.BookContentBlock
import io.github.lycosmic.model.FileFormat
import io.github.lycosmic.repository.BookContentParser
import javax.inject.Inject


class GetChapterContentUseCase @Inject constructor(
    private val bookContentParser: BookContentParser
) {
    suspend operator fun invoke(
        uriString: String,
        format: FileFormat,
        chapter: BookChapter
    ): Result<List<BookContentBlock>> {
        return bookContentParser.loadChapterContent(uriString, format, chapter)
    }
}