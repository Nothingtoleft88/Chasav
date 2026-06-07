package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import java.io.Serializable

@Entity(tableName = "transactions")
@JsonClass(generateAdapter = true)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val type: String, // "EXPENSE", "INCOME", or "TRANSFER"
    val categoryName: String,
    val categoryIcon: String,
    val categoryColorHex: String,
    val dateMillis: Long,
    val note: String,
    val walletName: String, // source wallet
    val transferToWallet: String? = null, // target wallet for transfers
    val ledgerName: String = "Sari-sari (Harian)", // Multiple ledgers / books
    val memberName: String = "Pribadi", // e.g. Pribadi, Pasangan, Anak, Orang tua
    val currencyCode: String = "IDR", // e.g. IDR, USD, JPY, EUR, SGD
    val exchangeRate: Double = 1.0, // relative to IDR/base
    val tagsCsv: String = "", // Comma-separated tags
    val isPeriodic: Boolean = false,
    val periodInterval: String = "NONE" // "DAILY", "WEEKLY", "MONTHLY", "NONE"
) : Serializable

@Entity(tableName = "budgets")
@JsonClass(generateAdapter = true)
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val categoryName: String, // "GLOBAL" or individual category names
    val amount: Double,
    val monthYear: String, // Format: "MM-yyyy" e.g., "06-2026"
    val budgetType: String = "MONTHLY", // "DAILY", "WEEKLY", "MONTHLY", "YEARLY"
    val ledgerName: String = "Sari-sari (Harian)"
) : Serializable

@Entity(tableName = "saving_goals")
@JsonClass(generateAdapter = true)
data class SavingGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val iconName: String, // e.g. "celebration", "flight", "gamepad", "laptop"
    val colorHex: String
) : Serializable
