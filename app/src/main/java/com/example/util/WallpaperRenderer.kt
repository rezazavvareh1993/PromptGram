package com.example.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import com.example.data.model.PromptItem
import kotlin.random.Random

object WallpaperRenderer {

    fun renderPromptWallpaper(
        prompt: PromptItem,
        width: Int = 1080,
        height: Int = 1920,
        filterMode: String = "Normal"
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        when (prompt.photoThemeType) {
            "IRAN_70S" -> renderIran70sTheme(canvas, width, height, prompt)
            "PARENTS_80S" -> renderParents80sTheme(canvas, width, height, prompt)
            "COUPLE_STARRY" -> renderStarryGalaxyTheme(canvas, width, height, prompt)
            "PARTNER_WALLPAPER" -> renderRainyNeonTheme(canvas, width, height, prompt)
            "PERSIAN_GARDEN" -> renderPersianGardenTheme(canvas, width, height, prompt)
            "CYBER_NEON" -> renderCyberNeonTheme(canvas, width, height, prompt)
            else -> renderMinimalStudioTheme(canvas, width, height, prompt)
        }

        // Apply visual filter if requested
        when (filterMode) {
            "Kodachrome Warm" -> applyColorOverlay(canvas, width, height, Color.argb(45, 255, 170, 70))
            "70s Sepia Nostalgia" -> applyColorOverlay(canvas, width, height, Color.argb(60, 160, 110, 60))
            "Cyber Magenta" -> applyColorOverlay(canvas, width, height, Color.argb(40, 255, 30, 140))
            "Midnight Cool" -> applyColorOverlay(canvas, width, height, Color.argb(45, 20, 80, 180))
            "Noir Film" -> applyMonochromeOverlay(canvas, width, height)
        }

        // Apply subtle film grain and vignette for analog aesthetic
        applyVignette(canvas, width, height)
        applyFilmGrain(canvas, width, height)

        return bitmap
    }

    private fun renderIran70sTheme(canvas: Canvas, width: Int, height: Int, prompt: PromptItem) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Warm 70s Kodachrome Sunset Sky gradient
        val skyGradient = LinearGradient(
            0f, 0f, 0f, height * 0.7f,
            intArrayOf(
                Color.rgb(180, 75, 45),   // Warm terracotta top
                Color.rgb(230, 130, 50),  // Deep amber
                Color.rgb(250, 195, 100), // Golden sunshine
                Color.rgb(255, 230, 170)  // Soft yellow haze
            ),
            floatArrayOf(0f, 0.35f, 0.7f, 1f),
            Shader.TileMode.CLAMP
        )
        paint.shader = skyGradient
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        paint.shader = null

        // Large 70s Sun Disk
        val sunPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(200, 255, 245, 210)
        }
        canvas.drawCircle(width * 0.5f, height * 0.42f, width * 0.28f, sunPaint)

        // Sun glow
        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                width * 0.5f, height * 0.42f, width * 0.45f,
                Color.argb(90, 255, 210, 120),
                Color.TRANSPARENT,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(width * 0.5f, height * 0.42f, width * 0.45f, glowPaint)

        // Distant Alborz Mountains silhouette (representing Tehran backdrop)
        val mountainPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(120, 60, 45)
        }
        val mountainPath = Path().apply {
            moveTo(0f, height * 0.60f)
            lineTo(width * 0.15f, height * 0.52f)
            lineTo(width * 0.30f, height * 0.55f)
            lineTo(width * 0.48f, height * 0.47f) // Peak (Damavand inspired)
            lineTo(width * 0.65f, height * 0.54f)
            lineTo(width * 0.85f, height * 0.50f)
            lineTo(width.toFloat(), height * 0.58f)
            lineTo(width.toFloat(), height.toFloat())
            lineTo(0f, height.toFloat())
            close()
        }
        canvas.drawPath(mountainPath, mountainPaint)

        val isCaspian = prompt.id == 2L || prompt.title.contains("Caspian", ignoreCase = true) || prompt.titleFa.contains("شمال")
        if (isCaspian) {
            // Draw Caspian sea coastal waters
            val seaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(30, 65, 80)
            }
            canvas.drawRect(0f, height * 0.60f, width.toFloat(), height * 0.72f, seaPaint)

            // Wave foam lines
            val foamPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(140, 220, 240, 250)
                strokeWidth = 3f
                style = Paint.Style.STROKE
            }
            val wavePath = Path().apply {
                moveTo(0f, height * 0.64f)
                quadTo(width * 0.25f, height * 0.62f, width * 0.5f, height * 0.65f)
                quadTo(width * 0.75f, height * 0.68f, width.toFloat(), height * 0.64f)
            }
            canvas.drawPath(wavePath, foamPaint)

            // Sandy beach
            val sandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(65, 45, 30)
            }
            canvas.drawRect(0f, height * 0.70f, width.toFloat(), height.toFloat(), sandPaint)

            // Vintage Paykan parked on seaside
            val carPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(25, 15, 15)
            }
            val carPath = Path().apply {
                val cx = width * 0.12f
                val cy = height * 0.68f
                moveTo(cx, cy + 70f)
                lineTo(cx + 35f, cy + 25f)
                lineTo(cx + 80f, cy + 25f)
                lineTo(cx + 125f, cy - 20f)
                lineTo(cx + 225f, cy - 20f)
                lineTo(cx + 280f, cy + 25f)
                lineTo(cx + 335f, cy + 30f)
                lineTo(cx + 345f, cy + 70f)
                close()
            }
            canvas.drawPath(carPath, carPaint)
            canvas.drawCircle(width * 0.12f + 80f, height * 0.68f + 70f, 28f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLACK })
            canvas.drawCircle(width * 0.12f + 270f, height * 0.68f + 70f, 28f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLACK })

            // Friends picnic blanket and samovar
            val samovarPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(215, 165, 65)
            }
            canvas.drawRect(width * 0.65f, height * 0.74f, width * 0.71f, height * 0.79f, samovarPaint)
            canvas.drawCircle(width * 0.68f, height * 0.73f, 12f, samovarPaint)

            // Friends sitting around picnic
            val figurePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(25, 15, 15) }
            canvas.drawCircle(width * 0.58f, height * 0.72f, 18f, figurePaint)
            canvas.drawOval(RectF(width * 0.54f, height * 0.74f, width * 0.62f, height * 0.82f), figurePaint)
            canvas.drawCircle(width * 0.78f, height * 0.71f, 20f, figurePaint)
            canvas.drawOval(RectF(width * 0.74f, height * 0.73f, width * 0.82f, height * 0.82f), figurePaint)

            drawVintagePhotoFrame(canvas, width, height, "CASPIAN SEA • 1976", "CHALOUS ROAD & VINTAGE PAYKAN PICNIC")
            return
        }

        // Near ground / Tehran Valiasr plane tree silhouettes
        val groundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(40, 22, 20)
        }
        canvas.drawRect(0f, height * 0.68f, width.toFloat(), height.toFloat(), groundPaint)

        // Draw iconic retro 1970s Paykan car silhouette
        val carPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(25, 15, 15)
        }
        val carPath = Path().apply {
            val cx = width * 0.10f
            val cy = height * 0.72f
            moveTo(cx, cy + 80f)
            lineTo(cx + 40f, cy + 30f)
            lineTo(cx + 90f, cy + 30f)
            lineTo(cx + 140f, cy - 20f)
            lineTo(cx + 250f, cy - 20f)
            lineTo(cx + 310f, cy + 30f)
            lineTo(cx + 370f, cy + 35f)
            lineTo(cx + 380f, cy + 80f)
            close()
        }
        canvas.drawPath(carPath, carPaint)

        // Car wheels
        carPaint.color = Color.rgb(15, 10, 10)
        canvas.drawCircle(width * 0.10f + 90f, height * 0.72f + 80f, 32f, carPaint)
        canvas.drawCircle(width * 0.10f + 300f, height * 0.72f + 80f, 32f, carPaint)

        // Silhouettes of 1970s Iranian couple laughing together (bell-bottoms & retro chic)
        val figurePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(25, 12, 12)
        }
        val fx = width * 0.65f
        val fy = height * 0.68f

        // Woman silhouette
        canvas.drawCircle(fx - 40f, fy - 180f, 24f, figurePaint) // head
        canvas.drawOval(RectF(fx - 62f, fy - 165f, fx - 20f, fy - 70f), figurePaint) // torso
        canvas.drawOval(RectF(fx - 68f, fy - 75f, fx - 16f, fy + 50f), figurePaint) // flared 70s skirt/pants

        // Man silhouette next to her, arm around
        canvas.drawCircle(fx + 25f, fy - 195f, 26f, figurePaint) // head
        canvas.drawOval(RectF(fx, fy - 175f, fx + 50f, fy - 65f), figurePaint) // torso
        canvas.drawOval(RectF(fx - 5f, fy - 70f, fx + 55f, fy + 50f), figurePaint) // flared trousers

        // Plane trees framing right and left
        val treePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(35, 18, 15)
        }
        canvas.drawRect(width * 0.88f, height * 0.35f, width * 0.94f, height.toFloat(), treePaint)
        canvas.drawCircle(width * 0.90f, height * 0.32f, 90f, treePaint)
        canvas.drawCircle(width * 0.84f, height * 0.25f, 75f, treePaint)

        // 1970s Retro Vintage Stamp on Bottom
        drawVintagePhotoFrame(canvas, width, height, "TEHRAN • 1974", "KODACHROME 64 ANALOG FILM")
    }

    private fun renderParents80sTheme(canvas: Canvas, width: Int, height: Int, prompt: PromptItem) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Golden Hour Warm Amber Gradient
        val bgGradient = LinearGradient(
            0f, 0f, 0f, height.toFloat(),
            intArrayOf(
                Color.rgb(75, 30, 45),   // Deep rose burgundy
                Color.rgb(180, 85, 45),  // Warm terracotta
                Color.rgb(235, 160, 60), // Warm golden sunset
                Color.rgb(250, 220, 150) // Golden hour haze
            ),
            floatArrayOf(0f, 0.4f, 0.75f, 1f),
            Shader.TileMode.CLAMP
        )
        paint.shader = bgGradient
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        paint.shader = null

        // Big golden backlight sun
        val sunGlow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                width * 0.5f, height * 0.55f, width * 0.6f,
                Color.argb(160, 255, 235, 160),
                Color.TRANSPARENT,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(width * 0.5f, height * 0.55f, width * 0.6f, sunGlow)

        // Beautiful Golden Bokeh orbs in background
        val bokehPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val rnd = Random(42)
        for (i in 0..25) {
            val bx = rnd.nextFloat() * width
            val by = height * 0.25f + rnd.nextFloat() * height * 0.45f
            val rad = 15f + rnd.nextFloat() * 45f
            val alpha = 30 + rnd.nextInt(70)
            bokehPaint.color = Color.argb(alpha, 255, 240, 180)
            canvas.drawCircle(bx, by, rad, bokehPaint)
        }

        // Grassy hillside / park meadow silhouette
        val groundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(35, 20, 15)
        }
        val hillPath = Path().apply {
            moveTo(0f, height * 0.65f)
            quadTo(width * 0.5f, height * 0.60f, width.toFloat(), height * 0.66f)
            lineTo(width.toFloat(), height.toFloat())
            lineTo(0f, height.toFloat())
            close()
        }
        canvas.drawPath(hillPath, groundPaint)

        // Young Parents standing close together holding hands
        val parentsPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(25, 12, 10)
        }
        val px = width * 0.5f
        val py = height * 0.62f

        // Father silhouette
        canvas.drawCircle(px - 35f, py - 180f, 26f, parentsPaint) // head
        canvas.drawOval(RectF(px - 65f, py - 160f, px - 10f, py - 40f), parentsPaint) // jacket
        canvas.drawRect(px - 55f, py - 45f, px - 20f, py + 70f, parentsPaint) // legs

        // Mother silhouette smiling towards father
        canvas.drawCircle(px + 28f, py - 168f, 24f, parentsPaint) // head
        canvas.drawOval(RectF(px + 5f, py - 150f, px + 55f, py - 45f), parentsPaint) // coat
        canvas.drawOval(RectF(px + 10f, py - 50f, px + 55f, py + 70f), parentsPaint) // long skirt

        // Small heart sparkle / flare between them
        val heartPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(190, 255, 220, 150)
        }
        canvas.drawCircle(px - 2f, py - 85f, 10f, heartPaint)

        drawVintagePhotoFrame(canvas, width, height, "HERITAGE TRIBUTE • 1982", "TIMELESS PARENTS ROMANCE")
    }

    private fun renderStarryGalaxyTheme(canvas: Canvas, width: Int, height: Int, prompt: PromptItem) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Deep Cosmos Gradient
        val skyGradient = LinearGradient(
            0f, 0f, 0f, height.toFloat(),
            intArrayOf(
                Color.rgb(10, 5, 25),    // Deep space
                Color.rgb(25, 12, 55),   // Dark cosmic violet
                Color.rgb(65, 25, 85),   // Magenta nebula glow
                Color.rgb(15, 20, 45)    // Horizon blue
            ),
            floatArrayOf(0f, 0.45f, 0.75f, 1f),
            Shader.TileMode.CLAMP
        )
        paint.shader = skyGradient
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        paint.shader = null

        // Glowing Milky Way Nebula Arc
        val nebulaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                width * 0.4f, height * 0.35f, width * 0.65f,
                intArrayOf(
                    Color.argb(120, 160, 60, 255),
                    Color.argb(70, 70, 180, 255),
                    Color.argb(30, 255, 120, 200),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.4f, 0.7f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(width * 0.4f, height * 0.35f, width * 0.65f, nebulaPaint)

        // Brilliant Galaxy Stars
        val starPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val rnd = Random(777)
        for (i in 0..180) {
            val sx = rnd.nextFloat() * width
            val sy = rnd.nextFloat() * (height * 0.72f)
            val rad = 1.2f + rnd.nextFloat() * 2.8f
            val alpha = 100 + rnd.nextInt(155)
            starPaint.color = Color.argb(alpha, 240 + rnd.nextInt(15), 240 + rnd.nextInt(15), 255)
            canvas.drawCircle(sx, sy, rad, starPaint)
        }

        // Shooting star streak
        val streakPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = 2.5f
            shader = LinearGradient(
                width * 0.15f, height * 0.18f, width * 0.38f, height * 0.28f,
                intArrayOf(Color.TRANSPARENT, Color.WHITE, Color.argb(180, 120, 220, 255)),
                null, Shader.TileMode.CLAMP
            )
        }
        canvas.drawLine(width * 0.15f, height * 0.18f, width * 0.38f, height * 0.28f, streakPaint)

        // Grassy Cliff Edge on lower right
        val cliffPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(8, 6, 16)
        }
        val cliffPath = Path().apply {
            moveTo(0f, height * 0.82f)
            lineTo(width * 0.35f, height * 0.75f)
            quadTo(width * 0.65f, height * 0.70f, width.toFloat(), height * 0.72f)
            lineTo(width.toFloat(), height.toFloat())
            lineTo(0f, height.toFloat())
            close()
        }
        canvas.drawPath(cliffPath, cliffPaint)

        // Couple silhouette sitting side-by-side on cliff, heads leaning together
        val couplePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(4, 3, 8)
        }
        val cx = width * 0.62f
        val cy = height * 0.71f

        // Partner 1
        canvas.drawCircle(cx - 20f, cy - 80f, 18f, couplePaint) // head
        canvas.drawOval(RectF(cx - 36f, cy - 65f, cx - 4f, cy + 5f), couplePaint) // torso

        // Partner 2 leaning
        canvas.drawCircle(cx + 12f, cy - 76f, 17f, couplePaint) // head
        canvas.drawOval(RectF(cx - 2f, cy - 62f, cx + 28f, cy + 5f), couplePaint) // torso

        // Glowing fireflies around grass
        val fireflyPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        for (i in 0..18) {
            val fx = cx - 180f + rnd.nextFloat() * 320f
            val fy = cy - 40f + rnd.nextFloat() * 120f
            fireflyPaint.color = Color.argb(180, 150, 255, 200)
            canvas.drawCircle(fx, fy, 3.5f, fireflyPaint)
        }

        drawWallpaperWatermark(canvas, width, height, "PARTNER GALAXY", "9:16 ULTRA HD WALLPAPER")
    }

    private fun renderRainyNeonTheme(canvas: Canvas, width: Int, height: Int, prompt: PromptItem) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Moody Twilight Rain Palette
        val skyGradient = LinearGradient(
            0f, 0f, 0f, height.toFloat(),
            intArrayOf(
                Color.rgb(15, 12, 30),
                Color.rgb(35, 18, 55),
                Color.rgb(20, 25, 45),
                Color.rgb(10, 15, 25)
            ),
            floatArrayOf(0f, 0.4f, 0.7f, 1f),
            Shader.TileMode.CLAMP
        )
        paint.shader = skyGradient
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        paint.shader = null

        // Blurred city buildings & neon lights in background
        val bldgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(20, 18, 38)
        }
        canvas.drawRect(width * 0.1f, height * 0.35f, width * 0.32f, height * 0.65f, bldgPaint)
        canvas.drawRect(width * 0.4f, height * 0.28f, width * 0.65f, height * 0.65f, bldgPaint)
        canvas.drawRect(width * 0.72f, height * 0.38f, width * 0.95f, height * 0.65f, bldgPaint)

        // Neon Sign Glowing Glows
        val neonMagenta = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                width * 0.25f, height * 0.42f, 160f,
                Color.argb(160, 255, 30, 150),
                Color.TRANSPARENT,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(width * 0.25f, height * 0.42f, 160f, neonMagenta)

        val neonCyan = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                width * 0.78f, height * 0.45f, 180f,
                Color.argb(160, 30, 220, 255),
                Color.TRANSPARENT,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(width * 0.78f, height * 0.45f, 180f, neonCyan)

        // Wet asphalt ground
        val groundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(12, 10, 18)
        }
        canvas.drawRect(0f, height * 0.65f, width.toFloat(), height.toFloat(), groundPaint)

        // Wet Street Neon Reflections (vertical stretched gradients)
        val refPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, height * 0.65f, 0f, height * 0.92f,
                intArrayOf(Color.argb(140, 255, 30, 150), Color.argb(80, 255, 120, 50), Color.TRANSPARENT),
                null, Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(width * 0.18f, height * 0.65f, width * 0.34f, height * 0.92f, refPaint)

        val refCyanPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, height * 0.65f, 0f, height * 0.92f,
                intArrayOf(Color.argb(140, 30, 220, 255), Color.argb(60, 10, 100, 200), Color.TRANSPARENT),
                null, Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(width * 0.70f, height * 0.65f, width * 0.88f, height * 0.92f, refCyanPaint)

        // Couple walking under large umbrella
        val cx = width * 0.5f
        val cy = height * 0.68f

        // Umbrella
        val umbrellaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(200, 25, 20, 35)
        }
        val umbrellaGlow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 3f
            color = Color.argb(180, 255, 160, 220)
        }
        val umbrellaRect = RectF(cx - 110f, cy - 230f, cx + 110f, cy - 140f)
        canvas.drawArc(umbrellaRect, 180f, 180f, true, umbrellaPaint)
        canvas.drawArc(umbrellaRect, 180f, 180f, false, umbrellaGlow)

        // Umbrella stem
        canvas.drawLine(cx, cy - 180f, cx, cy - 100f, umbrellaGlow)

        // Couple silhouette
        val couplePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(18, 14, 25)
        }
        canvas.drawCircle(cx - 28f, cy - 115f, 22f, couplePaint) // Partner 1 head
        canvas.drawOval(RectF(cx - 52f, cy - 100f, cx - 6f, cy + 20f), couplePaint) // coat
        canvas.drawCircle(cx + 25f, cy - 110f, 20f, couplePaint) // Partner 2 head
        canvas.drawOval(RectF(cx + 4f, cy - 95f, cx + 46f, cy + 20f), couplePaint) // coat

        // Rain Streaks
        val rainPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = 1.6f
            color = Color.argb(70, 200, 220, 255)
        }
        val rnd = Random(99)
        for (i in 0..70) {
            val rx = rnd.nextFloat() * width
            val ry = rnd.nextFloat() * height
            canvas.drawLine(rx, ry, rx - 10f, ry + 28f, rainPaint)
        }

        drawWallpaperWatermark(canvas, width, height, "NEON ROMANCE", "9:16 MOBILE LOCKSCREEN")
    }

    private fun renderPersianGardenTheme(canvas: Canvas, width: Int, height: Int, prompt: PromptItem) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Warm Terracotta & Turquoise Persian Palace Palette
        val bgGradient = LinearGradient(
            0f, 0f, 0f, height.toFloat(),
            intArrayOf(
                Color.rgb(55, 30, 40),
                Color.rgb(160, 95, 60),
                Color.rgb(230, 180, 110),
                Color.rgb(20, 110, 120) // Turquoise reflection
            ),
            floatArrayOf(0f, 0.4f, 0.65f, 1f),
            Shader.TileMode.CLAMP
        )
        paint.shader = bgGradient
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        paint.shader = null

        // Persian Archway frame
        val archPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(40, 20, 25)
        }
        // Left pillar
        canvas.drawRect(0f, 0f, width * 0.18f, height.toFloat(), archPaint)
        // Right pillar
        canvas.drawRect(width * 0.82f, 0f, width.toFloat(), height.toFloat(), archPaint)
        // Top arch
        val archPath = Path().apply {
            moveTo(0f, 0f)
            lineTo(width.toFloat(), 0f)
            lineTo(width.toFloat(), height * 0.32f)
            quadTo(width * 0.5f, height * 0.15f, 0f, height * 0.32f)
            close()
        }
        canvas.drawPath(archPath, archPaint)

        // Traditional Turquoise Pool (Howz) in lower half
        val howzPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(15, 95, 110)
        }
        val howzRect = RectF(width * 0.22f, height * 0.65f, width * 0.78f, height * 0.92f)
        canvas.drawRoundRect(howzRect, 40f, 40f, howzPaint)

        // Pool border
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 6f
            color = Color.rgb(210, 160, 80)
        }
        canvas.drawRoundRect(howzRect, 40f, 40f, borderPaint)

        // Pomegranates in bowl on edge
        val pomPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(190, 35, 45)
        }
        canvas.drawCircle(width * 0.5f, height * 0.64f, 16f, pomPaint)
        canvas.drawCircle(width * 0.48f, height * 0.65f, 14f, pomPaint)
        canvas.drawCircle(width * 0.52f, height * 0.65f, 14f, pomPaint)

        drawVintagePhotoFrame(canvas, width, height, "PERSIAN COURTYARD • 1975", "ISFAHAN HERITAGE NOSTALGIA")
    }

    private fun renderCyberNeonTheme(canvas: Canvas, width: Int, height: Int, prompt: PromptItem) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Dark Synthwave Night
        val bgGradient = LinearGradient(
            0f, 0f, 0f, height.toFloat(),
            intArrayOf(
                Color.rgb(10, 8, 25),
                Color.rgb(40, 10, 60),
                Color.rgb(80, 15, 80),
                Color.rgb(15, 10, 30)
            ),
            floatArrayOf(0f, 0.45f, 0.75f, 1f),
            Shader.TileMode.CLAMP
        )
        paint.shader = bgGradient
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        paint.shader = null

        // Glowing Neon Grid Horizon
        val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = 2f
            color = Color.argb(120, 0, 240, 255)
        }
        val horizonY = height * 0.65f
        for (i in 0..12) {
            val y = horizonY + (i * i * 3.5f)
            if (y < height) {
                canvas.drawLine(0f, y, width.toFloat(), y, gridPaint)
            }
        }
        for (i in 0..14) {
            val startX = (width / 14f) * i
            canvas.drawLine(startX, horizonY, (startX - width * 0.5f) * 2.5f + width * 0.5f, height.toFloat(), gridPaint)
        }

        // Giant Cyber Sun
        val sunPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, horizonY - width * 0.35f, 0f, horizonY,
                intArrayOf(Color.rgb(255, 220, 0), Color.rgb(255, 40, 120)),
                null, Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(width * 0.5f, horizonY, width * 0.35f, sunPaint)

        // Neon Partner Silhouette
        val figurePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(5, 5, 12)
        }
        val fx = width * 0.5f
        val fy = horizonY - 20f
        canvas.drawCircle(fx - 25f, fy - 75f, 20f, figurePaint)
        canvas.drawOval(RectF(fx - 45f, fy - 60f, fx - 5f, fy + 40f), figurePaint)
        canvas.drawCircle(fx + 25f, fy - 75f, 20f, figurePaint)
        canvas.drawOval(RectF(fx + 5f, fy - 60f, fx + 45f, fy + 40f), figurePaint)

        drawWallpaperWatermark(canvas, width, height, "NEO RETRO CYBER", "9:16 LOCKSCREEN WALLPAPER")
    }

    private fun renderMinimalStudioTheme(canvas: Canvas, width: Int, height: Int, prompt: PromptItem) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Warm Scandinavian linen / stone studio
        val bgGradient = LinearGradient(
            0f, 0f, 0f, height.toFloat(),
            intArrayOf(
                Color.rgb(238, 230, 220),
                Color.rgb(215, 200, 185),
                Color.rgb(195, 175, 160)
            ),
            null, Shader.TileMode.CLAMP
        )
        paint.shader = bgGradient
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        paint.shader = null

        // Organic warm aesthetic arch
        val archPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(180, 155, 135)
        }
        val rect = RectF(width * 0.2f, height * 0.22f, width * 0.8f, height * 0.72f)
        canvas.drawRoundRect(rect, 180f, 180f, archPaint)

        // Soft silhouette
        val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(110, 90, 75)
        }
        canvas.drawCircle(width * 0.5f, height * 0.42f, 55f, innerPaint)
        canvas.drawOval(RectF(width * 0.4f, height * 0.48f, width * 0.6f, height * 0.65f), innerPaint)

        drawVintagePhotoFrame(canvas, width, height, "MINIMALIST STUDIO", "35MM LEICA CANDID")
    }

    private fun drawVintagePhotoFrame(
        canvas: Canvas,
        width: Int,
        height: Int,
        title: String,
        subtitle: String
    ) {
        // Bottom badge card
        val badgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(190, 15, 12, 18)
        }
        val badgeRect = RectF(width * 0.08f, height * 0.86f, width * 0.92f, height * 0.95f)
        canvas.drawRoundRect(badgeRect, 24f, 24f, badgePaint)

        // Border accent
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 2.5f
            color = Color.argb(120, 255, 215, 120)
        }
        canvas.drawRoundRect(badgeRect, 24f, 24f, borderPaint)

        // Title
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(255, 230, 160)
            textSize = 34f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(title, width * 0.5f, height * 0.90f, textPaint)

        // Subtitle
        textPaint.textSize = 22f
        textPaint.color = Color.rgb(210, 210, 210)
        textPaint.isFakeBoldText = false
        canvas.drawText(subtitle, width * 0.5f, height * 0.93f, textPaint)
    }

    private fun drawWallpaperWatermark(
        canvas: Canvas,
        width: Int,
        height: Int,
        title: String,
        subtitle: String
    ) {
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(200, 255, 255, 255)
            textSize = 32f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(title, width * 0.5f, height * 0.92f, textPaint)

        textPaint.textSize = 20f
        textPaint.color = Color.argb(150, 220, 220, 240)
        textPaint.isFakeBoldText = false
        canvas.drawText(subtitle, width * 0.5f, height * 0.945f, textPaint)
    }

    private fun applyColorOverlay(canvas: Canvas, width: Int, height: Int, color: Int) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }

    private fun applyMonochromeOverlay(canvas: Canvas, width: Int, height: Int) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(140, 30, 30, 30)
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }

    private fun applyVignette(canvas: Canvas, width: Int, height: Int) {
        val vignette = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                width * 0.5f, height * 0.5f,
                Math.max(width, height) * 0.65f,
                intArrayOf(Color.TRANSPARENT, Color.argb(40, 0, 0, 0), Color.argb(130, 0, 0, 0)),
                floatArrayOf(0f, 0.6f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), vignette)
    }

    private fun applyFilmGrain(canvas: Canvas, width: Int, height: Int) {
        val grainPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val rnd = Random(1234)
        for (i in 0..400) {
            val gx = rnd.nextFloat() * width
            val gy = rnd.nextFloat() * height
            grainPaint.color = Color.argb(18 + rnd.nextInt(25), 255, 255, 255)
            canvas.drawCircle(gx, gy, 1f + rnd.nextFloat() * 1.5f, grainPaint)
        }
    }
}
