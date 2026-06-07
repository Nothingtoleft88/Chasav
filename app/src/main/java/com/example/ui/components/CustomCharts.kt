package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionEntity
import com.example.ui.theme.CuteCategory
import java.text.DecimalFormat

data class ChartSlice(
    val categoryName: String,
    val amount: Double,
    val color: Color,
    val percentage: Float
)

@Composable
fun CuteDonutChart(
    transactions: List<TransactionEntity>,
    modifier: Modifier = Modifier
) {
    val expenseTransactions = transactions.filter { it.type == "EXPENSE" }
    val totalExpense = expenseTransactions.sumOf { it.amount }

    if (expenseTransactions.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🌸", fontSize = 42.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Belum ada pengeluaran dicatat!",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
        return
    }

    // Process categories
    val categoryGroups = expenseTransactions.groupBy { it.categoryName }
    val slices = categoryGroups.map { (catName, txList) ->
        val catSum = txList.sumOf { it.amount }
        val sampleColorHex = txList.firstOrNull()?.categoryColorHex ?: "#BFFCC6"
        val color = try {
            Color(android.graphics.Color.parseColor(sampleColorHex))
        } catch (e: Exception) {
            Color(0xFFFFB7B2)
        }
        val pct = if (totalExpense > 0) (catSum / totalExpense).toFloat() else 0f
        ChartSlice(catName, catSum, color, pct)
    }.sortedByDescending { it.amount }

    // Animatable progress for radial sweep
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(transactions) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
    }

    val currencyFormatter = DecimalFormat("#,###")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // The Donut Canvas
        Box(
            modifier = Modifier
                .size(140.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 32f
                val diameter = size.minDimension - strokeWidth
                val topLeftOffset = Offset(
                    (size.width - diameter) / 2,
                    (size.height - diameter) / 2
                )
                val rectSize = Size(diameter, diameter)

                var currentStartAngle = -90f

                slices.forEach { slice ->
                    val sweepAngle = slice.percentage * 360f * animProgress.value
                    if (sweepAngle > 0) {
                        drawArc(
                            color = slice.color,
                            startAngle = currentStartAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = topLeftOffset,
                            size = rectSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                        currentStartAngle += sweepAngle
                    }
                }
            }

            // Inner Center Labels
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Pengeluaran",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Rp${currencyFormatter.format(totalExpense)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5E4E52)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Legend Breakdown List
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            slices.take(4).forEach { slice ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(slice.color, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = slice.categoryName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF5E4E52),
                            maxLines = 1,
                        )
                    }
                    Text(
                        text = "${(slice.percentage * 100).toInt()}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
            }
            if (slices.size > 4) {
                Text(
                    text = "+ ${slices.size - 4} kategori lainnya",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

@Composable
fun CuteMonthlyBarChart(
    monthlyTotals: List<Pair<String, Double>>, // Pairs of "Month" to "Total Expense"
    themeColor: Color,
    modifier: Modifier = Modifier
) {
    if (monthlyTotals.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Tidak ada data trend bulanan", color = Color.Gray, fontSize = 12.sp)
        }
        return
    }

    val maxAmount = monthlyTotals.maxOf { it.second }.coerceAtLeast(10000.0)
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(monthlyTotals) {
        animProgress.animateTo(1f, tween(1000))
    }

    val currencyFormatter = DecimalFormat("#,###")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            monthlyTotals.forEach { (label, value) ->
                val barHeightFraction = (value / maxAmount).toFloat() * animProgress.value

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = "Rp${currencyFormatter.format(value / 1000)}k",
                        fontSize = 9.sp,
                        color = themeColor.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(barHeightFraction.coerceIn(0.05f, 1f))
                            .width(22.dp)
                            .background(themeColor, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
