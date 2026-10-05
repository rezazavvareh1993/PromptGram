package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PromptItem
import com.example.ui.mvi.AppLanguage
import com.example.ui.theme.SunsetAmber
import com.example.util.LocaleStrings
import com.example.util.WallpaperRenderer
import com.example.util.WallpaperTarget

@Composable
fun WallpaperScreen(
    prompt: PromptItem,
    language: AppLanguage,
    activeFilter: String,
    showLockscreenOverlay: Boolean,
    onFilterChange: (String) -> Unit,
    onToggleLockscreen: () -> Unit,
    onSaveToGallery: () -> Unit,
    onSetWallpaper: (WallpaperTarget) -> Unit,
    onShare: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val isPersian = language == AppLanguage.FA
    var showSetDialog by remember { mutableStateOf(false) }

    val filterOptions = listOf(
        "Normal",
        "Kodachrome Warm",
        "70s Sepia Nostalgia",
        "Cyber Magenta",
        "Midnight Cool",
        "Noir Film"
    )

    // Render wallpaper bitmap with current filter at 1080x1920
    val wallpaperBitmap = remember(prompt.photoThemeType, prompt.id, activeFilter) {
        WallpaperRenderer.renderPromptWallpaper(
            prompt = prompt,
            width = 1080,
            height = 1920,
            filterMode = activeFilter
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("wallpaper_screen")
    ) {
        // Fullscreen Image Background
        Image(
            bitmap = wallpaperBitmap.asImageBitmap(),
            contentDescription = "Wallpaper preview for ${prompt.title}",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Mock Lockscreen Overlay
        if (showLockscreenOverlay) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp)
            ) {
                // Top Lock Icon & Clock
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isPersian) "۰۹:۴۱" else "09:41",
                        fontSize = 76.sp,
                        fontWeight = FontWeight.Light,
                        fontFamily = FontFamily.SansSerif,
                        color = Color.White
                    )
                    Text(
                        text = if (isPersian) "یکشنبه، ۱۲ مهر" else "Sunday, October 4",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                // Bottom Action Shortcuts
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 145.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashlightOn,
                            contentDescription = "Torch",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.Black.copy(alpha = 0.45f)
                    ) {
                        Text(
                            text = if (isPersian) "برای باز کردن به بالا بکشید" else "Swipe up to unlock",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Camera",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(42.dp)
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                    .testTag("wallpaper_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            // Lockscreen Overlay Toggle
            Surface(
                onClick = onToggleLockscreen,
                shape = RoundedCornerShape(20.dp),
                color = Color.Black.copy(alpha = 0.6f),
                modifier = Modifier.testTag("toggle_lockscreen_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (showLockscreenOverlay) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle overlay",
                        tint = if (showLockscreenOverlay) SunsetAmber else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (showLockscreenOverlay) LocaleStrings.lockscreenMode(language) else LocaleStrings.cleanImageMode(language),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Bottom Controls Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f),
                            Color.Black
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = if (isPersian) "فیلتر و تم رنگی والپیپر" else "Photo Aesthetic Filter",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filterOptions) { filter ->
                    val selected = filter == activeFilter
                    FilterChip(
                        selected = selected,
                        onClick = { onFilterChange(filter) },
                        label = { Text(filter, fontSize = 12.sp) },
                        leadingIcon = if (selected) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color.White.copy(alpha = 0.15f),
                            labelColor = Color.White,
                            selectedContainerColor = SunsetAmber,
                            selectedLabelColor = Color.Black
                        ),
                        border = null,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Primary Bottom Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onSaveToGallery,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SunsetAmber),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("save_wallpaper_gallery_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Save to gallery",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = LocaleStrings.saveToGallery(language),
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 13.sp
                    )
                }

                Button(
                    onClick = { showSetDialog = true },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("set_device_wallpaper_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Wallpaper,
                        contentDescription = "Set Wallpaper",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = LocaleStrings.setWallpaper(language),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }

                IconButton(
                    onClick = onShare,
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                        .testTag("share_wallpaper_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.White
                    )
                }
            }
        }
    }

    // Set Wallpaper Dialog
    if (showSetDialog) {
        AlertDialog(
            onDismissRequest = { showSetDialog = false },
            title = {
                Text(
                    text = if (isPersian) "اعمال تصویر زمینه" else "Apply Wallpaper",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = if (isPersian)
                            "محل قرارگیری تصویر زمینه را انتخاب کنید:"
                        else
                            "Choose where you would like to apply this wallpaper:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = {
                            showSetDialog = false
                            onSetWallpaper(WallpaperTarget.HOME)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (isPersian) "فقط صفحه اصلی (Home Screen)" else "Home Screen Only")
                    }

                    OutlinedButton(
                        onClick = {
                            showSetDialog = false
                            onSetWallpaper(WallpaperTarget.LOCK)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (isPersian) "فقط صفحه قفل (Lock Screen)" else "Lock Screen Only")
                    }

                    Button(
                        onClick = {
                            showSetDialog = false
                            onSetWallpaper(WallpaperTarget.BOTH)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (isPersian) "هر دو صفحه (قفل و اصلی)" else "Both Home & Lock Screen")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showSetDialog = false }) {
                    Text(if (isPersian) "انصراف" else "Cancel")
                }
            }
        )
    }
}
