package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor

enum class CuteTheme(
    val id: String,
    val displayName: String,
    val primary: Color,
    val background: Color,
    val surface: Color,
    val onPrimary: Color,
    val onSurface: Color,
    val accent: Color,
    val cardBackground: Color
) {
    ARTISTIC(
        id = "artistic",
        displayName = "Artistic Flair 🎨",
        primary = Color(0xFFFF6B6B),
        background = Color(0xFFFFFBFB),
        surface = Color(0xFFFFFFFF),
        onPrimary = Color(0xFFFFFFFF),
        onSurface = Color(0xFF4A3F3F),
        accent = Color(0xFFFF8585),
        cardBackground = Color(0xFFFFEDED)
    ),
    SAKURA(
        id = "sakura",
        displayName = "Sakura Pink 🌸",
        primary = Color(0xFFFF94B0),
        background = Color(0xFFFFF0F3),
        surface = Color(0xFFFFFFFF),
        onPrimary = Color(0xFFFFFFFF),
        onSurface = Color(0xFF5E4E52),
        accent = Color(0xFFFF5280),
        cardBackground = Color(0xFFFFF5F7)
    ),
    MINT(
        id = "mint",
        displayName = "Minty Garden 🍃",
        primary = Color(0xFF8AE0C5),
        background = Color(0xFFE8F8F5),
        surface = Color(0xFFFFFFFF),
        onPrimary = Color(0xFFFFFFFF),
        onSurface = Color(0xFF344A43),
        accent = Color(0xFF1EAD8E),
        cardBackground = Color(0xFFF2FDFB)
    ),
    PEACH(
        id = "peach",
        displayName = "Peach Apricot 🍑",
        primary = Color(0xFFFFB37C),
        background = Color(0xFFFFF6ED),
        surface = Color(0xFFFFFFFF),
        onPrimary = Color(0xFFFFFFFF),
        onSurface = Color(0xFF634D41),
        accent = Color(0xFFFF7E3D),
        cardBackground = Color(0xFFFFF9F5)
    ),
    LAVENDER(
        id = "lavender",
        displayName = "Lavender Dream ☁️",
        primary = Color(0xFFC3AEFF),
        background = Color(0xFFF5F3FF),
        surface = Color(0xFFFFFFFF),
        onPrimary = Color(0xFFFFFFFF),
        onSurface = Color(0xFF4C4263),
        accent = Color(0xFF8F63FF),
        cardBackground = Color(0xFFF9F7FF)
    );

    fun getCardBrush(): Brush {
        return if (this == ARTISTIC) {
            Brush.linearGradient(
                colors = listOf(Color(0xFFFFEDED), Color(0xFFFFF5F5))
            )
        } else {
            SolidColor(cardBackground)
        }
    }
}

data class CuteCategory(
    val name: String,
    val iconName: String, // mapped to material icons or custom drawings
    val colorHex: String,
    val isExpense: Boolean
)

val DEFAULT_CATEGORIES = listOf(
    // Pengeluaran / Expenses
    CuteCategory("Makanan", "restaurant", "#FFB7B2", true),
    CuteCategory("Belanja", "shopping_bag", "#FFDAC1", true),
    CuteCategory("Transportasi", "directions_car", "#E2F0CB", true),
    CuteCategory("Hiburan", "sports_esports", "#BFFCC6", true),
    CuteCategory("Tagihan", "receipt_long", "#D5AAFF", true),
    CuteCategory("Kesehatan", "medical_services", "#FFC6FF", true),
    CuteCategory("Pendidikan", "school", "#A0C4FF", true),
    CuteCategory("Sosial/Kado", "card_giftcard", "#E8AEB7", true),
    CuteCategory("Lainnya", "category", "#D8E2DC", true),

    // Pemasukan / Incomes
    CuteCategory("Gaji", "payments", "#BFFCC6", false),
    CuteCategory("Investasi", "trending_up", "#A0C4FF", false),
    CuteCategory("Uang Saku", "redeem", "#FFDAC1", false),
    CuteCategory("Lainnya", "monetization_on", "#D5AAFF", false)
)
