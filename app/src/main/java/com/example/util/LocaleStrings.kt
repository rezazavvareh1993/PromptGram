package com.example.util

import com.example.ui.mvi.AppLanguage

object LocaleStrings {

    fun appName(lang: AppLanguage): String = when (lang) {
        AppLanguage.FA -> "پرامپت‌گرام"
        AppLanguage.EN -> "PromptGram"
    }

    fun appTagline(lang: AppLanguage): String = when (lang) {
        AppLanguage.FA -> "مرجع پرامپت‌های هوش مصنوعی و والپیپرهای ترند"
        AppLanguage.EN -> "Trending Social Media AI Photo Prompts & Wallpapers"
    }

    fun navExplore(lang: AppLanguage): String = when (lang) {
        AppLanguage.FA -> "کاوش"
        AppLanguage.EN -> "Explore"
    }

    fun navStudio(lang: AppLanguage): String = when (lang) {
        AppLanguage.FA -> "استودیو ایده"
        AppLanguage.EN -> "Idea Studio"
    }

    fun navSaved(lang: AppLanguage): String = when (lang) {
        AppLanguage.FA -> "نشان‌شده‌ها"
        AppLanguage.EN -> "Saved"
    }

    fun searchPlaceholder(lang: AppLanguage): String = when (lang) {
        AppLanguage.FA -> "جستجوی پرامپت (دهه ۵۰ ایران، والدین، والپیپر...)"
        AppLanguage.EN -> "Search prompts (70s Iran, parents, wallpaper...)"
    }

    fun copyPrompt(lang: AppLanguage): String = when (lang) {
        AppLanguage.FA -> "کپی پرامپت"
        AppLanguage.EN -> "Copy Prompt"
    }

    fun saveToGallery(lang: AppLanguage): String = when (lang) {
        AppLanguage.FA -> "ذخیره در گالری"
        AppLanguage.EN -> "Save to Gallery"
    }

    fun setWallpaper(lang: AppLanguage): String = when (lang) {
        AppLanguage.FA -> "تنظیم والپیپر"
        AppLanguage.EN -> "Set Wallpaper"
    }

    fun sharePrompt(lang: AppLanguage): String = when (lang) {
        AppLanguage.FA -> "اشتراک‌گذاری متن"
        AppLanguage.EN -> "Share Prompt"
    }

    fun openInAi(lang: AppLanguage): String = when (lang) {
        AppLanguage.FA -> "باز کردن در هوش مصنوعی"
        AppLanguage.EN -> "Open in AI"
    }

    fun openInGemini(lang: AppLanguage): String = when (lang) {
        AppLanguage.FA -> "باز کردن در جمینای (Gemini)"
        AppLanguage.EN -> "Open in Gemini"
    }

    fun openInChatGpt(lang: AppLanguage): String = when (lang) {
        AppLanguage.FA -> "باز کردن در چت‌جی‌پی‌تی (ChatGPT)"
        AppLanguage.EN -> "Open in ChatGPT"
    }

    fun aiGenerate(lang: AppLanguage): String = when (lang) {
        AppLanguage.FA -> "گسترش و بهینه‌سازی با هوش مصنوعی"
        AppLanguage.EN -> "Enhance / Generate with AI"
    }

    fun refreshTrends(lang: AppLanguage): String = when (lang) {
        AppLanguage.FA -> "بروزرسانی ترندهای روز"
        AppLanguage.EN -> "Update Trending Ideas"
    }

    fun heroBannerTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.FA -> "🔥 ترندهای داغ اینستاگرام و شبکه‌های اجتماعی"
        AppLanguage.EN -> "🔥 Social Media Trending Prompts"
    }

    fun heroBannerSubtitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.FA -> "عکس‌های نوستالژیک دهه ۵۰ ایران، خاطرات جوانی والدین و والپیپرهای عاشقانه ۹:۱۶"
        AppLanguage.EN -> "Vintage 70s Iran memories, parents' young years, and romantic 9:16 mobile wallpapers."
    }

    fun heroBannerAction(lang: AppLanguage): String = when (lang) {
        AppLanguage.FA -> "ورود به استودیو و شخصی‌سازی پرامپت ←"
        AppLanguage.EN -> "Customize in Idea Studio →"
    }

    fun categoryAll(lang: AppLanguage): String = when (lang) {
        AppLanguage.FA -> "همه"
        AppLanguage.EN -> "All"
    }

    fun categoryIran(lang: AppLanguage): String = when (lang) {
        AppLanguage.FA -> "دهه ۵۰ و نوستالژی ایران"
        AppLanguage.EN -> "1970s & Vintage Iran"
    }

    fun categoryParents(lang: AppLanguage): String = when (lang) {
        AppLanguage.FA -> "والدین و اصالت خانوادگی"
        AppLanguage.EN -> "Parents & Heritage"
    }

    fun categoryPartner(lang: AppLanguage): String = when (lang) {
        AppLanguage.FA -> "پارتنر و عاشقانه"
        AppLanguage.EN -> "Partner & Romance"
    }

    fun categoryWallpapers(lang: AppLanguage): String = when (lang) {
        AppLanguage.FA -> "تصاویر زمینه گوشی (۹:۱۶)"
        AppLanguage.EN -> "Mobile Wallpapers (9:16)"
    }

    fun lockscreenMode(lang: AppLanguage): String = when (lang) {
        AppLanguage.FA -> "پیش‌نمایش قفل صفحه"
        AppLanguage.EN -> "Lockscreen View"
    }

    fun cleanImageMode(lang: AppLanguage): String = when (lang) {
        AppLanguage.FA -> "تصویر بدون ویجت"
        AppLanguage.EN -> "Clean Image"
    }

    fun copiedSuccess(lang: AppLanguage): String = when (lang) {
        AppLanguage.FA -> "پرامپت با موفقیت کپی شد! آماده برای استفاده در هوش مصنوعی 📋"
        AppLanguage.EN -> "Prompt copied to clipboard! Ready to paste in AI 📋"
    }

    fun savedSuccess(lang: AppLanguage): String = when (lang) {
        AppLanguage.FA -> "تصویر در گالری ذخیره شد! پوشه Pictures/PromptGram 🖼️"
        AppLanguage.EN -> "Saved to Gallery! Check Pictures/PromptGram 🖼️"
    }

    fun wallpaperApplied(lang: AppLanguage): String = when (lang) {
        AppLanguage.FA -> "تصویر زمینه با موفقیت تنظیم شد! ✨"
        AppLanguage.EN -> "Wallpaper applied successfully! ✨"
    }

    fun emptyStateTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.FA -> "موردی یافت نشد"
        AppLanguage.EN -> "No prompts found"
    }

    fun emptyStateDesc(lang: AppLanguage): String = when (lang) {
        AppLanguage.FA -> "برای مشاهده نتایج، کلمات 'ایران', 'والدین', یا 'والپیپر' را جستجو کنید."
        AppLanguage.EN -> "Try searching for 'Iran', 'parents', or 'wallpaper'."
    }
}
