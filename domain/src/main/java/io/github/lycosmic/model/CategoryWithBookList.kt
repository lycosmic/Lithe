package io.github.lycosmic.model


data class CategoryWithBookList(
    val category: Category,
    val bookList: List<Book>
)