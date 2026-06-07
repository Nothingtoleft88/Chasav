package com.example.ui.companion

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

enum class MascotType(val id: String, val displayName: String, val emoji: String, val color: Color) {
    MOMO("momo", "Pio (Kelinci) 🐰", "🐰", Color(0xFFFFC0CB)),
    KOKO("koko", "Coco (Kucing) 🐱", "🐱", Color(0xFFFFDAB9)),
    PIP("pip", "Onal (Babi Celengan) 🐷", "🐷", Color(0xFFFFB6C1))
}

@Composable
fun CuteMascotBubble(
    mascotType: MascotType,
    spendingPercentage: Double, // 0.0 to 1.0
    hasTransactions: Boolean,
    modifier: Modifier = Modifier
) {
    var isHappy by remember { mutableStateOf(true) }
    var scaleY by remember { mutableStateOf(1f) }
    var currentSpeech by remember { mutableStateOf("") }

    // Spring animation for mascot bounce
    val bounceAnim = rememberInfiniteTransition(label = "mascot_bounce")
    val translationY by bounceAnim.animateFloat(
        initialValue = 0f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    // Speech quotes mapper
    LaunchedEffect(mascotType, spendingPercentage, hasTransactions) {
        val quotes = when {
            !hasTransactions -> when (mascotType) {
                MascotType.MOMO -> listOf(
                    "Halo Kak! Yuk mulai catat keuangan harian Kakak bareng Pio! ✨",
                    "Hihi, dompetnya masih bersih nih. Pio siap bantu catat ya! 🥕"
                )
                MascotType.KOKO -> listOf(
                    "Meww~ Selamat datang Kak! Yuk catat pemasukan / pengeluaran pertamamu! 🍃",
                    "Meong! Hari ini belum jajan? Hebat! Catat yuk kalau ada transaksi! 🐾"
                )
                MascotType.PIP -> listOf(
                    "Oink oink! Celengan Onal masih kosong melompong. Masukkan koin yuk! 💰",
                    "Halo! Onal siap menelan semua laporan keuangan Kakak! Nyum! 🍕"
                )
            }
            spendingPercentage >= 0.9 -> when (mascotType) {
                MascotType.MOMO -> listOf(
                    "Aduh gawat Kak! 🥺 Anggaran Kakak sudah kritis (>90%). Berhenti jajan dulu ya!",
                    "Gawat! Pio nangis nih lihat pengeluaran Kakak yang super boros hari ini... 😭"
                )
                MascotType.KOKO -> listOf(
                    "Meong keras-keras! 🙀 Dompetmu kritis Kak! Yuk diet belanja, makan mie instan dulu? Meow~",
                    "Auuuw, anggaran menipis seperti kumisku! Sayangi uangmu Kak! 😿"
                )
                MascotType.PIP -> listOf(
                    "Oink! Celengan jebol Kak! 💔 Uang keluar terus, kasihan celenganmu lapar!",
                    "Stop belanja Kak! Dompetmu bisa pingsan kalau digesek terus! 😤"
                )
            }
            spendingPercentage >= 0.7 -> when (mascotType) {
                MascotType.MOMO -> listOf(
                    "Eits, pengeluaran sudah lewat 70%. Kurangi shopeepay-nya ya Kak! 😉",
                    "Pio ingatkan ya, dompet mulai diet ketat. Jaga diri dari godaan boba! 🧋"
                )
                MascotType.KOKO -> listOf(
                    "Meww~ Kantongmu mulai kempes nih Kak. Fokus belanja kebutuhan utama saja ya! ❤️",
                    "Kurangi jajan gorengan Kak, simpan uangnya buat masa depan meow-mu! 🐟"
                )
                MascotType.PIP -> listOf(
                    "Nguuuk~ Anggaran bulanan sudah kuning. Kunci dompetnya sekarang juga! 🔑",
                    "Onal mendeteksi pengeluaran berlebih di sektor jajan! Waspada! ⚠️"
                )
            }
            else -> when (mascotType) {
                MascotType.MOMO -> listOf(
                    "Yay! Pengeluaran Kakak super sehat! Sisa anggaran melimpah! 🌸",
                    "Pio senang melihat Kakak jago mengatur uang! Dapat wortel virtual! 🥕✨",
                    "Wah, tabungan Kakak makin gemuk, seperti pipi Pio! Hihi! 🥰"
                )
                MascotType.KOKO -> listOf(
                    "Meong mantap! Kakak hemat sekali bulan ini, dapet elusan kucing! 🐱❤️",
                    "Asyik, dompet aman sentosa! Besok bisa nabung banyak nih, mewww! 🐟",
                    "Mew! Kakak pahlawan keuangan! Coco bangga sekali! 🏆"
                )
                MascotType.PIP -> listOf(
                    "Oink-oink gembira! 🐖 Onal kenyang berisi banyak laporan hemat Kakak!",
                    "Pintar menabung! Sisa anggaran melimpah ruah, yuk tabung lagi! 🏦",
                    "Tabungan bertambah, Onal makin gemuk dan bahagia! Oink! 🎉"
                )
            }
        }
        // Rotate quotes periodically
        while (true) {
            currentSpeech = quotes.random()
            delay(8000)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mascot character
        Box(
            modifier = Modifier
                .size(90.dp)
                .offset(y = translationY.dp)
                .clickable {
                    isHappy = !isHappy
                    scaleY = 0.8f
                },
            contentAlignment = Alignment.Center
        ) {
            // Simple animated vector drawing for custom character or fallback icon
            MascotVector(mascotType = mascotType, isHappy = isHappy, spendingPercentage = spendingPercentage)
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Speech Bubble
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(18.dp),
            shadowElevation = 3.dp,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier.padding(14.dp)
            ) {
                Column {
                    Text(
                        text = mascotType.displayName.split(" ")[0],
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = mascotType.color.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = currentSpeech,
                        fontSize = 13.sp,
                        color = Color(0xFF5E4E52),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MascotVector(
    mascotType: MascotType,
    isHappy: Boolean,
    spendingPercentage: Double
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        when (mascotType) {
            MascotType.MOMO -> {
                // Outer Pink body
                drawRoundRect(
                    color = Color(0xFFFFD1DC),
                    topLeft = Offset(w * 0.15f, h * 0.35f),
                    size = Size(w * 0.7f, h * 0.55f),
                    cornerRadius = CornerRadius(40f, 40f)
                )

                // Left Ear
                drawRoundRect(
                    color = Color(0xFFFFD1DC),
                    topLeft = Offset(w * 0.25f, h * 0.05f),
                    size = Size(w * 0.18f, h * 0.4f),
                    cornerRadius = CornerRadius(30f, 30f)
                )
                // Left Ear Inner pink
                drawRoundRect(
                    color = Color(0xFFFFB7B2),
                    topLeft = Offset(w * 0.29f, h * 0.12f),
                    size = Size(w * 0.10f, h * 0.28f),
                    cornerRadius = CornerRadius(20f, 20f)
                )

                // Right Ear
                drawRoundRect(
                    color = Color(0xFFFFD1DC),
                    topLeft = Offset(w * 0.57f, h * 0.05f),
                    size = Size(w * 0.18f, h * 0.4f),
                    cornerRadius = CornerRadius(30f, 30f)
                )
                // Right Ear Inner pink
                drawRoundRect(
                    color = Color(0xFFFFB7B2),
                    topLeft = Offset(w * 0.61f, h * 0.12f),
                    size = Size(w * 0.10f, h * 0.28f),
                    cornerRadius = CornerRadius(20f, 20f)
                )

                // White tummy overlay
                drawCircle(
                    color = Color.White,
                    radius = w * 0.2f,
                    center = Offset(w * 0.5f, h * 0.75f)
                )

                // Eyes
                if (isHappy && spendingPercentage < 0.9) {
                    // Curved Happy eyes
                    drawArc(
                        color = Color(0xFF5E4E52),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(w * 0.28f, h * 0.45f),
                        size = Size(w * 0.12f, h * 0.12f),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                    )
                    drawArc(
                        color = Color(0xFF5E4E52),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(w * 0.60f, h * 0.45f),
                        size = Size(w * 0.12f, h * 0.12f),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                    )
                } else {
                    // Dot sad/normal eyes
                    drawCircle(color = Color(0xFF5E4E52), radius = 6f, center = Offset(w * 0.35f, h * 0.54f))
                    drawCircle(color = Color(0xFF5E4E52), radius = 6f, center = Offset(w * 0.65f, h * 0.54f))
                    // Tear drops if danger
                    if (spendingPercentage >= 0.9) {
                        drawCircle(color = Color(0xFF81D4FA), radius = 4f, center = Offset(w * 0.35f, h * 0.62f))
                        drawCircle(color = Color(0xFF81D4FA), radius = 4f, center = Offset(w * 0.65f, h * 0.62f))
                    }
                }

                // Cheek Blushes
                drawCircle(color = Color(0xFFFFB2B2).copy(alpha = 0.6f), radius = 10f, center = Offset(w * 0.26f, h * 0.62f))
                drawCircle(color = Color(0xFFFFB2B2).copy(alpha = 0.6f), radius = 10f, center = Offset(w * 0.74f, h * 0.62f))

                // Nose & Bunny Mouth
                drawCircle(color = Color(0xFFE57373), radius = 4f, center = Offset(w * 0.5f, h * 0.58f))
                // Mouth
                drawArc(
                    color = Color(0xFF5E4E52),
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(w * 0.44f, h * 0.58f),
                    size = Size(w * 0.06f, h * 0.06f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                )
                drawArc(
                    color = Color(0xFF5E4E52),
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(w * 0.50f, h * 0.58f),
                    size = Size(w * 0.06f, h * 0.06f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                )
            }
            MascotType.KOKO -> {
                // Kitten drawing
                // Body/Face circle
                drawCircle(
                    color = Color(0xFFFFD280), // Orange/yellow kitty
                    radius = w * 0.38f,
                    center = Offset(w * 0.5f, h * 0.58f)
                )

                // Left Ear triangle
                val pathLeft = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.16f, h * 0.32f)
                    lineTo(w * 0.12f, h * 0.06f)
                    lineTo(w * 0.42f, h * 0.28f)
                    close()
                }
                drawPath(path = pathLeft, color = Color(0xFFFFD280))
                val pathLeftInner = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.20f, h * 0.28f)
                    lineTo(w * 0.16f, h * 0.12f)
                    lineTo(w * 0.36f, h * 0.26f)
                    close()
                }
                drawPath(path = pathLeftInner, color = Color(0xFFFFB6C1))

                // Right Ear triangle
                val pathRight = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.84f, h * 0.32f)
                    lineTo(w * 0.88f, h * 0.06f)
                    lineTo(w * 0.58f, h * 0.28f)
                    close()
                }
                drawPath(path = pathRight, color = Color(0xFFFFD280))
                val pathRightInner = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.80f, h * 0.28f)
                    lineTo(w * 0.84f, h * 0.12f)
                    lineTo(w * 0.64f, h * 0.26f)
                    close()
                }
                drawPath(path = pathRightInner, color = Color(0xFFFFB6C1))

                // Eyes
                if (isHappy && spendingPercentage < 0.9) {
                    drawArc(
                        color = Color(0xFF344A43),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(w * 0.25f, h * 0.48f),
                        size = Size(w * 0.12f, h * 0.10f),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                    )
                    drawArc(
                        color = Color(0xFF344A43),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(w * 0.63f, h * 0.48f),
                        size = Size(w * 0.12f, h * 0.10f),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                    )
                } else {
                    drawCircle(color = Color(0xFF344A43), radius = 6f, center = Offset(w * 0.33f, h * 0.54f))
                    drawCircle(color = Color(0xFF344A43), radius = 6f, center = Offset(w * 0.67f, h * 0.54f))
                }

                // Blushes
                drawCircle(color = Color(0xFFFF9E80).copy(alpha = 0.5f), radius = 8f, center = Offset(w * 0.24f, h * 0.62f))
                drawCircle(color = Color(0xFFFF9E80).copy(alpha = 0.5f), radius = 8f, center = Offset(w * 0.76f, h * 0.62f))

                // Whiskers
                drawLine(color = Color(0xFF5E4E52), start = Offset(w * 0.15f, h * 0.6f), end = Offset(w * 0.28f, h * 0.62f), strokeWidth = 3f)
                drawLine(color = Color(0xFF5E4E52), start = Offset(w * 0.14f, h * 0.68f), end = Offset(w * 0.27f, h * 0.66f), strokeWidth = 3f)

                drawLine(color = Color(0xFF5E4E52), start = Offset(w * 0.85f, h * 0.6f), end = Offset(w * 0.72f, h * 0.62f), strokeWidth = 3f)
                drawLine(color = Color(0xFF5E4E52), start = Offset(w * 0.86f, h * 0.68f), end = Offset(w * 0.73f, h * 0.66f), strokeWidth = 3f)

                // Kitten Nose/Mouth
                drawCircle(color = Color(0xFFFF8A80), radius = 4f, center = Offset(w * 0.5f, h * 0.60f))
                // Cute kitty mouth
                drawArc(
                    color = Color(0xFF5E4E52),
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(w * 0.44f, h * 0.60f),
                    size = Size(w * 0.06f, h * 0.06f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                )
                drawArc(
                    color = Color(0xFF5E4E52),
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(w * 0.50f, h * 0.60f),
                    size = Size(w * 0.06f, h * 0.06f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                )
            }
            MascotType.PIP -> {
                // Piggy character
                drawCircle(
                    color = Color(0xFFFFB6C1), // Light pink pig
                    radius = w * 0.38f,
                    center = Offset(w * 0.5f, h * 0.58f)
                )

                // Left ear folded
                drawOval(
                    color = Color(0xFFFF8DA1),
                    topLeft = Offset(w * 0.16f, h * 0.15f),
                    size = Size(w * 0.18f, h * 0.22f)
                )
                // Right ear folded
                drawOval(
                    color = Color(0xFFFF8DA1),
                    topLeft = Offset(w * 0.66f, h * 0.15f),
                    size = Size(w * 0.18f, h * 0.22f)
                )

                // Piggy Nose Snout
                drawRoundRect(
                    color = Color(0xFFFF829B),
                    topLeft = Offset(w * 0.38f, h * 0.55f),
                    size = Size(w * 0.24f, h * 0.16f),
                    cornerRadius = CornerRadius(20f, 20f)
                )
                // Snout holes
                drawCircle(color = Color(0xFF5E4E52), radius = 3.5f, center = Offset(w * 0.45f, h * 0.63f))
                drawCircle(color = Color(0xFF5E4E52), radius = 3.5f, center = Offset(w * 0.55f, h * 0.63f))

                // Eyes
                if (isHappy && spendingPercentage < 0.9) {
                    drawArc(
                        color = Color(0xFF5E4E52),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(w * 0.26f, h * 0.42f),
                        size = Size(w * 0.12f, h * 0.10f),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                    )
                    drawArc(
                        color = Color(0xFF5E4E52),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(w * 0.62f, h * 0.42f),
                        size = Size(w * 0.12f, h * 0.10f),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                    )
                } else {
                    drawCircle(color = Color(0xFF5E4E52), radius = 5.5f, center = Offset(w * 0.32f, h * 0.48f))
                    drawCircle(color = Color(0xFF5E4E52), radius = 5.5f, center = Offset(w * 0.68f, h * 0.48f))
                }

                // Blushes
                drawCircle(color = Color(0xFFFF8DA1).copy(alpha = 0.4f), radius = 8f, center = Offset(w * 0.22f, h * 0.56f))
                drawCircle(color = Color(0xFFFF8DA1).copy(alpha = 0.4f), radius = 8f, center = Offset(w * 0.78f, h * 0.56f))
            }
        }
    }
}
