package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.TransactionEntity
import com.example.ui.components.CuteDonutChart
import com.example.ui.components.CuteMonthlyBarChart
import com.example.ui.viewmodel.FinanceViewModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StatsScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val theme by viewModel.currentTheme.collectAsStateWithLifecycle()
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val currentMonthTransactions by viewModel.currentMonthTransactions.collectAsStateWithLifecycle()

    var isExpenseView by remember { mutableStateOf(true) }
    var analysisType by remember { mutableStateOf("CATEGORY") } // "CATEGORY", "TAG", "MEMBER"

    val activeTransactions = remember(currentMonthTransactions, isExpenseView) {
        currentMonthTransactions.filter { if (isExpenseView) it.type == "EXPENSE" else it.type == "INCOME" }
    }

    val totalActive = activeTransactions.sumOf { it.amount }

    // Map monthly trend from last 3 months
    val monthlyTrend = remember(allTransactions) {
        val df = SimpleDateFormat("MM-yyyy", Locale.getDefault())
        val grouped = allTransactions.filter { it.type == "EXPENSE" }.groupBy {
            val date = Date(it.dateMillis)
            df.format(date)
        }
        val sortedMonths = grouped.map { (monthYear, list) ->
            val monthLabel = try {
                val inputDate = df.parse(monthYear)
                val outDf = SimpleDateFormat("MMM", Locale("id", "ID"))
                outDf.format(inputDate)
            } catch (e: Exception) {
                monthYear
            }
            monthLabel to list.sumOf { tx -> tx.amount }
        }.take(4)

        if (sortedMonths.isEmpty()) {
            val cal = Calendar.getInstance()
            val m1 = SimpleDateFormat("MMM", Locale("id", "ID")).format(cal.time)
            listOf(m1 to 0.0)
        } else {
            sortedMonths
        }
    }

    // --- Dynamic Analysis Groupings ---
    val tagBreakdown = remember(activeTransactions) {
        val map = mutableMapOf<String, Double>()
        activeTransactions.forEach { tx ->
            if (!tx.tagsCsv.isNullOrBlank()) {
                tx.tagsCsv.split(",").forEach { tag ->
                    val trimmed = tag.trim()
                    if (trimmed.isNotEmpty()) {
                        val normalized = if (trimmed.startsWith("#")) trimmed else "#$trimmed"
                        map[normalized] = (map[normalized] ?: 0.0) + tx.amount
                    }
                }
            }
        }
        val total = map.values.sum()
        map.map { (name, sum) ->
            val progress = if (total > 0f) (sum / total).toFloat() else 0f
            Triple(name, sum, progress)
        }.sortedByDescending { it.second }
    }

    val memberBreakdown = remember(activeTransactions) {
        val map = mutableMapOf<String, Double>()
        activeTransactions.forEach { tx ->
            val mLabel = if (tx.memberName.isNullOrBlank()) "Pribadi" else tx.memberName
            map[mLabel] = (map[mLabel] ?: 0.0) + tx.amount
        }
        val total = map.values.sum()
        map.map { (name, sum) ->
            val progress = if (total > 0f) (sum / total).toFloat() else 0f
            Triple(name, sum, progress)
        }.sortedByDescending { it.second }
    }

    val currencyFormatter = DecimalFormat("#,###")

    Scaffold(
        containerColor = theme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Header Title
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Text(
                    "Analisis Keuangan Imut 📊",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = theme.onSurface
                )
                Text(
                    "Evaluasi pengeluaran & pemasukan harianmu disini",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Custom Tabs to toggle Expense vs Income
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(theme.background)
                        .padding(4.dp)
                ) {
                    TabToggleItem(
                        text = "Pengeluaran 📉",
                        isSelected = isExpenseView,
                        themeColor = theme.primary,
                        onClick = { isExpenseView = true },
                        modifier = Modifier.weight(1f)
                    )
                    TabToggleItem(
                        text = "Pemasukan 📈",
                        isSelected = !isExpenseView,
                        themeColor = theme.primary,
                        onClick = { isExpenseView = false },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Analysis Pivot: Categories vs Tags vs Members
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val pivotList = listOf(
                        "CATEGORY" to "Kategori 🍰",
                        "TAG" to "Hashtags 🏷️",
                        "MEMBER" to "Anggota 👪"
                    )
                    pivotList.forEach { (option, label) ->
                        val isSel = analysisType == option
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) theme.accent else theme.background)
                                .clickable { analysisType = option }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.White else Color.Gray
                            )
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 60.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Interactive Pie Chart card for visual representation
                if (analysisType == "CATEGORY") {
                    item {
                        Card(
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                                .fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .background(theme.getCardBrush())
                                    .padding(16.dp)
                            ) {
                                Text(
                                    "Proporsi Kategori 🧁",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = theme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                // Custom donut
                                CuteDonutChart(transactions = activeTransactions)
                            }
                        }
                    }
                }

                // Bar Trend analysis card
                if (isExpenseView && analysisType == "CATEGORY") {
                    item {
                        Card(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .background(theme.getCardBrush())
                                    .padding(16.dp)
                            ) {
                                Text(
                                    "Tren Pengeluaran Bulanan 🌿",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = theme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                CuteMonthlyBarChart(
                                    monthlyTotals = monthlyTrend,
                                    themeColor = theme.accent
                                )
                            }
                        }
                    }
                }

                // Breakdown list headers
                item {
                    val summaryTitle = when (analysisType) {
                        "TAG" -> "Rangkuman Analisis Tags Terpopuler #️⃣"
                        "MEMBER" -> "Kontribusi Pengeluaran Anggota Keluarga 👪"
                        else -> "Rangkuman Kategori Terbesar 🍪"
                    }
                    Text(
                        text = summaryTitle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.onSurface,
                        modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 4.dp)
                    )
                }

                if (activeTransactions.isEmpty() || (analysisType == "TAG" && tagBreakdown.isEmpty())) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (analysisType == "TAG") "Belum ada transaksi dengan #tags di bulan ini."
                                       else "Belum melihat data sirkulasi bulan ini.",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else {
                    when (analysisType) {
                        "CATEGORY" -> {
                            val grouped = activeTransactions.groupBy { it.categoryName }.map { (name, txs) ->
                                val sum = txs.sumOf { it.amount }
                                val progress = if (totalActive > 0) (sum / totalActive).toFloat() else 0f
                                Triple(name, sum, progress)
                            }.sortedByDescending { it.second }

                            items(grouped) { (name, sum, progress) ->
                                CategoryBreakdownItem(
                                    categoryName = name,
                                    totalAmount = sum,
                                    percentageFloat = progress,
                                    themeColor = theme.primary
                                )
                            }
                        }
                        "TAG" -> {
                            items(tagBreakdown) { (tagName, sum, progress) ->
                                CategoryBreakdownItem(
                                    categoryName = tagName,
                                    totalAmount = sum,
                                    percentageFloat = progress,
                                    themeColor = theme.accent
                                )
                            }
                        }
                        "MEMBER" -> {
                            items(memberBreakdown) { (mName, sum, progress) ->
                                CategoryBreakdownItem(
                                    categoryName = mName,
                                    totalAmount = sum,
                                    percentageFloat = progress,
                                    themeColor = theme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TabToggleItem(
    text: String,
    isSelected: Boolean,
    themeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) themeColor else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color.Gray,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

@Composable
fun CategoryBreakdownItem(
    categoryName: String,
    totalAmount: Double,
    percentageFloat: Float,
    themeColor: Color
) {
    val currencyFormatter = DecimalFormat("#,###")

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = categoryName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5E4E52)
                    )
                    Text(
                        text = "Rp${currencyFormatter.format(totalAmount)} (${(percentageFloat * 100).toInt()}%)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = themeColor
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Custom rounded Linear progress indicators
                LinearProgressIndicator(
                    progress = { percentageFloat },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = themeColor,
                    trackColor = themeColor.copy(alpha = 0.15f),
                )
            }
        }
    }
}
