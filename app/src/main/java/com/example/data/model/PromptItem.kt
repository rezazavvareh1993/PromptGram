package com.example.data.model

data class PromptItem(
    val id: Long = 0,
    val title: String,
    val titleFa: String = "",
    val category: String,
    val categoryFa: String = "",
    val descriptionFa: String = "",
    val platformTrend: String,
    val promptText: String,
    val negativePrompt: String = "blurry, low quality, distorted anatomy, extra fingers, cartoonish, oversaturated, watermark, signature",
    val aspectRatio: String = "9:16 (Phone Wallpaper)",
    val modelTips: String = "Recommended for Midjourney v6.1 or Flux.1 Dev",
    val styleTags: List<String> = emptyList(),
    val photoThemeType: String = "IRAN_70S",
    val isFavorite: Boolean = false,
    val isCustom: Boolean = false,
    val isSuggestedTrend: Boolean = false,
    val copyCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun displayTitle(isPersian: Boolean): String {
        return if (isPersian && titleFa.isNotBlank()) titleFa else title
    }

    fun displayCategory(isPersian: Boolean): String {
        return if (isPersian && categoryFa.isNotBlank()) categoryFa else category
    }
}
