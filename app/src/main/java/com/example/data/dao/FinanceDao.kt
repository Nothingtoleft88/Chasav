package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BudgetEntity
import com.example.data.model.SavingGoalEntity
import com.example.data.model.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceDao {

    // --- Transactions Queries ---
    @Query("SELECT * FROM transactions ORDER BY dateMillis DESC")
    fun getAllTransactionsFlow(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE dateMillis >= :startMillis AND dateMillis <= :endMillis ORDER BY dateMillis DESC")
    fun getTransactionsForRangeFlow(startMillis: Long, endMillis: Long): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Int)


    // --- Budgets Queries ---
    @Query("SELECT * FROM budgets WHERE monthYear = :monthYear")
    fun getBudgetsForMonthFlow(monthYear: String): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE categoryName = :categoryName AND monthYear = :monthYear LIMIT 1")
    suspend fun getBudgetByCategoryAndMonth(categoryName: String, monthYear: String): BudgetEntity?

    @Query("SELECT * FROM budgets WHERE categoryName = :categoryName AND monthYear = :monthYear LIMIT 1")
    fun getBudgetByCategoryAndMonthFlow(categoryName: String, monthYear: String): Flow<BudgetEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity)

    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)


    // --- Saving Goals Queries ---
    @Query("SELECT * FROM saving_goals")
    fun getAllSavingGoalsFlow(): Flow<List<SavingGoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingGoal(goal: SavingGoalEntity)

    @Update
    suspend fun updateSavingGoal(goal: SavingGoalEntity)

    @Delete
    suspend fun deleteSavingGoal(goal: SavingGoalEntity)


    // --- Backup & Restore Queries ---
    @Query("SELECT * FROM budgets")
    fun getAllBudgetsFlow(): Flow<List<BudgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactionsBulk(transactions: List<TransactionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudgetsBulk(budgets: List<BudgetEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingGoalsBulk(goals: List<SavingGoalEntity>)

    @Query("DELETE FROM transactions")
    suspend fun clearTransactions()

    @Query("DELETE FROM budgets")
    suspend fun clearBudgets()

    @Query("DELETE FROM saving_goals")
    suspend fun clearSavingGoals()
}
