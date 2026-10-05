package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PromptItem
import com.example.ui.components.PromptCard
import com.example.ui.mvi.AiAppTarget
import com.example.ui.mvi.AppLanguage
import com.example.ui.theme.SunsetAmber

@Composable
fun SavedScreen(
    language: AppLanguage,
    favoritePrompts: List<PromptItem>,
    customPrompts: List<PromptItem>,
    onPromptClick: (PromptItem) -> Unit,
    onCopyClick: (PromptItem) -> Unit,
    onSaveToGalleryClick: (PromptItem) -> Unit,
    onWallpaperClick: (PromptItem) -> Unit,
    onFavoriteClick: (PromptItem) -> Unit,
    onShareTextClick: (PromptItem) -> Unit,
    onOpenInAi: (PromptItem, AiAppTarget) -> Unit,
    onDeleteCustomPrompt: (PromptItem) -> Unit,
    onAddCustomPrompt: (
        title: String,
        titleFa: String,
        category: String,
        promptText: String,
        negativePrompt: String,
        aspectRatio: String,
        modelTips: String,
        styleTags: List<String>,
        photoThemeType: String
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val isPersian = language == AppLanguage.FA
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("saved_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = null,
                    tint = SunsetAmber,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isPersian) "پرامپت‌های ذخیره‌شده و سفارشی" else "Saved & Custom Prompts",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isPersian) "مجموعه علاقه‌مندی‌ها و پرامپت‌های ساخته شده توسط شما" else "Your favorited viral prompts and personal creations",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Tab Bar: Favorites vs Custom
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.testTag("saved_tabs")
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = if (isPersian) "نشان‌شده‌ها (${favoritePrompts.size})" else "Favorites (${favoritePrompts.size})",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = if (isPersian) "پرامپت‌های من (${customPrompts.size})" else "My Custom (${customPrompts.size})",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    },
                    icon = { Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }

            val currentList = if (selectedTab == 0) favoritePrompts else customPrompts

            if (currentList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (selectedTab == 0) Icons.Default.Favorite else Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (selectedTab == 0) {
                                if (isPersian) "هنوز پرامپتی را نشان نکرده‌اید" else "No favorite prompts yet"
                            } else {
                                if (isPersian) "هنوز پرامپت سفارشی نساخته‌اید" else "No custom prompts yet"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (selectedTab == 0) {
                                if (isPersian) "با زدن آیکون قلب روی هر پرامپت ترند، آن را در این بخش ذخیره کنید."
                                else "Tap the heart icon on any trending prompt to save it here for fast access!"
                            } else {
                                if (isPersian) "با استفاده از دکمه + پایین یا بخش استودیو، پرامپت‌های دلخواه خود را اضافه کنید."
                                else "Create your own customized AI prompts using the '+' button below or via the Idea Studio!"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(currentList, key = { it.id }) { prompt ->
                        Box {
                            PromptCard(
                                prompt = prompt,
                                language = language,
                                onPromptClick = { onPromptClick(prompt) },
                                onCopyClick = { onCopyClick(prompt) },
                                onSaveToGalleryClick = { onSaveToGalleryClick(prompt) },
                                onWallpaperClick = { onWallpaperClick(prompt) },
                                onFavoriteClick = { onFavoriteClick(prompt) },
                                onShareTextClick = { onShareTextClick(prompt) },
                                onOpenInAi = { target -> onOpenInAi(prompt, target) }
                            )

                            if (selectedTab == 1 && prompt.isCustom) {
                                IconButton(
                                    onClick = { onDeleteCustomPrompt(prompt) },
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(12.dp)
                                        .size(36.dp)
                                        .testTag("delete_custom_button_${prompt.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete prompt",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = SunsetAmber,
            contentColor = Color.Black,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_custom_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add custom prompt")
        }

        if (showAddDialog) {
            AddCustomPromptDialog(
                language = language,
                onDismiss = { showAddDialog = false },
                onSave = onAddCustomPrompt
            )
        }
    }
}
