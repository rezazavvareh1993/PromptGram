package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PromptDao {
    @Query("SELECT * FROM prompts ORDER BY isFavorite DESC, createdAt DESC")
    fun getAllPrompts(): Flow<List<PromptEntity>>

    @Query("SELECT * FROM prompts WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoritePrompts(): Flow<List<PromptEntity>>

    @Query("SELECT COUNT(*) FROM prompts WHERE isFavorite = 1")
    fun getFavoriteCount(): Flow<Int>

    @Query("SELECT * FROM prompts WHERE isFavorite = 1 AND (title LIKE '%' || :query || '%' OR titleFa LIKE '%' || :query || '%' OR promptText LIKE '%' || :query || '%') ORDER BY createdAt DESC")
    fun searchFavoritePrompts(query: String): Flow<List<PromptEntity>>

    @Query("SELECT * FROM prompts WHERE isCustom = 1 ORDER BY createdAt DESC")
    fun getCustomPrompts(): Flow<List<PromptEntity>>

    @Query("SELECT * FROM prompts WHERE isSuggestedTrend = 1 ORDER BY createdAt DESC")
    fun getSuggestedPrompts(): Flow<List<PromptEntity>>

    @Query("SELECT * FROM prompts WHERE category = :category ORDER BY createdAt DESC")
    fun getPromptsByCategory(category: String): Flow<List<PromptEntity>>

    @Query("SELECT * FROM prompts WHERE title LIKE '%' || :query || '%' OR titleFa LIKE '%' || :query || '%' OR promptText LIKE '%' || :query || '%' OR styleTags LIKE '%' || :query || '%'")
    fun searchPrompts(query: String): Flow<List<PromptEntity>>

    @Query("SELECT COUNT(*) FROM prompts")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrompt(prompt: PromptEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(prompts: List<PromptEntity>)

    @Update
    suspend fun updatePrompt(prompt: PromptEntity)

    @Delete
    suspend fun deletePrompt(prompt: PromptEntity)

    @Query("DELETE FROM prompts WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE prompts SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)

    @Query("UPDATE prompts SET isFavorite = 0")
    suspend fun clearAllFavorites()

    @Query("UPDATE prompts SET copyCount = copyCount + 1 WHERE id = :id")
    suspend fun incrementCopyCount(id: Long)
}
