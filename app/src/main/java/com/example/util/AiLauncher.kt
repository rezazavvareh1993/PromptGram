package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.model.PromptItem
import java.io.File
import java.io.FileOutputStream
import java.net.URLEncoder

object AiLauncher {

    const val PACKAGE_GEMINI = "com.google.android.apps.bard"
    const val PACKAGE_CHATGPT = "com.openai.chatgpt"

    fun copyToClipboard(context: Context, label: String, text: String) {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(label, text)
            clipboard.setPrimaryClip(clip)
        } catch (_: Exception) {}
    }

    /**
     * Saves a camera-captured Bitmap into a FileProvider-hosted cache file.
     */
    fun saveBitmapToShareableUri(context: Context, bitmap: Bitmap): Uri? {
        return try {
            val cacheFolder = File(context.cacheDir, "camera_photos").apply { mkdirs() }
            val photoFile = File(cacheFolder, "ai_attached_${System.currentTimeMillis()}.jpg")
            FileOutputStream(photoFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
            }
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Converts any external URI (e.g., from Android PhotoPicker) into a FileProvider URI
     * hosted by OUR app so third-party apps (ChatGPT, Gemini) can read it without Permission Denial.
     */
    fun ensureShareableFileProviderUri(context: Context, sourceUri: Uri): Uri {
        // If it's already from our own FileProvider, return as is
        if (sourceUri.authority == "${context.packageName}.fileprovider") {
            return sourceUri
        }
        return try {
            val cacheFolder = File(context.cacheDir, "shared_images").apply { mkdirs() }
            val destFile = File(cacheFolder, "shareable_photo_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", destFile)
        } catch (e: Exception) {
            sourceUri
        }
    }

    /**
     * Copies the prompt to clipboard and shares both prompt text & attached image
     * to the user's AI app of choice (Gemini, ChatGPT, or system chooser).
     * Returns true if directly opened target package, false if opened system chooser.
     */
    fun sharePromptWithOptionalImage(
        context: Context,
        promptText: String,
        promptTitle: String,
        imageUri: Uri?,
        targetPackage: String?,
        isPersian: Boolean
    ): Boolean {
        // 1. ALWAYS copy prompt to system clipboard first
        copyToClipboard(context, "AI Prompt: $promptTitle", promptText)

        // 2. Prepare safe FileProvider URI if image exists
        val shareableImageUri = if (imageUri != null) {
            ensureShareableFileProviderUri(context, imageUri)
        } else null

        // 3. Build ACTION_SEND intent
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            if (shareableImageUri != null) {
                type = "image/jpeg"
                putExtra(Intent.EXTRA_STREAM, shareableImageUri)
                putExtra(Intent.EXTRA_TEXT, promptText)
                putExtra(Intent.EXTRA_SUBJECT, promptTitle)
                clipData = ClipData.newUri(context.contentResolver, promptTitle, shareableImageUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } else {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, promptText)
                putExtra(Intent.EXTRA_SUBJECT, promptTitle)
            }
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        // 4. Try direct package delivery if requested
        if (targetPackage != null) {
            try {
                val pm = context.packageManager
                val resolveList = pm.queryIntentActivities(sendIntent, PackageManager.MATCH_DEFAULT_ONLY)
                    .ifEmpty { pm.queryIntentActivities(sendIntent, 0) }

                val targetResolve = resolveList.firstOrNull {
                    it.activityInfo.packageName.equals(targetPackage, ignoreCase = true)
                }

                if (targetResolve != null) {
                    sendIntent.setClassName(targetResolve.activityInfo.packageName, targetResolve.activityInfo.name)
                    if (shareableImageUri != null) {
                        try {
                            context.grantUriPermission(
                                targetResolve.activityInfo.packageName,
                                shareableImageUri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                            )
                        } catch (_: Exception) {}
                    }
                    context.startActivity(sendIntent)
                    return true
                }
            } catch (e: Exception) {
                // If package launch fails, fall through to chooser
            }
        }

        // 5. Open System Share Chooser (shows all installed AI apps, messengers, etc.)
        val chooserTitle = if (isPersian) "انتخاب نرم‌افزار هوش مصنوعی (ChatGPT, Gemini و...)" else "Choose AI App (ChatGPT, Gemini, etc.)"
        val chooserIntent = Intent.createChooser(sendIntent, chooserTitle).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (shareableImageUri != null) {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }

        // Grant URI permission to all potential receivers in chooser
        if (shareableImageUri != null) {
            try {
                val resInfoList = context.packageManager.queryIntentActivities(chooserIntent, PackageManager.MATCH_DEFAULT_ONLY)
                for (resolveInfo in resInfoList) {
                    val packageName = resolveInfo.activityInfo.packageName
                    context.grantUriPermission(packageName, shareableImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            } catch (_: Exception) {}
        }

        context.startActivity(chooserIntent)
        return false
    }

    fun openInGemini(context: Context, prompt: PromptItem) {
        sharePromptWithOptionalImage(
            context = context,
            promptText = prompt.promptText,
            promptTitle = prompt.title,
            imageUri = null,
            targetPackage = PACKAGE_GEMINI,
            isPersian = false
        )
    }

    fun openInChatGPT(context: Context, prompt: PromptItem) {
        sharePromptWithOptionalImage(
            context = context,
            promptText = prompt.promptText,
            promptTitle = prompt.title,
            imageUri = null,
            targetPackage = PACKAGE_CHATGPT,
            isPersian = false
        )
    }

    fun sharePromptAsText(context: Context, prompt: PromptItem, isPersian: Boolean) {
        sharePromptWithOptionalImage(
            context = context,
            promptText = prompt.promptText,
            promptTitle = prompt.displayTitle(isPersian),
            imageUri = null,
            targetPackage = null,
            isPersian = isPersian
        )
    }

    fun generateEnhancedPrompt(topic: String, era: String): String {
        return "An ultra-detailed cinematic 35mm photograph of $topic in $era, featuring emotional candid expressions, authentic period-accurate styling and vintage accessories, golden hour sunlight with Kodachrome 64 analog grain, Hasselblad depth of field, hyper-detailed photorealistic masterwork --ar 9:16 --style raw --v 6.1"
    }
}
