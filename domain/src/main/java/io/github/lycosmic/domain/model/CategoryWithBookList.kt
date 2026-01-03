package io.github.lycosmic.domain.model


data class CategoryWithBookList(
    val category: Category,
    val bookList: List<Book>
)