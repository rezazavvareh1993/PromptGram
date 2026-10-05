package com.example.data.repository

import com.example.data.local.DefaultPrompts
import com.example.data.local.PromptDao
import com.example.data.local.PromptEntity
import com.example.data.model.PromptItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PromptRepository(private val dao: PromptDao) {

    val allPrompts: Flow<List<PromptItem>> = dao.getAllPrompts().map { list ->
        list.map { it.toDomain() }
    }

    val favoritePrompts: Flow<List<PromptItem>> = dao.getFavoritePrompts().map { list ->
        list.map { it.toDomain() }
    }

    val favoriteCount: Flow<Int> = dao.getFavoriteCount()

    val customPrompts: Flow<List<PromptItem>> = dao.getCustomPrompts().map { list ->
        list.map { it.toDomain() }
    }

    val suggestedPrompts: Flow<List<PromptItem>> = dao.getSuggestedPrompts().map { list ->
        list.map { it.toDomain() }
    }

    fun getPromptsByCategory(category: String): Flow<List<PromptItem>> {
        return dao.getPromptsByCategory(category).map { list ->
            list.map { it.toDomain() }
        }
    }

    fun searchPrompts(query: String): Flow<List<PromptItem>> {
        return dao.searchPrompts(query).map { list ->
            list.map { it.toDomain() }
        }
    }

    fun searchFavoritePrompts(query: String): Flow<List<PromptItem>> {
        return dao.searchFavoritePrompts(query).map { list ->
            list.map { it.toDomain() }
        }
    }

    suspend fun ensureDefaultData() = withContext(Dispatchers.IO) {
        if (dao.getCount() == 0) {
            dao.insertAll(DefaultPrompts.getInitialPrompts())
        }
    }

    suspend fun refreshTrendingSuggestions() = withContext(Dispatchers.IO) {
        val fresh = DefaultPrompts.getFreshTrendingSuggestions()
        dao.insertAll(fresh)
    }

    suspend fun toggleFavorite(id: Long, currentFavorite: Boolean) = withContext(Dispatchers.IO) {
        dao.updateFavorite(id, !currentFavorite)
    }

    suspend fun clearAllFavorites() = withContext(Dispatchers.IO) {
        dao.clearAllFavorites()
    }

    suspend fun incrementCopy(id: Long) = withContext(Dispatchers.IO) {
        dao.incrementCopyCount(id)
    }

    suspend fun insertPrompt(item: PromptItem): Long = withContext(Dispatchers.IO) {
        dao.insertPrompt(PromptEntity.fromDomain(item))
    }

    suspend fun deletePrompt(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteById(id)
    }
}
