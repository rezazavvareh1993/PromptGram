package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PromptItem
import com.example.ui.components.PromptCard
import com.example.ui.mvi.AppLanguage
import com.example.ui.mvi.PromptCategory
import com.example.ui.mvi.PromptIntent
import com.example.ui.mvi.PromptUiState
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.RoseCoral
import com.example.ui.theme.SunsetAmber
import com.example.util.LocaleStrings

@Composable
fun HomeScreen(
    state: PromptUiState,
    onIntent: (PromptIntent) -> Unit,
    onNavigateToStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isPersian = state.language == AppLanguage.FA

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen")
    ) {
        // Top App Bar with Branding & Language Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(ElectricViolet, SunsetAmber)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "PromptGram Icon",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = LocaleStrings.appName(state.language),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = LocaleStrings.appTagline(state.language),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Language Switcher Chip (FA / EN)
            Surface(
                onClick = {
                    val nextLang = if (state.language == AppLanguage.FA) AppLanguage.EN else AppLanguage.FA
                    onIntent(PromptIntent.SwitchLanguage(nextLang))
                },
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.testTag("language_toggle_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Toggle Language",
                        tint = SunsetAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isPersian) "فارسی (FA)" else "English (EN)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { onIntent(PromptIntent.SearchQueryChanged(it)) },
            placeholder = { Text(LocaleStrings.searchPlaceholder(state.language)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (state.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onIntent(PromptIntent.SearchQueryChanged("")) }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                focusedBorderColor = ElectricViolet,
                unfocusedBorderColor = Color.Transparent
            ),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .testTag("search_text_field")
        )

        // Category Tab System (Vintage, Artistic, Portrait, Wallpaper, All)
        val categoryTabs = listOf(
            PromptCategory.ALL to Icons.Default.Whatshot,
            PromptCategory.VINTAGE to Icons.Default.HistoryEdu,
            PromptCategory.ARTISTIC to Icons.Default.Palette,
            PromptCategory.PORTRAIT to Icons.Default.Person,
            PromptCategory.WALLPAPER to Icons.Default.PhoneAndroid
        )

        val selectedTabIndex = categoryTabs.indexOfFirst { it.first == state.selectedCategoryTab }.coerceAtLeast(0)

        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            edgePadding = 16.dp,
            containerColor = Color.Transparent,
            divider = {},
            indicator = { tabPositions ->
                if (selectedTabIndex < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        height = 3.dp,
                        color = if (state.selectedCategoryTab == PromptCategory.VINTAGE) SunsetAmber else ElectricViolet
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .testTag("category_tab_row")
        ) {
            categoryTabs.forEachIndexed { index, (category, icon) ->
                val isSelected = category == state.selectedCategoryTab
                Tab(
                    selected = isSelected,
                    onClick = { onIntent(PromptIntent.SelectCategoryTab(category)) },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (isSelected) {
                                    if (category == PromptCategory.VINTAGE) SunsetAmber else ElectricViolet
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = category.displayTitle(isPersian),
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.onSurface
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    },
                    modifier = Modifier.testTag("category_tab_${category.id}")
                )
            }
        }

        // Seamless content switching animated with MVI State
        AnimatedContent(
            targetState = state.selectedCategoryTab,
            transitionSpec = {
                (fadeIn() + slideInHorizontally { width -> if (targetState.ordinal > initialState.ordinal) width / 4 else -width / 4 })
                    .togetherWith(fadeOut() + slideOutHorizontally { width -> if (targetState.ordinal > initialState.ordinal) -width / 4 else width / 4 })
            },
            label = "CategoryTabContentAnimation",
            modifier = Modifier.weight(1f)
        ) { targetCategory ->
            if (state.prompts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = LocaleStrings.emptyStateTitle(state.language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = LocaleStrings.emptyStateDesc(state.language),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Hero Spotlight Banner with Refresh Trends Action
                    item {
                        HeroTrendBanner(
                            language = state.language,
                            selectedCategory = targetCategory,
                            onExploreStudio = onNavigateToStudio,
                            onRefreshTrends = { onIntent(PromptIntent.RefreshTrendingSuggestions) }
                        )
                    }

                    // Quick Access Favorites Carousel (Room Database Saved Prompts)
                    if (state.favoritePrompts.isNotEmpty()) {
                        item {
                            QuickAccessFavoritesSection(
                                favoritePrompts = state.favoritePrompts,
                                language = state.language,
                                onPromptClick = { onIntent(PromptIntent.OpenDetail(it)) },
                                onCopyClick = { onIntent(PromptIntent.CopyPrompt(context, it)) }
                            )
                        }
                    }

                    items(state.prompts, key = { it.id }) { prompt ->
                        PromptCard(
                            prompt = prompt,
                            language = state.language,
                            onPromptClick = { onIntent(PromptIntent.OpenDetail(prompt)) },
                            onCopyClick = { onIntent(PromptIntent.CopyPrompt(context, prompt)) },
                            onSaveToGalleryClick = { onIntent(PromptIntent.SaveToGallery(context, prompt)) },
                            onWallpaperClick = { onIntent(PromptIntent.OpenWallpaper(prompt)) },
                            onFavoriteClick = { onIntent(PromptIntent.ToggleFavorite(prompt)) },
                            onShareTextClick = { onIntent(PromptIntent.OpenAiShareDialog(prompt)) },
                            onShareToAi = { onIntent(PromptIntent.OpenAiShareDialog(prompt)) },
                            onOpenInAi = { target -> onIntent(PromptIntent.OpenPromptInAi(context, prompt, target)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroTrendBanner(
    language: AppLanguage,
    selectedCategory: PromptCategory,
    onExploreStudio: () -> Unit,
    onRefreshTrends: () -> Unit
) {
    val isPersian = language == AppLanguage.FA

    val bannerCategoryTitle = when (selectedCategory) {
        PromptCategory.ALL -> if (isPersian) "همه ترندهای داغ" else "All Trending Feeds"
        PromptCategory.VINTAGE -> if (isPersian) "ترند نوستالژی دهه ۵۰ ایران" else "1970s Vintage Iran Trends"
        PromptCategory.ARTISTIC -> if (isPersian) "ترندهای هنری و اصالت" else "Artistic & Heritage Prompts"
        PromptCategory.PORTRAIT -> if (isPersian) "پرتره‌های سینمایی و جوانی والدین" else "Portrait & Parents Heritage"
        PromptCategory.WALLPAPER -> if (isPersian) "والپیپرهای ترند گوشی ۹:۱۶" else "Mobile 9:16 Wallpapers"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_trend_banner"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF4A154B),
                            Color(0xFF8A2BE2),
                            Color(0xFFE65100)
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.35f)
                    ) {
                        Text(
                            text = "🔥 $bannerCategoryTitle",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFD166),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Refresh Trending Ideas Button
                    Surface(
                        onClick = onRefreshTrends,
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.35f),
                        modifier = Modifier.testTag("refresh_trends_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = Color(0xFFFFD166),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (language == AppLanguage.FA) "ایده‌های تازه" else "New Trends",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = LocaleStrings.heroBannerTitle(language),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = LocaleStrings.heroBannerSubtitle(language),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.92f),
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onExploreStudio() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = LocaleStrings.heroBannerAction(language),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD166)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickAccessFavoritesSection(
    favoritePrompts: List<PromptItem>,
    language: AppLanguage,
    onPromptClick: (PromptItem) -> Unit,
    onCopyClick: (PromptItem) -> Unit
) {
    val isPersian = language == AppLanguage.FA

    Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 4.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = RoseCoral,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isPersian) "⭐ دسترسی سریع به نشان‌شده‌ها (${favoritePrompts.size})" else "⭐ Quick Access Favorites (${favoritePrompts.size})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(favoritePrompts, key = { "quick_fav_${it.id}" }) { prompt ->
                Card(
                    modifier = Modifier
                        .width(220.dp)
                        .clickable { onPromptClick(prompt) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = prompt.displayTitle(isPersian),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = prompt.promptText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { onCopyClick(prompt) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isPersian) "کپی سریع" else "Quick Copy", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
