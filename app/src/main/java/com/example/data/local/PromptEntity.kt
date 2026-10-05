package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.PromptItem

@Entity(tableName = "prompts")
data class PromptEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val titleFa: String = "",
    val category: String,
    val categoryFa: String = "",
    val descriptionFa: String = "",
    val platformTrend: String,
    val promptText: String,
    val negativePrompt: String,
    val aspectRatio: String,
    val modelTips: String,
    val styleTags: String, // Comma separated tags
    val photoThemeType: String,
    val isFavorite: Boolean,
    val isCustom: Boolean,
    val isSuggestedTrend: Boolean = false,
    val copyCount: Int,
    val createdAt: Long
) {
    fun toDomain(): PromptItem {
        return PromptItem(
            id = id,
            title = title,
            titleFa = titleFa.ifBlank { title },
            category = category,
            categoryFa = categoryFa.ifBlank { category },
            descriptionFa = descriptionFa,
            platformTrend = platformTrend,
            promptText = promptText,
            negativePrompt = negativePrompt,
            aspectRatio = aspectRatio,
            modelTips = modelTips,
            styleTags = if (styleTags.isBlank()) emptyList() else styleTags.split(",").map { it.trim() },
            photoThemeType = photoThemeType,
            isFavorite = isFavorite,
            isCustom = isCustom,
            isSuggestedTrend = isSuggestedTrend,
            copyCount = copyCount,
            createdAt = createdAt
        )
    }

    companion object {
        fun fromDomain(item: PromptItem): PromptEntity {
            return PromptEntity(
                id = item.id,
                title = item.title,
                titleFa = item.titleFa,
                category = item.category,
                categoryFa = item.categoryFa,
                descriptionFa = item.descriptionFa,
                platformTrend = item.platformTrend,
                promptText = item.promptText,
                negativePrompt = item.negativePrompt,
                aspectRatio = item.aspectRatio,
                modelTips = item.modelTips,
                styleTags = item.styleTags.joinToString(","),
                photoThemeType = item.photoThemeType,
                isFavorite = item.isFavorite,
                isCustom = item.isCustom,
                isSuggestedTrend = item.isSuggestedTrend,
                copyCount = item.copyCount,
                createdAt = item.createdAt
            )
        }
    }
}
