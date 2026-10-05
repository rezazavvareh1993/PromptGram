package com.example.ui.mvi

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.PromptItem
import com.example.data.repository.PromptRepository
import com.example.util.AiLauncher
import com.example.util.LocaleStrings
import com.example.util.WallpaperRenderer
import com.example.util.WallpaperSaver
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PromptMviViewModel(
    private val repository: PromptRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PromptUiState())
    val uiState: StateFlow<PromptUiState> = _uiState.asStateFlow()

    private val _sideEffects = Channel<PromptSideEffect>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()

    init {
        viewModelScope.launch {
            repository.ensureDefaultData()
        }

        // Combine repository flows with internal UI state (search, category, language, etc.)
        viewModelScope.launch {
            combine(
                repository.allPrompts,
                repository.favoritePrompts,
                repository.customPrompts,
                repository.suggestedPrompts
            ) { all, favs, custom, suggested ->
                Quadruple(all, favs, custom, suggested)
            }.collect { (all, favs, custom, suggested) ->
                _uiState.update { current ->
                    current.copy(
                        rawPrompts = all,
                        prompts = filterPrompts(all, current.searchQuery, current.selectedCategoryTab, current.language),
                        favoritePrompts = favs,
                        customPrompts = custom,
                        suggestedPrompts = suggested.ifEmpty { all.filter { it.isSuggestedTrend } }
                    )
                }
            }
        }
    }

    fun processIntent(intent: PromptIntent) {
        when (intent) {
            is PromptIntent.SelectCategoryTab -> {
                _uiState.update { current ->
                    current.copy(
                        selectedCategoryTab = intent.category,
                        prompts = filterPrompts(current.rawPrompts, current.searchQuery, intent.category, current.language)
                    )
                }
            }

            is PromptIntent.SelectCategory -> {
                // For backward compatibility or string-based chips
                val matchedTab = PromptCategory.entries.firstOrNull {
                    it.titleEn.equals(intent.category, ignoreCase = true) ||
                    it.titleFa.equals(intent.category, ignoreCase = true) ||
                    it.id.equals(intent.category, ignoreCase = true)
                } ?: PromptCategory.ALL

                _uiState.update { current ->
                    current.copy(
                        selectedCategoryTab = matchedTab,
                        prompts = filterPrompts(current.rawPrompts, current.searchQuery, matchedTab, current.language)
                    )
                }
            }

            is PromptIntent.SearchQueryChanged -> {
                _uiState.update { current ->
                    current.copy(
                        searchQuery = intent.query,
                        prompts = filterPrompts(current.rawPrompts, intent.query, current.selectedCategoryTab, current.language)
                    )
                }
            }

            is PromptIntent.SwitchLanguage -> {
                _uiState.update { current ->
                    current.copy(
                        language = intent.language,
                        prompts = filterPrompts(current.rawPrompts, current.searchQuery, current.selectedCategoryTab, intent.language)
                    )
                }
                val msg = if (intent.language == AppLanguage.FA) "زبان به فارسی تغییر یافت" else "Language switched to English"
                emitEffect(PromptSideEffect.ShowSnackbar(msg))
            }

            is PromptIntent.ToggleFavorite -> {
                viewModelScope.launch {
                    repository.toggleFavorite(intent.prompt.id, intent.prompt.isFavorite)
                    val isFa = _uiState.value.language == AppLanguage.FA
                    val msg = if (intent.prompt.isFavorite) {
                        if (isFa) "از نشان‌شده‌ها حذف شد" else "Removed from favorites"
                    } else {
                        if (isFa) "به نشان‌شده‌ها اضافه شد ❤️" else "Added to favorites ❤️"
                    }
                    emitEffect(PromptSideEffect.ShowSnackbar(msg))
                }
            }

            is PromptIntent.CopyPrompt -> {
                viewModelScope.launch {
                    AiLauncher.copyToClipboard(
                        intent.context,
                        "Prompt: ${intent.prompt.title}",
                        intent.prompt.promptText
                    )
                    repository.incrementCopy(intent.prompt.id)
                    val msg = LocaleStrings.copiedSuccess(_uiState.value.language)
                    emitEffect(PromptSideEffect.ShowSnackbar(msg))
                }
            }

            is PromptIntent.SaveToGallery -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(isSaving = true) }
                    try {
                        val bitmap = WallpaperRenderer.renderPromptWallpaper(
                            prompt = intent.prompt,
                            width = 1080,
                            height = 1920,
                            filterMode = _uiState.value.activeWallpaperFilter
                        )
                        val result = WallpaperSaver.saveToGallery(intent.context, bitmap, intent.prompt.title)
                        if (result.isSuccess) {
                            emitEffect(PromptSideEffect.ShowSnackbar(LocaleStrings.savedSuccess(_uiState.value.language)))
                        } else {
                            emitEffect(PromptSideEffect.ShowSnackbar("Error: ${result.exceptionOrNull()?.localizedMessage}"))
                        }
                    } catch (e: Exception) {
                        emitEffect(PromptSideEffect.ShowSnackbar("Failed to save: ${e.localizedMessage}"))
                    } finally {
                        _uiState.update { it.copy(isSaving = false) }
                    }
                }
            }

            is PromptIntent.SetAsWallpaper -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(isSaving = true) }
                    try {
                        val bitmap = WallpaperRenderer.renderPromptWallpaper(
                            prompt = intent.prompt,
                            width = 1080,
                            height = 1920,
                            filterMode = _uiState.value.activeWallpaperFilter
                        )
                        val result = WallpaperSaver.setAsDeviceWallpaper(intent.context, bitmap, intent.target)
                        if (result.isSuccess) {
                            emitEffect(PromptSideEffect.ShowSnackbar(LocaleStrings.wallpaperApplied(_uiState.value.language)))
                        } else {
                            emitEffect(PromptSideEffect.ShowSnackbar("Failed: ${result.exceptionOrNull()?.localizedMessage}"))
                        }
                    } catch (e: Exception) {
                        emitEffect(PromptSideEffect.ShowSnackbar("Error: ${e.localizedMessage}"))
                    } finally {
                        _uiState.update { it.copy(isSaving = false) }
                    }
                }
            }

            is PromptIntent.OpenPromptInAi -> {
                when (intent.target) {
                    AiAppTarget.GEMINI -> {
                        AiLauncher.openInGemini(intent.context, intent.prompt)
                        val msg = if (_uiState.value.language == AppLanguage.FA) "پرامپت کپی شد و جمینای باز شد" else "Prompt copied and Gemini launched"
                        emitEffect(PromptSideEffect.ShowSnackbar(msg))
                    }
                    AiAppTarget.CHATGPT -> {
                        AiLauncher.openInChatGPT(intent.context, intent.prompt)
                        val msg = if (_uiState.value.language == AppLanguage.FA) "پرامپت کپی شد و چت‌جی‌پی‌تی باز شد" else "Prompt copied and ChatGPT launched"
                        emitEffect(PromptSideEffect.ShowSnackbar(msg))
                    }
                    AiAppTarget.AI_ENHANCE -> {
                        val enhanced = AiLauncher.generateEnhancedPrompt(intent.prompt.title, intent.prompt.category)
                        _uiState.update { it.copy(generatedAiPromptText = enhanced) }
                        AiLauncher.copyToClipboard(intent.context, "Enhanced Prompt", enhanced)
                        val msg = if (_uiState.value.language == AppLanguage.FA) "پرامپت با هوش مصنوعی بازنویسی و کپی شد ✨" else "Prompt enhanced with AI & copied ✨"
                        emitEffect(PromptSideEffect.ShowSnackbar(msg))
                    }
                }
            }

            is PromptIntent.SharePromptText -> {
                val isPersian = _uiState.value.language == AppLanguage.FA
                AiLauncher.sharePromptAsText(intent.context, intent.prompt, isPersian)
            }

            is PromptIntent.SharePromptImage -> {
                viewModelScope.launch {
                    val bitmap = WallpaperRenderer.renderPromptWallpaper(
                        prompt = intent.prompt,
                        width = 1080,
                        height = 1920,
                        filterMode = _uiState.value.activeWallpaperFilter
                    )
                    WallpaperSaver.shareBitmapWithPrompt(intent.context, bitmap, intent.prompt.title, intent.prompt.promptText)
                }
            }

            is PromptIntent.RefreshTrendingSuggestions -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(isRefreshingSuggestions = true) }
                    repository.refreshTrendingSuggestions()
                    _uiState.update { it.copy(isRefreshingSuggestions = false) }
                    val msg = if (_uiState.value.language == AppLanguage.FA) "ایده‌ها و ترندهای تازه اضافه شدند! 🔥" else "Fresh trending ideas updated! 🔥"
                    emitEffect(PromptSideEffect.ShowSnackbar(msg))
                }
            }

            is PromptIntent.GenerateAiPrompt -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(isGeneratingAi = true) }
                    val generated = AiLauncher.generateEnhancedPrompt(intent.topic, intent.era)
                    _uiState.update {
                        it.copy(
                            isGeneratingAi = false,
                            generatedAiPromptText = generated
                        )
                    }
                    if (intent.context != null) {
                        AiLauncher.copyToClipboard(intent.context, "Generated Prompt", generated)
                    }
                    val msg = if (_uiState.value.language == AppLanguage.FA) "پرامپت جدید با هوش مصنوعی ساخته و کپی شد!" else "New prompt generated & copied!"
                    emitEffect(PromptSideEffect.ShowSnackbar(msg))
                }
            }

            is PromptIntent.OpenDetail -> {
                _uiState.update { it.copy(selectedPromptForDetail = intent.prompt) }
            }

            is PromptIntent.CloseDetail -> {
                _uiState.update { it.copy(selectedPromptForDetail = null) }
            }

            is PromptIntent.OpenAiShareDialog -> {
                _uiState.update { it.copy(selectedPromptForAiShare = intent.prompt) }
            }

            is PromptIntent.CloseAiShareDialog -> {
                _uiState.update { it.copy(selectedPromptForAiShare = null) }
            }

            is PromptIntent.OpenWallpaper -> {
                _uiState.update { it.copy(selectedPromptForWallpaper = intent.prompt) }
            }

            is PromptIntent.CloseWallpaper -> {
                _uiState.update { it.copy(selectedPromptForWallpaper = null) }
            }

            is PromptIntent.SetWallpaperFilter -> {
                _uiState.update { it.copy(activeWallpaperFilter = intent.filter) }
            }

            is PromptIntent.ToggleLockscreenOverlay -> {
                _uiState.update { it.copy(showLockscreenOverlay = !it.showLockscreenOverlay) }
            }

            is PromptIntent.AddCustomPrompt -> {
                viewModelScope.launch {
                    val item = PromptItem(
                        title = intent.title,
                        titleFa = intent.titleFa.ifBlank { intent.title },
                        category = intent.category,
                        categoryFa = intent.category,
                        descriptionFa = intent.titleFa,
                        platformTrend = "Personal Creation • پرامپت شخصی",
                        promptText = intent.promptText,
                        negativePrompt = intent.negativePrompt.ifBlank { "blurry, low quality, deformed, extra fingers, cartoonish" },
                        aspectRatio = intent.aspectRatio,
                        modelTips = intent.modelTips.ifBlank { "Custom tailored prompt" },
                        styleTags = intent.styleTags,
                        photoThemeType = intent.photoThemeType,
                        isFavorite = true,
                        isCustom = true,
                        copyCount = 0,
                        createdAt = System.currentTimeMillis()
                    )
                    repository.insertPrompt(item)
                    val msg = if (_uiState.value.language == AppLanguage.FA) "پرامپت سفارشی ذخیره شد! 🎉" else "Custom prompt saved to collection! 🎉"
                    emitEffect(PromptSideEffect.ShowSnackbar(msg))
                }
            }

            is PromptIntent.DeleteCustomPrompt -> {
                viewModelScope.launch {
                    repository.deletePrompt(intent.prompt.id)
                    if (_uiState.value.selectedPromptForDetail?.id == intent.prompt.id) {
                        _uiState.update { it.copy(selectedPromptForDetail = null) }
                    }
                    if (_uiState.value.selectedPromptForWallpaper?.id == intent.prompt.id) {
                        _uiState.update { it.copy(selectedPromptForWallpaper = null) }
                    }
                    val msg = if (_uiState.value.language == AppLanguage.FA) "پرامپت حذف شد" else "Prompt deleted"
                    emitEffect(PromptSideEffect.ShowSnackbar(msg))
                }
            }
        }
    }

    private fun filterPrompts(
        prompts: List<PromptItem>,
        query: String,
        categoryTab: PromptCategory,
        language: AppLanguage
    ): List<PromptItem> {
        return prompts.filter { item ->
            val matchesCategory = when (categoryTab) {
                PromptCategory.ALL -> true
                PromptCategory.VINTAGE -> {
                    item.category.contains("Vintage", ignoreCase = true) ||
                    item.category.contains("Iran", ignoreCase = true) ||
                    item.categoryFa.contains("دهه ۵۰") ||
                    item.categoryFa.contains("ایران") ||
                    item.photoThemeType == "IRAN_70S" ||
                    item.styleTags.any { it.contains("vintage", ignoreCase = true) || it.contains("1970", ignoreCase = true) || it.contains("iran", ignoreCase = true) }
                }
                PromptCategory.ARTISTIC -> {
                    item.category.contains("Heritage", ignoreCase = true) ||
                    item.categoryFa.contains("اصالت") ||
                    item.categoryFa.contains("فرهنگ") ||
                    item.photoThemeType == "PERSIAN_GARDEN" ||
                    item.styleTags.any { it.contains("heritage", ignoreCase = true) || it.contains("persepolis", ignoreCase = true) || it.contains("artistic", ignoreCase = true) }
                }
                PromptCategory.PORTRAIT -> {
                    item.category.contains("Parents", ignoreCase = true) ||
                    item.category.contains("Romance", ignoreCase = true) ||
                    item.categoryFa.contains("والدین") ||
                    item.categoryFa.contains("عاشقانه") ||
                    item.photoThemeType == "PARENTS_80S" ||
                    item.photoThemeType == "PARTNER_WALLPAPER" ||
                    item.aspectRatio.contains("Portrait") ||
                    item.styleTags.any { it.contains("portrait", ignoreCase = true) || it.contains("parents", ignoreCase = true) || it.contains("candid", ignoreCase = true) }
                }
                PromptCategory.WALLPAPER -> {
                    item.aspectRatio.contains("9:16") ||
                    item.category.contains("Wallpaper", ignoreCase = true) ||
                    item.categoryFa.contains("والپیپر") ||
                    item.photoThemeType == "COUPLE_STARRY" ||
                    item.photoThemeType == "CYBER_NEON" ||
                    item.styleTags.any { it.contains("wallpaper", ignoreCase = true) }
                }
            }

            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.titleFa.contains(query, ignoreCase = true) ||
                    item.promptText.contains(query, ignoreCase = true) ||
                    item.descriptionFa.contains(query, ignoreCase = true) ||
                    item.styleTags.any { it.contains(query, ignoreCase = true) }

            matchesCategory && matchesQuery
        }
    }

    private fun emitEffect(effect: PromptSideEffect) {
        viewModelScope.launch {
            _sideEffects.send(effect)
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

class PromptMviViewModelFactory(private val repository: PromptRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PromptMviViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PromptMviViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
