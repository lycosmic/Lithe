package io.github.lycosmic.domain.use_case.reader

import io.github.lycosmic.domain.model.BookChapter
import io.github.lycosmic.domain.model.BookContentBlock
import io.github.lycosmic.domain.model.FileFormat
import io.github.lycosmic.domain.repository.BookContentParser
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