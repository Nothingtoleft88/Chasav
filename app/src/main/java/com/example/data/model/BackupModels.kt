package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FinanceBackup(
    val transactions: List<TransactionEntity>,
    val budgets: List<BudgetEntity>,
    val savingGoals: List<SavingGoalEntity>
)
