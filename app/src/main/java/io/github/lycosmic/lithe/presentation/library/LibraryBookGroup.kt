package io.github.lycosmic.lithe.presentation.library

import io.github.lycosmic.lithe.data.local.entity.CategoryEntity
import io.github.lycosmic.lithe.data.model.Book

data class LibraryBookGroup(
    val category: CategoryEntity, // 分类
    val books: List<Book>   // 该分类下的书籍列表
)