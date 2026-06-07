package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.DEFAULT_CATEGORIES
import com.example.ui.viewmodel.FinanceViewModel
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetsScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val theme by viewModel.currentTheme.collectAsStateWithLifecycle()
    val budgets by viewModel.currentMonthBudgets.collectAsStateWithLifecycle()
    val transactions by viewModel.currentMonthTransactions.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val totalExpense = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }

    val globalBudget = budgets.firstOrNull { it.categoryName == "GLOBAL" }?.amount ?: 1500000.0
    val remainingValue = (globalBudget - totalExpense).coerceAtLeast(0.0)
    val percentageUsed = if (globalBudget > 0) (totalExpense / globalBudget).toFloat() else 0f

    var isEditingGlobalBudget by remember { mutableStateOf(false) }
    var editAmountText by remember { mutableStateOf("") }

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
            // Header panel
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Text(
                    "Anggaran Bulan Ini 🌿",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = theme.onSurface
                )
                Text(
                    "Batasi pengeluaranmu agar tanaman hiasmu tetap mekar!",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 60.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Interactive Plant Card
                item {
                    Card(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, top = 12.dp)
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .background(theme.getCardBrush())
                                .padding(18.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Status Tanaman Keuanganmu 🌱",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.onSurface.copy(alpha = 0.6f)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Plant illustration representing spend ratio
                            Box(
                                modifier = Modifier.size(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                when {
                                    percentageUsed >= 0.9f -> {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("🥀", fontSize = 65.sp) // Withered flower
                                        }
                                    }
                                    percentageUsed >= 0.7f -> {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("🍂", fontSize = 65.sp) // Falling leaves
                                        }
                                    }
                                    percentageUsed >= 0.4f -> {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("🌿", fontSize = 65.sp) // Growing plant
                                        }
                                    }
                                    else -> {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("🌸", fontSize = 65.sp) // Blooming cherry blossom tree!
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = when {
                                    percentageUsed >= 0.9f -> "Aduh! Tanamanmu layu kering! Pengeluaran lewat batas kritis. Yuk puasa belanja! 🥺"
                                    percentageUsed >= 0.7f -> "Sst! Tanaman butuh cairan! Pengeluaran sudah mengancam ketahanan kantong. 🥛"
                                    percentageUsed >= 0.4f -> "Tanamanmu sehat alami! Pertahankan pengeluaran seimbang ya Kak! 🍃"
                                    else -> "Wah! Tanamanmu berbunga Sakura indah karena Kakak cerdas berhemat! 🌸✨"
                                },
                                style = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.onSurface,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                        }
                    }
                }

                // Global Budget settings
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
                                .padding(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        "Limit Anggaran Global",
                                        fontSize = 11.sp,
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        "Rp${currencyFormatter.format(globalBudget)}",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        color = theme.onSurface
                                    )
                                }

                                if (!isEditingGlobalBudget) {
                                    Button(
                                        onClick = {
                                            editAmountText = globalBudget.toInt().toString()
                                            isEditingGlobalBudget = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                    ) {
                                        Text("Atur", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }

                            if (isEditingGlobalBudget) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    TextField(
                                        value = editAmountText,
                                        onValueChange = { editAmountText = it },
                                        placeholder = { Text("cth: 1500000") },
                                        singleLine = true,
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = theme.background,
                                            unfocusedContainerColor = theme.background,
                                            focusedIndicatorColor = theme.accent,
                                            unfocusedIndicatorColor = Color.Transparent
                                        ),
                                        textStyle = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = theme.onSurface),
                                        modifier = Modifier.weight(1f)
                                    )

                                    Button(
                                        onClick = {
                                            val amt = editAmountText.toDoubleOrNull() ?: 0.0
                                            if (amt > 0) {
                                                viewModel.setBudgetLimit("GLOBAL", amt)
                                                isEditingGlobalBudget = false
                                                Toast.makeText(context, "Limit anggaran global diperbarui! ✨", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Jumlah tidak valid!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = theme.accent),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Simpan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Overall progress bar
                            LinearProgressIndicator(
                                progress = { percentageUsed.coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp)),
                                color = if (percentageUsed >= 0.9f) Color(0xFFFF6584) else theme.primary,
                                trackColor = theme.primary.copy(alpha = 0.15f)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Terpakai: Rp${currencyFormatter.format(totalExpense)} (${(percentageUsed * 100).toInt()}%)",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    "Sisa: Rp${currencyFormatter.format(remainingValue)}",
                                    fontSize = 11.sp,
                                    color = theme.accent,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Category budget tracking
                item {
                    Text(
                        "Anggaran per Kategori 🍭",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.onSurface,
                        modifier = Modifier.padding(horizontal = 18.dp)
                    )
                }

                // List of customizable expense budgets
                val expenseCategories = DEFAULT_CATEGORIES.filter { it.isExpense }
                items(expenseCategories) { category ->
                    val budget = budgets.find { it.categoryName == category.name }?.amount ?: 500000.0
                    val spentByCat = transactions.filter { it.categoryName == category.name && it.type == "EXPENSE" }.sumOf { it.amount }
                    val ratio = if (budget > 0) (spentByCat / budget).toFloat() else 0f

                    CategoryBudgetItem(
                        category = category,
                        budgetAmount = budget,
                        spentAmount = spentByCat,
                        ratio = ratio,
                        themeColor = theme.primary,
                        onSetLimit = { amount ->
                            viewModel.setBudgetLimit(category.name, amount)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryBudgetItem(
    category: com.example.ui.theme.CuteCategory,
    budgetAmount: Double,
    spentAmount: Double,
    ratio: Float,
    themeColor: Color,
    onSetLimit: (Double) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var amountValueText by remember { mutableStateOf("") }
    val formatter = DecimalFormat("#,###")

    val categoryColor = try {
        Color(android.graphics.Color.parseColor(category.colorHex))
    } catch (e: Exception) {
        themeColor
    }

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Round Category icon
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(categoryColor.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = mapIconStringToVector(category.iconName),
                        contentDescription = category.name,
                        tint = categoryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        category.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5E4E52)
                    )
                    Text(
                        "Sisa: Rp${formatter.format((budgetAmount - spentAmount).coerceAtLeast(0.0))}",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Limit: Rp${formatter.format(budgetAmount)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5E4E52)
                    )
                    Text(
                        text = "Rp${formatter.format(spentAmount)} terpakai",
                        fontSize = 10.sp,
                        color = Color.LightGray,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = {
                        amountValueText = budgetAmount.toInt().toString()
                        isEditing = !isEditing
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Edit Category budget")
                }
            }

            AnimatedVisibility(visible = isEditing) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextField(
                            value = amountValueText,
                            onValueChange = { amountValueText = it },
                            placeholder = { Text("Atur limit baru...", fontSize = 12.sp) },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.FadedGrayOrTransparent(), // custom lightweight
                                unfocusedContainerColor = Color.FadedGrayOrTransparent(),
                                focusedIndicatorColor = categoryColor
                            ),
                            textStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = {
                                val amt = amountValueText.toDoubleOrNull() ?: 0.0
                                if (amt >= 0) {
                                    onSetLimit(amt)
                                    isEditing = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = categoryColor),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                        ) {
                            Text("Ubah", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { ratio.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (ratio >= 0.9f) Color(0xFFFF6584) else categoryColor,
                trackColor = categoryColor.copy(alpha = 0.1f)
            )
        }
    }
}

// Simple color helper extension
@Composable
fun Color.Companion.FadedGrayOrTransparent(): Color {
    return Color(0xFFF7F5F6)
}
