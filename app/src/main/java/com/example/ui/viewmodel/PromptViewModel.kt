package com.example.ui.viewmodel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.PromptItem
import com.example.data.repository.PromptRepository
import com.example.util.WallpaperRenderer
import com.example.util.WallpaperSaver
import com.example.util.WallpaperTarget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PromptViewModel(
    private val repository: PromptRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.ensureDefaultData()
        }
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedPromptForDetail = MutableStateFlow<PromptItem?>(null)
    val selectedPromptForDetail: StateFlow<PromptItem?> = _selectedPromptForDetail.asStateFlow()

    private val _selectedPromptForWallpaper = MutableStateFlow<PromptItem?>(null)
    val selectedPromptForWallpaper: StateFlow<PromptItem?> = _selectedPromptForWallpaper.asStateFlow()

    private val _wallpaperFilter = MutableStateFlow("Normal")
    val wallpaperFilter: StateFlow<String> = _wallpaperFilter.asStateFlow()

    private val _showLockscreenOverlay = MutableStateFlow(true)
    val showLockscreenOverlay: StateFlow<Boolean> = _showLockscreenOverlay.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    val allPrompts = repository.allPrompts.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val favoritePrompts = repository.favoritePrompts.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val customPrompts = repository.customPrompts.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val filteredPrompts = combine(
        allPrompts,
        _searchQuery,
        _selectedCategory
    ) { prompts, query, category ->
        prompts.filter { item ->
            val matchesCategory = (category == "All") || (item.category == category)
            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.promptText.contains(query, ignoreCase = true) ||
                    item.styleTags.any { it.contains(query, ignoreCase = true) }
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun openDetail(prompt: PromptItem) {
        _selectedPromptForDetail.value = prompt
    }

    fun closeDetail() {
        _selectedPromptForDetail.value = null
    }

    fun openWallpaperScreen(prompt: PromptItem) {
        _selectedPromptForWallpaper.value = prompt
    }

    fun closeWallpaperScreen() {
        _selectedPromptForWallpaper.value = null
    }

    fun setWallpaperFilter(filter: String) {
        _wallpaperFilter.value = filter
    }

    fun toggleLockscreenOverlay() {
        _showLockscreenOverlay.value = !_showLockscreenOverlay.value
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }

    fun toggleFavorite(prompt: PromptItem) {
        viewModelScope.launch {
            repository.toggleFavorite(prompt.id, prompt.isFavorite)
            val action = if (prompt.isFavorite) "Removed from favorites" else "Added to favorites ❤️"
            _snackbarMessage.value = action
        }
    }

    fun copyPromptToClipboard(context: Context, prompt: PromptItem) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("AI Prompt: ${prompt.title}", prompt.promptText)
        clipboard.setPrimaryClip(clip)

        viewModelScope.launch {
            repository.incrementCopy(prompt.id)
            _snackbarMessage.value = "Prompt copied to clipboard! Ready to paste 📋"
        }
    }

    fun saveWallpaperToGallery(context: Context, prompt: PromptItem) {
        viewModelScope.launch {
            _isSaving.value = true
            try {
                val bitmap = WallpaperRenderer.renderPromptWallpaper(
                    prompt = prompt,
                    width = 1080,
                    height = 1920,
                    filterMode = _wallpaperFilter.value
                )
                val result = WallpaperSaver.saveToGallery(context, bitmap, prompt.title)
                if (result.isSuccess) {
                    _snackbarMessage.value = "Saved to Gallery! Check Pictures/PromptGram 🖼️"
                } else {
                    _snackbarMessage.value = "Could not save photo: ${result.exceptionOrNull()?.localizedMessage}"
                }
            } catch (e: Exception) {
                _snackbarMessage.value = "Error saving: ${e.localizedMessage}"
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun setAsDeviceWallpaper(context: Context, prompt: PromptItem, target: WallpaperTarget) {
        viewModelScope.launch {
            _isSaving.value = true
            try {
                val bitmap = WallpaperRenderer.renderPromptWallpaper(
                    prompt = prompt,
                    width = 1080,
                    height = 1920,
                    filterMode = _wallpaperFilter.value
                )
                val result = WallpaperSaver.setAsDeviceWallpaper(context, bitmap, target)
                if (result.isSuccess) {
                    val label = when (target) {
                        WallpaperTarget.HOME -> "Home Screen"
                        WallpaperTarget.LOCK -> "Lock Screen"
                        WallpaperTarget.BOTH -> "Home & Lock Screen"
                    }
                    _snackbarMessage.value = "Wallpaper set successfully for $label! ✨"
                } else {
                    _snackbarMessage.value = "Could not set wallpaper: ${result.exceptionOrNull()?.localizedMessage}"
                }
            } catch (e: Exception) {
                _snackbarMessage.value = "Failed: ${e.localizedMessage}"
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun sharePrompt(context: Context, prompt: PromptItem) {
        viewModelScope.launch {
            try {
                val bitmap = WallpaperRenderer.renderPromptWallpaper(
                    prompt = prompt,
                    width = 1080,
                    height = 1920,
                    filterMode = _wallpaperFilter.value
                )
                WallpaperSaver.shareBitmapWithPrompt(context, bitmap, prompt.title, prompt.promptText)
            } catch (e: Exception) {
                _snackbarMessage.value = "Could not share: ${e.localizedMessage}"
            }
        }
    }

    fun addCustomPrompt(
        title: String,
        category: String,
        promptText: String,
        negativePrompt: String,
        aspectRatio: String,
        modelTips: String,
        styleTags: List<String>,
        photoThemeType: String
    ) {
        viewModelScope.launch {
            val item = PromptItem(
                title = title,
                category = category,
                platformTrend = "My Custom Creation",
                promptText = promptText,
                negativePrompt = negativePrompt.ifBlank { "blurry, low quality, deformed, extra fingers, cartoonish" },
                aspectRatio = aspectRatio,
                modelTips = modelTips.ifBlank { "Custom tailored prompt" },
                styleTags = styleTags,
                photoThemeType = photoThemeType,
                isFavorite = true,
                isCustom = true,
                copyCount = 0,
                createdAt = System.currentTimeMillis()
            )
            repository.insertPrompt(item)
            _snackbarMessage.value = "Custom prompt saved to your collection! 🎉"
        }
    }

    fun deleteCustomPrompt(prompt: PromptItem) {
        viewModelScope.launch {
            repository.deletePrompt(prompt.id)
            if (_selectedPromptForDetail.value?.id == prompt.id) {
                _selectedPromptForDetail.value = null
            }
            if (_selectedPromptForWallpaper.value?.id == prompt.id) {
                _selectedPromptForWallpaper.value = null
            }
            _snackbarMessage.value = "Prompt deleted"
        }
    }
}

class PromptViewModelFactory(private val repository: PromptRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PromptViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PromptViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
