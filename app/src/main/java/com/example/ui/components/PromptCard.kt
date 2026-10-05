package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PromptItem
import com.example.ui.mvi.AiAppTarget
import com.example.ui.mvi.AppLanguage
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.RoseCoral
import com.example.ui.theme.SunsetAmber
import com.example.util.LocaleStrings
import com.example.util.WallpaperRenderer
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PromptCard(
    prompt: PromptItem,
    language: AppLanguage,
    onPromptClick: () -> Unit,
    onCopyClick: () -> Unit,
    onSaveToGalleryClick: () -> Unit,
    onWallpaperClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onShareTextClick: () -> Unit,
    onOpenInAi: (AiAppTarget) -> Unit,
    onShareToAi: () -> Unit = onShareTextClick,
    modifier: Modifier = Modifier
) {
    val isPersian = language == AppLanguage.FA
    var showAiMenu by remember { mutableStateOf(false) }
    var isCopied by remember { mutableStateOf(false) }

    LaunchedEffect(isCopied) {
        if (isCopied) {
            delay(2000)
            isCopied = false
        }
    }

    // Render preview thumbnail for the prompt
    val previewBitmap = remember(prompt.photoThemeType, prompt.id) {
        WallpaperRenderer.renderPromptWallpaper(prompt, width = 640, height = 480)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onPromptClick() }
            .testTag("prompt_card_${prompt.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Visual Preview Thumbnail Box with Overlays
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            ) {
                Image(
                    bitmap = previewBitmap.asImageBitmap(),
                    contentDescription = "Visual preview for ${prompt.title}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dark gradient overlay for readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.5f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.82f)
                                )
                            )
                        )
                )

                // Platform trend pill top-left
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (prompt.category.contains("Iran") || prompt.categoryFa.contains("ایران")) SunsetAmber else ElectricViolet
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = prompt.platformTrend,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Top right actions: Favorite & Wallpaper preview
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd)
                ) {
                    IconButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color.Black.copy(alpha = 0.45f), CircleShape)
                            .testTag("favorite_button_${prompt.id}")
                    ) {
                        Icon(
                            imageVector = if (prompt.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Toggle favorite",
                            tint = if (prompt.isFavorite) RoseCoral else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onWallpaperClick,
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color.Black.copy(alpha = 0.45f), CircleShape)
                            .testTag("fullscreen_button_${prompt.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = "Fullscreen Wallpaper View",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Title and category badge at bottom of preview
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                ) {
                    Text(
                        text = prompt.displayTitle(isPersian),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = prompt.displayCategory(isPersian),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFFD166),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = " • ${prompt.aspectRatio}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // Card Body
            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                // Persian Description (if available)
                if (isPersian && prompt.descriptionFa.isNotBlank()) {
                    Text(
                        text = prompt.descriptionFa,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Prompt text block with 1-tap copy to clipboard
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            onCopyClick()
                            isCopied = true
                        }
                        .testTag("prompt_snippet_${prompt.id}"),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isPersian) "متن پرامپت هوش مصنوعی" else "AI Prompt",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                    contentDescription = "Copy snippet",
                                    tint = if (isCopied) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isCopied) {
                                        if (isPersian) "کپی شد! ✓" else "Copied! ✓"
                                    } else {
                                        if (isPersian) "کلیک برای کپی" else "Tap to copy"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isCopied) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = prompt.promptText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tags chips
                if (prompt.styleTags.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        prompt.styleTags.take(4).forEach { tag ->
                            SuggestionChip(
                                onClick = {},
                                label = {
                                    Text(
                                        text = "#$tag",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 11.sp
                                    )
                                },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                border = null,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(26.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Primary Action: Copy Prompt to Clipboard
                    Button(
                        onClick = {
                            onCopyClick()
                            isCopied = true
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCopied) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("copy_prompt_button_${prompt.id}")
                    ) {
                        Icon(
                            imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = "Copy prompt",
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isCopied) {
                                if (isPersian) "کپی شد! ✓" else "Copied! ✓"
                            } else {
                                LocaleStrings.copyPrompt(language)
                            },
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }

                    // Save to Gallery Button
                    FilledTonalButton(
                        onClick = onSaveToGalleryClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = SunsetAmber.copy(alpha = 0.18f),
                            contentColor = SunsetAmber
                        ),
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("save_gallery_button_${prompt.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Save to gallery",
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isPersian) "گالری" else "Save",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }

                    // Open in AI (ChatGPT / Gemini) Button
                    Box {
                        IconButton(
                            onClick = { showAiMenu = true },
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    ElectricViolet.copy(alpha = 0.15f),
                                    RoundedCornerShape(12.dp)
                                )
                                .testTag("ai_menu_button_${prompt.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Open in AI",
                                tint = ElectricViolet,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showAiMenu,
                            onDismissRequest = { showAiMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (isPersian) "📷 ارسال به هوش مصنوعی با عکس" else "📷 Send to AI with Photo") },
                                onClick = {
                                    showAiMenu = false
                                    onShareToAi()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(LocaleStrings.openInGemini(language)) },
                                onClick = {
                                    showAiMenu = false
                                    onOpenInAi(AiAppTarget.GEMINI)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(LocaleStrings.openInChatGpt(language)) },
                                onClick = {
                                    showAiMenu = false
                                    onOpenInAi(AiAppTarget.CHATGPT)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(LocaleStrings.aiGenerate(language)) },
                                onClick = {
                                    showAiMenu = false
                                    onOpenInAi(AiAppTarget.AI_ENHANCE)
                                }
                            )
                        }
                    }

                    // Share Prompt & Photo to AI / Messaging Apps
                    IconButton(
                        onClick = onShareToAi,
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(12.dp)
                            )
                            .testTag("share_text_button_${prompt.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share prompt and photo to AI",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
