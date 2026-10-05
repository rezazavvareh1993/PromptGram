package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.PromptDatabase
import com.example.data.repository.PromptRepository
import com.example.ui.mvi.AppLanguage
import com.example.ui.mvi.PromptIntent
import com.example.ui.mvi.PromptMviViewModel
import com.example.ui.mvi.PromptMviViewModelFactory
import com.example.ui.mvi.PromptSideEffect
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PromptDetailDialog
import com.example.ui.screens.SavedScreen
import com.example.ui.screens.ShareToAiDialog
import com.example.ui.screens.StudioScreen
import com.example.ui.screens.WallpaperScreen
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SunsetAmber
import com.example.util.LocaleStrings
import kotlinx.coroutines.launch

enum class AppDestination {
    EXPLORE,
    STUDIO,
    SAVED
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = PromptDatabase.getInstance(this)
        val repository = PromptRepository(database.promptDao())

        setContent {
            MyApplicationTheme {
                val viewModel: PromptMviViewModel = viewModel(
                    factory = PromptMviViewModelFactory(repository)
                )
                PromptGramApp(viewModel)
            }
        }
    }
}

@Composable
fun PromptGramApp(viewModel: PromptMviViewModel) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var currentDestination by remember { mutableStateOf(AppDestination.EXPLORE) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Handle side effects (Snackbars, messages)
    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                is PromptSideEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is PromptSideEffect.OpenUrl -> {
                    // Url opened by intent in helper
                }
            }
        }
    }

    // Set LayoutDirection: RTL for Persian, LTR for English
    val layoutDirection = if (uiState.language == AppLanguage.FA) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        // Back navigation handling
        BackHandler(enabled = currentDestination != AppDestination.EXPLORE && uiState.selectedPromptForWallpaper == null) {
            currentDestination = AppDestination.EXPLORE
        }

        // Fullscreen Wallpaper Screen
        if (uiState.selectedPromptForWallpaper != null) {
            val prompt = uiState.selectedPromptForWallpaper!!
            WallpaperScreen(
                prompt = prompt,
                language = uiState.language,
                activeFilter = uiState.activeWallpaperFilter,
                showLockscreenOverlay = uiState.showLockscreenOverlay,
                onFilterChange = { viewModel.processIntent(PromptIntent.SetWallpaperFilter(it)) },
                onToggleLockscreen = { viewModel.processIntent(PromptIntent.ToggleLockscreenOverlay) },
                onSaveToGallery = { viewModel.processIntent(PromptIntent.SaveToGallery(context, prompt)) },
                onSetWallpaper = { target -> viewModel.processIntent(PromptIntent.SetAsWallpaper(context, prompt, target)) },
                onShare = { viewModel.processIntent(PromptIntent.SharePromptImage(context, prompt)) },
                onBack = { viewModel.processIntent(PromptIntent.CloseWallpaper) }
            )
        } else {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("main_scaffold"),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp,
                        modifier = Modifier.testTag("bottom_nav_bar")
                    ) {
                        NavigationBarItem(
                            selected = currentDestination == AppDestination.EXPLORE,
                            onClick = { currentDestination = AppDestination.EXPLORE },
                            icon = {
                                Icon(
                                    imageVector = if (currentDestination == AppDestination.EXPLORE) Icons.Filled.Explore else Icons.Outlined.Explore,
                                    contentDescription = LocaleStrings.navExplore(uiState.language)
                                )
                            },
                            label = { Text(LocaleStrings.navExplore(uiState.language), fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ElectricViolet,
                                indicatorColor = ElectricViolet.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_explore")
                        )

                        NavigationBarItem(
                            selected = currentDestination == AppDestination.STUDIO,
                            onClick = { currentDestination = AppDestination.STUDIO },
                            icon = {
                                Icon(
                                    imageVector = if (currentDestination == AppDestination.STUDIO) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome,
                                    contentDescription = LocaleStrings.navStudio(uiState.language)
                                )
                            },
                            label = { Text(LocaleStrings.navStudio(uiState.language), fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SunsetAmber,
                                indicatorColor = SunsetAmber.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_studio")
                        )

                        NavigationBarItem(
                            selected = currentDestination == AppDestination.SAVED,
                            onClick = { currentDestination = AppDestination.SAVED },
                            icon = {
                                Icon(
                                    imageVector = if (currentDestination == AppDestination.SAVED) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = LocaleStrings.navSaved(uiState.language)
                                )
                            },
                            label = { Text(LocaleStrings.navSaved(uiState.language), fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ElectricViolet,
                                indicatorColor = ElectricViolet.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_saved")
                        )
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    when (currentDestination) {
                        AppDestination.EXPLORE -> {
                            HomeScreen(
                                state = uiState,
                                onIntent = { viewModel.processIntent(it) },
                                onNavigateToStudio = { currentDestination = AppDestination.STUDIO }
                            )
                        }

                        AppDestination.STUDIO -> {
                            StudioScreen(
                                language = uiState.language,
                                onCopyPrompt = { viewModel.processIntent(PromptIntent.CopyPrompt(context, it)) },
                                onSaveToCollection = { title, titleFa, category, promptText, negativePrompt, aspectRatio, modelTips, styleTags, photoThemeType ->
                                    viewModel.processIntent(
                                        PromptIntent.AddCustomPrompt(
                                            title, titleFa, category, promptText, negativePrompt, aspectRatio, modelTips, styleTags, photoThemeType
                                        )
                                    )
                                },
                                onOpenWallpaper = { viewModel.processIntent(PromptIntent.OpenWallpaper(it)) },
                                onOpenInAi = { prompt, target -> viewModel.processIntent(PromptIntent.OpenPromptInAi(context, prompt, target)) },
                                onShareText = { viewModel.processIntent(PromptIntent.OpenAiShareDialog(it)) }
                            )
                        }

                        AppDestination.SAVED -> {
                            SavedScreen(
                                language = uiState.language,
                                favoritePrompts = uiState.favoritePrompts,
                                customPrompts = uiState.customPrompts,
                                onPromptClick = { viewModel.processIntent(PromptIntent.OpenDetail(it)) },
                                onCopyClick = { viewModel.processIntent(PromptIntent.CopyPrompt(context, it)) },
                                onSaveToGalleryClick = { viewModel.processIntent(PromptIntent.SaveToGallery(context, it)) },
                                onWallpaperClick = { viewModel.processIntent(PromptIntent.OpenWallpaper(it)) },
                                onFavoriteClick = { viewModel.processIntent(PromptIntent.ToggleFavorite(it)) },
                                onShareTextClick = { viewModel.processIntent(PromptIntent.OpenAiShareDialog(it)) },
                                onOpenInAi = { prompt, target -> viewModel.processIntent(PromptIntent.OpenPromptInAi(context, prompt, target)) },
                                onDeleteCustomPrompt = { viewModel.processIntent(PromptIntent.DeleteCustomPrompt(it)) },
                                onAddCustomPrompt = { title, titleFa, category, promptText, negativePrompt, aspectRatio, modelTips, styleTags, photoThemeType ->
                                    viewModel.processIntent(
                                        PromptIntent.AddCustomPrompt(
                                            title, titleFa, category, promptText, negativePrompt, aspectRatio, modelTips, styleTags, photoThemeType
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // Prompt Detail Dialog
            uiState.selectedPromptForDetail?.let { prompt ->
                PromptDetailDialog(
                    prompt = prompt,
                    language = uiState.language,
                    onDismiss = { viewModel.processIntent(PromptIntent.CloseDetail) },
                    onCopyPrompt = { viewModel.processIntent(PromptIntent.CopyPrompt(context, prompt)) },
                    onSaveToGallery = { viewModel.processIntent(PromptIntent.SaveToGallery(context, prompt)) },
                    onOpenWallpaper = {
                        viewModel.processIntent(PromptIntent.CloseDetail)
                        viewModel.processIntent(PromptIntent.OpenWallpaper(prompt))
                    },
                    onToggleFavorite = { viewModel.processIntent(PromptIntent.ToggleFavorite(prompt)) },
                    onShareText = { viewModel.processIntent(PromptIntent.OpenAiShareDialog(prompt)) },
                    onOpenInAi = { target -> viewModel.processIntent(PromptIntent.OpenPromptInAi(context, prompt, target)) },
                    onShareToAiWithPhoto = { viewModel.processIntent(PromptIntent.OpenAiShareDialog(prompt)) }
                )
            }

            // Share with Photo to AI Dialog (Camera / Gallery / Gemini / ChatGPT)
            uiState.selectedPromptForAiShare?.let { prompt ->
                ShareToAiDialog(
                    prompt = prompt,
                    language = uiState.language,
                    onDismiss = { viewModel.processIntent(PromptIntent.CloseAiShareDialog) },
                    onLaunched = { message: String ->
                        scope.launch {
                            snackbarHostState.showSnackbar(message)
                        }
                    }
                )
            }
        }
    }
}
