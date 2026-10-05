package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SuggestionChip
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
fun PromptDetailDialog(
    prompt: PromptItem,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onCopyPrompt: () -> Unit,
    onSaveToGallery: () -> Unit,
    onOpenWallpaper: () -> Unit,
    onToggleFavorite: () -> Unit,
    onShareText: () -> Unit,
    onOpenInAi: (AiAppTarget) -> Unit,
    onShareToAiWithPhoto: () -> Unit = onShareText
) {
    val isPersian = language == AppLanguage.FA
    var isCopied by remember { mutableStateOf(false) }

    LaunchedEffect(isCopied) {
        if (isCopied) {
            delay(2000)
            isCopied = false
        }
    }

    val previewBitmap = remember(prompt.photoThemeType, prompt.id) {
        WallpaperRenderer.renderPromptWallpaper(prompt, width = 720, height = 720)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxSize(0.92f)
                .testTag("prompt_detail_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Image Banner with Overlay Controls
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                ) {
                    Image(
                        bitmap = previewBitmap.asImageBitmap(),
                        contentDescription = prompt.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Top Bar over Image: Close & Favorite
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                .testTag("detail_close_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }

                        Row {
                            IconButton(
                                onClick = onToggleFavorite,
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                    .testTag("detail_favorite_button")
                            ) {
                                Icon(
                                    imageVector = if (prompt.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Toggle favorite",
                                    tint = if (prompt.isFavorite) RoseCoral else Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = onOpenWallpaper,
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                    .testTag("detail_open_wallpaper_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Fullscreen,
                                    contentDescription = "Open wallpaper",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    // Badge at bottom of preview
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = Color.Black.copy(alpha = 0.75f)
                    ) {
                        Text(
                            text = if (isPersian) "پیش‌نمایش تصویر • برای والپیپر گوشی روی آیکون تصویر زمینه بزنید" else "Generated Sample • Tap Wallpaper to set on phone",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.95f),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }

                // Details Content
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Category & Trend Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SunsetAmber.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = prompt.displayCategory(isPersian),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = SunsetAmber,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            text = prompt.platformTrend,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = prompt.displayTitle(isPersian),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Persian description if available
                    if (isPersian && prompt.descriptionFa.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = prompt.descriptionFa,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // AI Prompt Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isPersian) "پرامپت اصلی هوش مصنوعی" else "Main AI Prompt",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        OutlinedButton(
                            onClick = {
                                onCopyPrompt()
                                isCopied = true
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                horizontal = 12.dp,
                                vertical = 4.dp
                            ),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(
                                imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                contentDescription = "Copy prompt",
                                tint = if (isCopied) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isCopied) (if (isPersian) "کپی شد! ✓" else "Copied! ✓") else (if (isPersian) "کپی" else "Copy"),
                                fontSize = 12.sp,
                                color = if (isCopied) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                onCopyPrompt()
                                isCopied = true
                            },
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = prompt.promptText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 22.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isCopied) {
                                        if (isPersian) "متن در کلیپ‌بورد ذخیره شد ✓" else "Copied to clipboard ✓"
                                    } else {
                                        if (isPersian) "برای کپی کردن روی متن ضربه بزنید 📋" else "Tap text to copy to clipboard 📋"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isCopied) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Negative Prompt Section
                    if (prompt.negativePrompt.isNotBlank()) {
                        Text(
                            text = if (isPersian) "پرامپت منفی (موارد نامطلوب)" else "Negative Prompt (What to Avoid)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        ) {
                            Text(
                                text = prompt.negativePrompt,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Generation Guidance Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = ElectricViolet.copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Model tips",
                                tint = ElectricViolet,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isPersian) "راهنمای ابعاد و مدل هوش مصنوعی" else "Generation & Aspect Ratio Tips",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${prompt.modelTips}\n${if (isPersian) "نسبت تصویر:" else "Aspect Ratio:"} ${prompt.aspectRatio}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Open in Gemini / ChatGPT Buttons Row
                    Text(
                        text = if (isPersian) "اجرا مستقیم در هوش مصنوعی" else "Open in AI Generator",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilledTonalButton(
                            onClick = { onOpenInAi(AiAppTarget.GEMINI) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = ElectricViolet.copy(alpha = 0.18f),
                                contentColor = ElectricViolet
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("detail_open_gemini_button")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Gemini", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        FilledTonalButton(
                            onClick = { onOpenInAi(AiAppTarget.CHATGPT) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = SunsetAmber.copy(alpha = 0.18f),
                                contentColor = SunsetAmber
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("detail_open_chatgpt_button")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ChatGPT", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Send to AI with Photo (Camera or Gallery)
                    Button(
                        onClick = onShareToAiWithPhoto,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("detail_share_with_photo_button")
                    ) {
                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isPersian) "📷 ارسال به هوش مصنوعی با عکس (دوربین / گالری)" else "📷 Send to AI with Photo",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Primary Actions: Save to Gallery, Wallpaper Studio & Share Text
                    Button(
                        onClick = onSaveToGallery,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SunsetAmber),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("detail_save_gallery_button")
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = LocaleStrings.saveToGallery(language),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    FilledTonalButton(
                        onClick = onOpenWallpaper,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("detail_view_wallpaper_button")
                    ) {
                        Icon(imageVector = Icons.Default.Wallpaper, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isPersian) "استودیو والپیپر گوشی (۹:۱۶)" else "Mobile Wallpaper Studio",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = onShareText,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("detail_share_text_button")
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isPersian) "ارسال متن پرامپت به پیام‌رسان‌ها" else "Share Prompt Text to Messengers",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}
