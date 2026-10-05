package com.example.ui.mvi

import android.content.Context
import com.example.data.model.PromptItem
import com.example.util.WallpaperTarget

enum class AppLanguage {
    FA, // Persian (Default)
    EN  // English
}

enum class AiAppTarget {
    GEMINI,
    CHATGPT,
    AI_ENHANCE
}

enum class PromptCategory(
    val id: String,
    val titleEn: String,
    val titleFa: String
) {
    ALL("all", "All", "همه ترندها"),
    VINTAGE("vintage", "Vintage", "نوستالژی و دهه ۵۰"),
    ARTISTIC("artistic", "Artistic", "هنری و اصالت"),
    PORTRAIT("portrait", "Portrait", "پرتره و چهره"),
    WALLPAPER("wallpaper", "Wallpaper", "والپیپر گوشی (۹:۱۶)");

    fun displayTitle(isPersian: Boolean): String = if (isPersian) titleFa else titleEn
}

data class PromptUiState(
    val prompts: List<PromptItem> = emptyList(),
    val rawPrompts: List<PromptItem> = emptyList(),
    val favoritePrompts: List<PromptItem> = emptyList(),
    val customPrompts: List<PromptItem> = emptyList(),
    val suggestedPrompts: List<PromptItem> = emptyList(),
    val selectedCategoryTab: PromptCategory = PromptCategory.ALL,
    val searchQuery: String = "",
    val language: AppLanguage = AppLanguage.FA, // Default is Persian!
    val activeWallpaperFilter: String = "Normal",
    val selectedPromptForDetail: PromptItem? = null,
    val selectedPromptForWallpaper: PromptItem? = null,
    val selectedPromptForAiShare: PromptItem? = null,
    val showLockscreenOverlay: Boolean = true,
    val isSaving: Boolean = false,
    val isRefreshingSuggestions: Boolean = false,
    val isGeneratingAi: Boolean = false,
    val generatedAiPromptText: String? = null
)

sealed interface PromptIntent {
    data class SelectCategoryTab(val category: PromptCategory) : PromptIntent
    data class SelectCategory(val category: String) : PromptIntent
    data class SearchQueryChanged(val query: String) : PromptIntent
    data class SwitchLanguage(val language: AppLanguage) : PromptIntent
    data class ToggleFavorite(val prompt: PromptItem) : PromptIntent
    data class CopyPrompt(val context: Context, val prompt: PromptItem) : PromptIntent
    data class SaveToGallery(val context: Context, val prompt: PromptItem) : PromptIntent
    data class SetAsWallpaper(val context: Context, val prompt: PromptItem, val target: WallpaperTarget) : PromptIntent
    data class OpenPromptInAi(val context: Context, val prompt: PromptItem, val target: AiAppTarget) : PromptIntent
    data class OpenAiShareDialog(val prompt: PromptItem) : PromptIntent
    data object CloseAiShareDialog : PromptIntent
    data class SharePromptText(val context: Context, val prompt: PromptItem) : PromptIntent
    data class SharePromptImage(val context: Context, val prompt: PromptItem) : PromptIntent
    data object RefreshTrendingSuggestions : PromptIntent
    data class GenerateAiPrompt(val context: Context, val topic: String, val era: String) : PromptIntent
    data class OpenDetail(val prompt: PromptItem) : PromptIntent
    data object CloseDetail : PromptIntent
    data class OpenWallpaper(val prompt: PromptItem) : PromptIntent
    data object CloseWallpaper : PromptIntent
    data class SetWallpaperFilter(val filter: String) : PromptIntent
    data object ToggleLockscreenOverlay : PromptIntent
    data class AddCustomPrompt(
        val title: String,
        val titleFa: String,
        val category: String,
        val promptText: String,
        val negativePrompt: String,
        val aspectRatio: String,
        val modelTips: String,
        val styleTags: List<String>,
        val photoThemeType: String
    ) : PromptIntent
    data class DeleteCustomPrompt(val prompt: PromptItem) : PromptIntent
}

sealed interface PromptSideEffect {
    data class ShowSnackbar(val message: String) : PromptSideEffect
    data class OpenUrl(val url: String) : PromptSideEffect
}
