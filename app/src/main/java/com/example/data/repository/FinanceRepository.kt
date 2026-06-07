package com.example.data.repository

import com.example.data.dao.FinanceDao
import com.example.data.model.BudgetEntity
import com.example.data.model.SavingGoalEntity
import com.example.data.model.TransactionEntity
import kotlinx.coroutines.flow.Flow

class FinanceRepository(private val financeDao: FinanceDao) {

    // --- Transactions ---
    val allTransactions: Flow<List<TransactionEntity>> = financeDao.getAllTransactionsFlow()

    fun getTransactionsForRange(start: Long, end: Long): Flow<List<TransactionEntity>> {
        return financeDao.getTransactionsForRangeFlow(start, end)
    }

    suspend fun insertTransaction(transaction: TransactionEntity) {
        financeDao.insertTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: TransactionEntity) {
        financeDao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        financeDao.deleteTransaction(transaction)
    }

    suspend fun deleteTransactionById(id: Int) {
        financeDao.deleteTransactionById(id)
    }


    // --- Budgets ---
    fun getBudgetsForMonth(monthYear: String): Flow<List<BudgetEntity>> {
        return financeDao.getBudgetsForMonthFlow(monthYear)
    }

    fun getBudgetByCategoryAndMonthFlow(categoryName: String, monthYear: String): Flow<BudgetEntity?> {
        return financeDao.getBudgetByCategoryAndMonthFlow(categoryName, monthYear)
    }

    suspend fun getBudgetByCategoryAndMonth(categoryName: String, monthYear: String): BudgetEntity? {
        return financeDao.getBudgetByCategoryAndMonth(categoryName, monthYear)
    }

    suspend fun insertBudget(budget: BudgetEntity) {
        financeDao.insertBudget(budget)
    }

    suspend fun deleteBudget(budget: BudgetEntity) {
        financeDao.deleteBudget(budget)
    }


    // --- Saving Goals ---
    val allSavingGoals: Flow<List<SavingGoalEntity>> = financeDao.getAllSavingGoalsFlow()

    suspend fun insertSavingGoal(goal: SavingGoalEntity) {
        financeDao.insertSavingGoal(goal)
    }

    suspend fun updateSavingGoal(goal: SavingGoalEntity) {
        financeDao.updateSavingGoal(goal)
    }

    suspend fun deleteSavingGoal(goal: SavingGoalEntity) {
        financeDao.deleteSavingGoal(goal)
    }


    // --- Backup & Restore ---
    val allBudgets: Flow<List<BudgetEntity>> = financeDao.getAllBudgetsFlow()

    suspend fun restoreDatabase(
        transactions: List<TransactionEntity>,
        budgets: List<BudgetEntity>,
        savingGoals: List<SavingGoalEntity>
    ) {
        financeDao.clearTransactions()
        financeDao.clearBudgets()
        financeDao.clearSavingGoals()

        if (transactions.isNotEmpty()) financeDao.insertTransactionsBulk(transactions)
        if (budgets.isNotEmpty()) financeDao.insertBudgetsBulk(budgets)
        if (savingGoals.isNotEmpty()) financeDao.insertSavingGoalsBulk(savingGoals)
    }
}
