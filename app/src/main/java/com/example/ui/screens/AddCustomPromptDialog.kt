package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.mvi.AppLanguage

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddCustomPromptDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        titleFa: String,
        category: String,
        promptText: String,
        negativePrompt: String,
        aspectRatio: String,
        modelTips: String,
        styleTags: List<String>,
        photoThemeType: String
    ) -> Unit
) {
    val isPersian = language == AppLanguage.FA

    var title by remember { mutableStateOf("") }
    var titleFa by remember { mutableStateOf("") }
    var promptText by remember { mutableStateOf("") }
    var negativePrompt by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("1970s & Vintage Iran") }
    var aspectRatio by remember { mutableStateOf("9:16 (Phone Wallpaper)") }
    var styleTagsText by remember { mutableStateOf("Vintage, Instagram, 35mm") }
    var themeType by remember { mutableStateOf("IRAN_70S") }

    val categories = listOf(
        "1970s & Vintage Iran",
        "Parents & Heritage",
        "Partner & Romance",
        "Mobile Wallpapers (9:16)"
    )

    val aspectRatios = listOf(
        "9:16 (Phone Wallpaper)",
        "4:5 (Instagram Portrait)",
        "1:1 (Square)",
        "16:9 (Landscape)"
    )

    val themes = listOf(
        "IRAN_70S" to (if (isPersian) "دهه ۵۰ ایران" else "70s Iran Vintage"),
        "PARENTS_80S" to (if (isPersian) "والدین دهه ۶۰" else "Parents 80s"),
        "PARTNER_WALLPAPER" to (if (isPersian) "شب بارانی نئونی" else "Rainy Neon"),
        "COUPLE_STARRY" to (if (isPersian) "کهکشان و ستاره‌ها" else "Starry Galaxy"),
        "PERSIAN_GARDEN" to (if (isPersian) "حیاط سنتی ایرانی" else "Persian Courtyard"),
        "CYBER_NEON" to (if (isPersian) "سایبرپانک نئون" else "Cyber Neon")
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .testTag("add_custom_prompt_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (isPersian) "افزودن پرامپت سفارشی جدید" else "Add Custom Prompt",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = titleFa,
                    onValueChange = { titleFa = it },
                    label = { Text(if (isPersian) "عنوان فارسی (مثلاً: پدر و مادر در خیابان پهلوی سال ۵۴)" else "Persian Title (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (isPersian) "عنوان انگلیسی (English Title)" else "English Title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_title_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = promptText,
                    onValueChange = { promptText = it },
                    label = { Text(if (isPersian) "متن پرامپت هوش مصنوعی (انگلیسی)" else "AI Prompt (English)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .testTag("custom_prompt_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = negativePrompt,
                    onValueChange = { negativePrompt = it },
                    label = { Text(if (isPersian) "پرامپت منفی (اختیاری)" else "Negative Prompt (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isPersian) "دسته‌بندی" else "Category",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = {
                                category = cat
                                if (cat.contains("Iran")) themeType = "IRAN_70S"
                                else if (cat.contains("Parents")) themeType = "PARENTS_80S"
                                else if (cat.contains("Wallpaper")) themeType = "COUPLE_STARRY"
                            },
                            label = { Text(cat) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isPersian) "نسبت تصویر" else "Aspect Ratio",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    aspectRatios.forEach { ar ->
                        FilterChip(
                            selected = aspectRatio == ar,
                            onClick = { aspectRatio = ar },
                            label = { Text(ar) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isPersian) "سبک رندر پیش‌نمایش گرافیکی" else "Visual Aesthetic Preview Style",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    themes.forEach { (type, label) ->
                        FilterChip(
                            selected = themeType == type,
                            onClick = { themeType = type },
                            label = { Text(label) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(if (isPersian) "انصراف" else "Cancel")
                    }
                    Button(
                        onClick = {
                            val finalTitle = title.ifBlank { titleFa }
                            if (finalTitle.isNotBlank() && promptText.isNotBlank()) {
                                val tags = styleTagsText.split(",").map { it.trim() }.filter { it.isNotBlank() }
                                onSave(
                                    finalTitle.trim(),
                                    titleFa.ifBlank { finalTitle },
                                    category,
                                    promptText.trim(),
                                    negativePrompt.trim(),
                                    aspectRatio,
                                    "Custom user prompt",
                                    tags,
                                    themeType
                                )
                                onDismiss()
                            }
                        },
                        enabled = (title.isNotBlank() || titleFa.isNotBlank()) && promptText.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("save_custom_dialog_button")
                    ) {
                        Text(if (isPersian) "ذخیره در کالکشن" else "Save to Collection")
                    }
                }
            }
        }
    }
}
