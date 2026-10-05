package com.example.ui.screens

import android.content.Context
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PromptItem
import com.example.ui.mvi.AiAppTarget
import com.example.ui.mvi.AppLanguage
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.SunsetAmber
import com.example.util.LocaleStrings
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudioScreen(
    language: AppLanguage,
    onCopyPrompt: (PromptItem) -> Unit,
    onSaveToCollection: (
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
    onOpenWallpaper: (PromptItem) -> Unit,
    onOpenInAi: (PromptItem, AiAppTarget) -> Unit,
    onShareText: (PromptItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val isPersian = language == AppLanguage.FA
    var selectedTab by remember { mutableIntStateOf(0) }
    var isCopied by remember { mutableStateOf(false) }

    LaunchedEffect(isCopied) {
        if (isCopied) {
            delay(2000)
            isCopied = false
        }
    }

    // Mode 1: 1970s Iran
    var iranDecade by remember { mutableStateOf(if (isPersian) "تهران ۱۳۵۳" else "1974 Tehran") }
    var iranSubject by remember { mutableStateOf(if (isPersian) "زوج جوان و شیک‌پوش در حال خنده" else "Fashionable young couple laughing") }
    var iranLocation by remember { mutableStateOf(if (isPersian) "کافه روباز خیابان ولیعصر تهران" else "Outdoor cafe on Valiasr Avenue") }
    var iranStyle by remember { mutableStateOf(if (isPersian) "نگاتیو کداکروم ۶۴ آنالوگ" else "Kodachrome 64 analog film") }

    // Mode 2: Parents & Heritage
    var parentsSubject by remember { mutableStateOf(if (isPersian) "جوانی پدر و مادر در اوایل دهه بیست زندگی" else "Young parents in their early 20s") }
    var parentsEra by remember { mutableStateOf(if (isPersian) "نوستالژی دهه ۶۰ و رنگ‌های گرم" else "1980s retro analog") }
    var parentsSetting by remember { mutableStateOf(if (isPersian) "میز کافه دنج با نور ملایم" else "Cozy vintage cafe table") }

    // Mode 3: Partner & Wallpaper 9:16
    var partnerMood by remember { mutableStateOf(if (isPersian) "شب بارانی با انعکاس نور نئونی و چتر شیشه‌ای" else "Rainy neon city night with umbrella") }
    var partnerSetting by remember { mutableStateOf(if (isPersian) "آسفالت خیس و بازتاب‌های بنفش و کهربایی" else "Under streetlamp with vibrant neon reflections") }

    val currentGeneratedPrompt by remember(selectedTab, iranDecade, iranSubject, iranLocation, iranStyle, parentsSubject, parentsEra, parentsSetting, partnerMood, partnerSetting, isPersian) {
        derivedStateOf {
            when (selectedTab) {
                0 -> {
                    val promptText = "A candid nostalgic 35mm film photograph from 1974 Tehran, Iran, featuring a fashionable young Iranian couple in vintage 70s clothing at an outdoor cafe on Valiasr Avenue. Wearing bell-bottom jeans, vintage sunglasses, retro Paykan car in background, golden hour sunlight, authentic Kodachrome 64 grain, emotional candid laughter --ar 4:5 --style raw"
                    PromptItem(
                        id = 901,
                        title = "1970s Iran: $iranSubject",
                        titleFa = "ایران دهه ۵۰: $iranSubject",
                        category = "1970s & Vintage Iran",
                        categoryFa = "دهه ۵۰ و نوستالژی ایران",
                        descriptionFa = "پرامپت ساخته شده در استودیو برای بازسازی عکس‌های نوستالژیک دهه پنجاه در $iranLocation",
                        platformTrend = "PromptGram Generator",
                        promptText = promptText,
                        negativePrompt = "modern smartphones, modern cars, digital plastic skin, 3d render, extra limbs, bad anatomy",
                        aspectRatio = "4:5 (Instagram Portrait)",
                        modelTips = "Designed for Midjourney v6.1 or Flux.1 Dev. Highly authentic 1970s Iranian nostalgic aesthetic.",
                        styleTags = listOf("1970s Iran", "Tehran", "Vintage", "Kodachrome", "Paykan", "Nostalgia"),
                        photoThemeType = "IRAN_70S",
                        isCustom = true
                    )
                }
                1 -> {
                    val promptText = "A deeply emotional and nostalgic 1980s retro photograph of young parents in their early 20s, enjoying cozy vintage cafe table. Heartfelt loving gaze, soft analog film tones, subtle lens flare, vintage clothing textures, timeless family heritage tribute --ar 9:16"
                    PromptItem(
                        id = 902,
                        title = "Heritage Tribute: $parentsSubject",
                        titleFa = "اصالت خانوادگی: $parentsSubject",
                        category = "Parents & Heritage",
                        categoryFa = "والدین و اصالت خانوادگی",
                        descriptionFa = "تولید تصویر نوستالژیک به یاد جوانی پدر و مادر با تم $parentsEra",
                        platformTrend = "PromptGram Generator",
                        promptText = promptText,
                        negativePrompt = "blurry, low quality, deformed hands, cartoonish, oversaturated, harsh digital lighting",
                        aspectRatio = "9:16 (Phone Wallpaper)",
                        modelTips = "High-emotion portrait generator. Great for honoring parents' youth on mobile wallpapers.",
                        styleTags = listOf("Parents", "Vintage", "Heritage", "1980s", "Warm Nostalgia", "Wallpaper"),
                        photoThemeType = "PARENTS_80S",
                        isCustom = true
                    )
                }
                else -> {
                    val promptText = "Ultra-detailed 9:16 smartphone wallpaper of a romantic couple in a $partnerMood, under streetlamp with vibrant neon reflections. Deep blacks for OLED screen, 8k resolution, romantic aesthetic atmosphere --ar 9:16"
                    PromptItem(
                        id = 903,
                        title = "Partner Wallpaper: $partnerMood",
                        titleFa = "والپیپر دونفره: $partnerMood",
                        category = "Mobile Wallpapers (9:16)",
                        categoryFa = "تصاویر زمینه گوشی (۹:۱۶)",
                        descriptionFa = "والپیپر عمودی ۹:۱۶ مناسب صفحه قفل موبایل با تم رمانتیک",
                        platformTrend = "PromptGram Generator",
                        promptText = promptText,
                        negativePrompt = "daylight, dry, flat colors, low resolution, bad anatomy, deformed fingers",
                        aspectRatio = "9:16 (Phone Wallpaper)",
                        modelTips = "Vertical 9:16 lockscreen format with space left on top for system clock.",
                        styleTags = listOf("Partner", "Couple", "9:16 Wallpaper", "Lockscreen", "Atmospheric"),
                        photoThemeType = if (partnerMood.contains("galaxy", ignoreCase = true) || partnerMood.contains("ستاره")) "COUPLE_STARRY" else "PARTNER_WALLPAPER",
                        isCustom = true
                    )
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("studio_screen")
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = SunsetAmber,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = if (isPersian) "استودیو خلق ایده و پرامپت" else "Prompt Idea Studio",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isPersian) "شخصی‌سازی پرامپت‌های دهه ۵۰ ایران، عکس جوانی والدین و والپیپرهای دونفره" else "Tailor nostalgic 70s Iran memories, parent tributes & partner wallpapers",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mode Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.testTag("studio_tabs")
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text(if (isPersian) "دهه ۵۰ ایران" else "70s Iran Era", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                icon = { Icon(Icons.Default.HistoryEdu, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text(if (isPersian) "یادبود والدین" else "Parents Tribute", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                icon = { Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text(if (isPersian) "والپیپر ۹:۱۶" else "Partner 9:16", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                icon = { Icon(Icons.Default.PhoneAndroid, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Controls
        when (selectedTab) {
            0 -> {
                // 1970s Iran Controls
                StudioOptionSection(
                    title = if (isPersian) "دوره زمانی و شهر" else "Decade & Setting",
                    options = if (isPersian)
                        listOf("تهران ۱۳۵۳", "تابستان خزر ۱۳۵۵", "دانشگاه تهران ۱۳۵۲", "اصفهان ۱۳۵۴", "شیراز ۱۳۵۷")
                    else
                        listOf("1974 Tehran", "1976 Caspian Summer", "1973 Tehran University", "1975 Isfahan", "1978 Shiraz"),
                    selectedOption = iranDecade,
                    onSelect = { iranDecade = it }
                )

                StudioOptionSection(
                    title = if (isPersian) "سوژه تصویر" else "Subject",
                    options = if (isPersian)
                        listOf(
                            "زوج جوان و شیک‌پوش در حال خنده",
                            "دختر جوان با مدل مو و استایل دهه ۵۰",
                            "پسر جوان با شلوار دمپا و کاپشن چرم",
                            "دوستان در سفر جاده چالوس با پیکان",
                            "دورهمی خانوادگی دور حوض فیروزه‌ای"
                        )
                    else
                        listOf(
                            "Fashionable young couple laughing",
                            "Young Iranian woman in 70s chic style",
                            "Young stylish man with flare trousers",
                            "Friends on seaside holiday",
                            "Family gathering around pool (howz)"
                        ),
                    selectedOption = iranSubject,
                    onSelect = { iranSubject = it }
                )

                StudioOptionSection(
                    title = if (isPersian) "موقعیت مکانی" else "Location & Background",
                    options = if (isPersian)
                        listOf(
                            "کافه روباز خیابان ولیعصر تهران",
                            "حیاط قاجاری با حوض و درخت انار",
                            "ساحل خزر با پیکان جوانان کلاسیک",
                            "دانشگاه تهران زیر درختان چنار",
                            "راسته بازار سنتی زیر طاق‌های آجری"
                        )
                    else
                        listOf(
                            "Outdoor cafe on Valiasr Avenue",
                            "Courtyard house with turquoise pool and pomegranates",
                            "Caspian Sea coastline with vintage Paykan car",
                            "Tehran University under plane trees",
                            "Grand Bazaar vintage arcade"
                        ),
                    selectedOption = iranLocation,
                    onSelect = { iranLocation = it }
                )

                StudioOptionSection(
                    title = if (isPersian) "نوع نگاتیو و دوربین آنالوگ" else "Film & Analog Camera",
                    options = if (isPersian)
                        listOf(
                            "نگاتیو کداکروم ۶۴ آنالوگ",
                            "دوربین ۳۵ میلی‌متری لایکا",
                            "عکس پولاروید فوری دهه ۵۰",
                            "نگاتیو اکتاکروم با رنگ‌های زنده"
                        )
                    else
                        listOf(
                            "Kodachrome 64 analog film",
                            "Warm 35mm Leica M3 candid",
                            "Vintage Polaroid flash look",
                            "Ektachrome 1970s vibrant film"
                        ),
                    selectedOption = iranStyle,
                    onSelect = { iranStyle = it }
                )
            }
            1 -> {
                StudioOptionSection(
                    title = if (isPersian) "سوژه یادبود" else "Subject",
                    options = if (isPersian)
                        listOf(
                            "جوانی پدر و مادر در اوایل دهه بیست زندگی",
                            "مادر جوان با لبخندی مهربان",
                            "پدر جوان با نگاهی گرم و امیدوار",
                            "پدر و مادر سالمند در حال قدم زدن در غروب",
                            "پدر و مادر در روز ازدواجشان در دهه پنجاه"
                        )
                    else
                        listOf(
                            "Young parents in their early 20s",
                            "Young mother with gentle smile",
                            "Young father with warm kind eyes",
                            "Elderly parents holding hands at sunset",
                            "Parents on their wedding day in the 70s"
                        ),
                    selectedOption = parentsSubject,
                    onSelect = { parentsSubject = it }
                )

                StudioOptionSection(
                    title = if (isPersian) "سبک و حس نوستالژی" else "Vintage Era & Aesthetic",
                    options = if (isPersian)
                        listOf(
                            "نوستالژی دهه ۶۰ و رنگ‌های گرم",
                            "فیلم نگاتیو دهه ۵۰ با نور طلایی",
                            "غروب خورشید سینمایی",
                            "پرتره کلاسیک آتلیه‌ای سیاه و سفید"
                        )
                    else
                        listOf(
                            "1980s retro analog",
                            "1970s Kodachrome warm film",
                            "Golden hour cinematic memory",
                            "Classic studio black and white portrait"
                        ),
                    selectedOption = parentsEra,
                    onSelect = { parentsEra = it }
                )

                StudioOptionSection(
                    title = if (isPersian) "محیط و پس‌زمینه" else "Setting & Backdrop",
                    options = if (isPersian)
                        listOf(
                            "میز کافه دنج با نور ملایم",
                            "دشت گندم طلایی در غروب خورشید",
                            "پارک پاییزی زیر برگ‌های چنار",
                            "اتاق نشیمن با گرامافون و کتاب"
                        )
                    else
                        listOf(
                            "Cozy vintage cafe table",
                            "Golden wheat field under sunset sky",
                            "Autumn park under golden maple trees",
                            "Living room with retro vinyl and books"
                        ),
                    selectedOption = parentsSetting,
                    onSelect = { parentsSetting = it }
                )
            }
            2 -> {
                StudioOptionSection(
                    title = if (isPersian) "حال و هوا و تم" else "Mood & Concept",
                    options = if (isPersian)
                        listOf(
                            "شب بارانی با انعکاس نور نئونی و چتر شیشه‌ای",
                            "تماشای راه شیری و کهکشان بالای صخره",
                            "ساحل غروب خورشید و خنده دو نفره",
                            "بام شهر سایبرپانک با آسمان خراش‌های نئونی",
                            "قرار صبحگاهی کافه در نور ملایم آفتاب"
                        )
                    else
                        listOf(
                            "Rainy neon city night with umbrella",
                            "Starry celestial Milky Way on cliff",
                            "Sunset beach silhouette laughing",
                            "Cyberpunk neo-Tokyo observation deck",
                            "Cozy morning coffee date in warm sun"
                        ),
                    selectedOption = partnerMood,
                    onSelect = { partnerMood = it }
                )

                StudioOptionSection(
                    title = if (isPersian) "نورپردازی و اتمسفر" else "Lighting & Atmosphere",
                    options = if (isPersian)
                        listOf(
                            "آسفالت خیس و بازتاب‌های بنفش و کهربایی",
                            "درخشش کهکشانی با کرم‌های شب‌تاب",
                            "نور ضد نور خورشید با هاله گرم",
                            "نور ملایم پنجره و بافت‌های کتان"
                        )
                    else
                        listOf(
                            "Wet street reflections and warm neon bokeh",
                            "Ethereal cosmic glow with fireflies",
                            "Golden backlight with warm lens flare",
                            "Soft window light and linen textures"
                        ),
                    selectedOption = partnerSetting,
                    onSelect = { partnerSetting = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Synthesized Result Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("generated_prompt_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(SunsetAmber, RoundedCornerShape(5.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isPersian) "پرامپت ساخته شده آماده استفاده" else "Synthesized AI Prompt",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = currentGeneratedPrompt.aspectRatio,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            onCopyPrompt(currentGeneratedPrompt)
                            isCopied = true
                        }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = currentGeneratedPrompt.promptText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isCopied) (if (isPersian) "کپی شد! ✓" else "Copied! ✓") else (if (isPersian) "برای کپی ضربه بزنید 📋" else "Tap to copy 📋"),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isCopied) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions: Copy, Open in Gemini / ChatGPT, Share, Save
                Button(
                    onClick = {
                        onCopyPrompt(currentGeneratedPrompt)
                        isCopied = true
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCopied) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("studio_copy_prompt_button")
                ) {
                    Icon(
                        imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isCopied) (if (isPersian) "کپی شد! ✓" else "Copied! ✓") else LocaleStrings.copyPrompt(language),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Open in Gemini / ChatGPT direct shortcuts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = { onOpenInAi(currentGeneratedPrompt, AiAppTarget.GEMINI) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Gemini", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    FilledTonalButton(
                        onClick = { onOpenInAi(currentGeneratedPrompt, AiAppTarget.CHATGPT) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ChatGPT", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onShareText(currentGeneratedPrompt) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isPersian) "ارسال" else "Share", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onSaveToCollection(
                                currentGeneratedPrompt.title,
                                currentGeneratedPrompt.titleFa,
                                currentGeneratedPrompt.category,
                                currentGeneratedPrompt.promptText,
                                currentGeneratedPrompt.negativePrompt,
                                currentGeneratedPrompt.aspectRatio,
                                currentGeneratedPrompt.modelTips,
                                currentGeneratedPrompt.styleTags,
                                currentGeneratedPrompt.photoThemeType
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("studio_save_collection_button")
                    ) {
                        Icon(imageVector = Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isPersian) "ذخیره در کالکشن" else "Save Prompt", fontSize = 13.sp)
                    }

                    Button(
                        onClick = { onOpenWallpaper(currentGeneratedPrompt) },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SunsetAmber),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("studio_view_wallpaper_button")
                    ) {
                        Icon(imageVector = Icons.Default.Wallpaper, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isPersian) "مشاهده والپیپر" else "Live Preview", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StudioOptionSection(
    title: String,
    options: List<String>,
    selectedOption: String,
    onSelect: (String) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                val isSelected = option == selectedOption
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelect(option) },
                    label = { Text(option, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricViolet,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }
    }
}
