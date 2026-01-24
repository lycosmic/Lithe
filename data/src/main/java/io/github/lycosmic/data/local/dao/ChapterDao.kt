package io.github.lycosmic.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.lycosmic.data.local.entity.ChapterEntity

@Dao
interface ChapterDao {

    /**
     * 通过书籍ID获取章节列表
     */
    @Query("SELECT * FROM ${ChapterEntity.TABLE_NAME} WHERE ${ChapterEntity.COL_BOOK_ID} = :bookId ORDER BY `${ChapterEntity.COL_INDEX}` ASC")
    suspend fun getChaptersByBookId(bookId: Long): List<ChapterEntity>

    /**
     * 批量插入章节，自动会作为一个事务
     * @return 插入的章节ID列表
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<ChapterEntity>): List<Long>

    /**
     * 根据书籍ID删除章节
     */
    @Query("DELETE FROM ${ChapterEntity.TABLE_NAME} WHERE ${ChapterEntity.COL_BOOK_ID} = :bookId")
    suspend fun deleteChaptersByBookId(bookId: Long): Int
}