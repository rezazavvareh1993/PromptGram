package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.DefaultPrompts
import com.example.util.WallpaperRenderer
import kotlinx.coroutines.flow.first
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("PromptGram", appName)
  }

  @Test
  fun `verify default prompts contain 1970s Iran and wallpapers`() {
    val prompts = DefaultPrompts.getInitialPrompts()
    assertTrue(prompts.isNotEmpty())
    val hasIran70s = prompts.any { it.category.contains("Iran") }
    val hasParents = prompts.any { it.category.contains("Parents") }
    val hasWallpapers = prompts.any { it.aspectRatio.contains("9:16") }
    assertTrue("Should include 1970s Iran prompts", hasIran70s)
    assertTrue("Should include Parents nostalgia prompts", hasParents)
    assertTrue("Should include 9:16 wallpaper prompts", hasWallpapers)
  }

  @Test
  fun `verify wallpaper renderer produces valid bitmap`() {
    val samplePrompt = DefaultPrompts.getInitialPrompts().first().toDomain()
    val bitmap = WallpaperRenderer.renderPromptWallpaper(samplePrompt, 360, 640)
    assertNotNull(bitmap)
    assertEquals(360, bitmap.width)
    assertEquals(640, bitmap.height)
  }

  @Test
  fun `verify bilingual support and fresh suggestions`() {
    val prompts = DefaultPrompts.getInitialPrompts()
    val hasPersian = prompts.any { it.titleFa.isNotBlank() && it.categoryFa.isNotBlank() }
    assertTrue("Default prompts must support Persian language", hasPersian)

    val freshSuggestions = DefaultPrompts.getFreshTrendingSuggestions()
    assertTrue("Fresh trending suggestions must not be empty", freshSuggestions.isNotEmpty())

    val persianName = com.example.util.LocaleStrings.appName(com.example.ui.mvi.AppLanguage.FA)
    assertEquals("پرامپت‌گرام", persianName)
  }

  @Test
  fun `verify Room local storage system for saving favorite prompts`() = kotlinx.coroutines.runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(
      context,
      com.example.data.local.PromptDatabase::class.java
    ).build()
    val dao = db.promptDao()

    val testPrompt = com.example.data.local.PromptEntity(
      id = 999,
      title = "Vintage 70s Tehran Sunset",
      titleFa = "غروب نوستالژیک دهه پنجاه تهران",
      category = "1970s & Vintage Iran",
      categoryFa = "دهه ۵۰ و نوستالژی ایران",
      descriptionFa = "عکس نوستالژیک غروب",
      platformTrend = "Instagram Trend",
      promptText = "A 35mm photograph of Tehran 1974 sunset",
      negativePrompt = "blurry, low quality",
      aspectRatio = "9:16 (Phone Wallpaper)",
      modelTips = "Midjourney v6",
      styleTags = "1970s, Tehran",
      photoThemeType = "IRAN_70S",
      isFavorite = false,
      isCustom = false,
      copyCount = 0,
      createdAt = System.currentTimeMillis()
    )

    dao.insertPrompt(testPrompt)

    // Save prompt to favorites in Room
    dao.updateFavorite(testPrompt.id, true)

    // Retrieve favorite prompts from Room
    val favorites = dao.getFavoritePrompts().first()
    assertTrue("Favorites must contain the saved prompt", favorites.any { it.id == 999L && it.isFavorite })

    // Unfavorite prompt
    dao.updateFavorite(testPrompt.id, false)
    val updatedFavorites = dao.getFavoritePrompts().first()
    assertTrue("Favorites must no longer contain the prompt", updatedFavorites.none { it.id == 999L })

    db.close()
  }

  @Test
  fun `verify category tab system and MVI state switching`() = kotlinx.coroutines.runBlocking {
    val categories = com.example.ui.mvi.PromptCategory.entries
    assertEquals(5, categories.size)
    assertTrue(categories.contains(com.example.ui.mvi.PromptCategory.VINTAGE))
    assertTrue(categories.contains(com.example.ui.mvi.PromptCategory.ARTISTIC))
    assertTrue(categories.contains(com.example.ui.mvi.PromptCategory.PORTRAIT))
    assertTrue(categories.contains(com.example.ui.mvi.PromptCategory.WALLPAPER))

    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(
      context,
      com.example.data.local.PromptDatabase::class.java
    ).build()
    db.promptDao().insertAll(DefaultPrompts.getInitialPrompts())
    val repository = com.example.data.repository.PromptRepository(db.promptDao())
    val viewModel = com.example.ui.mvi.PromptMviViewModel(repository)

    // Select VINTAGE tab
    viewModel.processIntent(com.example.ui.mvi.PromptIntent.SelectCategoryTab(com.example.ui.mvi.PromptCategory.VINTAGE))
    assertEquals(com.example.ui.mvi.PromptCategory.VINTAGE, viewModel.uiState.value.selectedCategoryTab)

    // Select WALLPAPER tab
    viewModel.processIntent(com.example.ui.mvi.PromptIntent.SelectCategoryTab(com.example.ui.mvi.PromptCategory.WALLPAPER))
    assertEquals(com.example.ui.mvi.PromptCategory.WALLPAPER, viewModel.uiState.value.selectedCategoryTab)

    db.close()
  }

  @Test
  fun `verify copy to clipboard feature on prompt card`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val testPromptText = "A nostalgic 1974 Tehran photograph with 35mm Kodachrome grain"
    com.example.util.AiLauncher.copyToClipboard(context, "Test Prompt", testPromptText)

    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
    assertNotNull(clipboard.primaryClip)
    val copiedText = clipboard.primaryClip?.getItemAt(0)?.text?.toString()
    assertEquals(testPromptText, copiedText)
  }

  @Test
  fun `verify share to AI with photo and auto clipboard copy`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val testPromptText = "A 1970s Tehran Paykan retro photograph"
    val testTitle = "Tehran 1974"

    val sampleBitmap = android.graphics.Bitmap.createBitmap(100, 100, android.graphics.Bitmap.Config.ARGB_8888)
    val shareableUri = com.example.util.AiLauncher.saveBitmapToShareableUri(context, sampleBitmap)
    assertNotNull(shareableUri)

    com.example.util.AiLauncher.sharePromptWithOptionalImage(
      context = context,
      promptText = testPromptText,
      promptTitle = testTitle,
      imageUri = shareableUri,
      targetPackage = null,
      isPersian = true
    )

    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
    assertNotNull(clipboard.primaryClip)
    val copiedText = clipboard.primaryClip?.getItemAt(0)?.text?.toString()
    assertEquals(testPromptText, copiedText)
  }
}
