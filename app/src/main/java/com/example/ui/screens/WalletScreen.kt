package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.TransactionEntity
import com.example.ui.companion.CuteMascotBubble
import com.example.ui.companion.MascotType
import com.example.ui.theme.CuteTheme
import com.example.ui.viewmodel.FinanceViewModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WalletScreen(
    viewModel: FinanceViewModel,
    onNavigateToAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme by viewModel.currentTheme.collectAsStateWithLifecycle()
    val mascot by viewModel.currentMascot.collectAsStateWithLifecycle()
    val transactions by viewModel.currentMonthTransactions.collectAsStateWithLifecycle()
    val budgets by viewModel.currentMonthBudgets.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDateMillis.collectAsStateWithLifecycle()

    val totalIncome = transactions.filter { it.type == "INCOME" }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    val balance = totalIncome - totalExpense

    val globalBudget = budgets.firstOrNull { it.categoryName == "GLOBAL" }?.amount ?: 1500000.0
    val spendingRatio = if (globalBudget > 0) totalExpense / globalBudget else 0.0

    val currencyFormatter = DecimalFormat("#,###")

    Scaffold(
        containerColor = theme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = theme.accent,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 16.dp, end = 8.dp)
                    .testTag("add_transaction_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction", modifier = Modifier.size(28.dp))
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Header
            CuteDashboardHeader(theme = theme, selectedDate = selectedDate, onDateSelected = { viewModel.selectDate(it) })

            // --- Cute Live Ledger Book Selector ---
            val currentLedger by viewModel.currentLedger.collectAsStateWithLifecycle()
            val ledgers = listOf("Sari-sari (Harian)", "Pekerjaan 💼", "Keluarga 🏡", "Liburan ✈️")

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ledgers) { lName ->
                    val isSelected = currentLedger == lName
                    val chipBg = if (isSelected) theme.accent else Color(0x10FF8585)
                    val chipTextColor = if (isSelected) Color.White else theme.onSurface

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(chipBg)
                            .clickable { viewModel.selectLedger(lName) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = (if (isSelected) "🧁 " else "") + lName,
                                color = chipTextColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Balance Summary Panel
                item {
                    val walletNames = listOf("Dompet Utama", "Tabungan", "E-Money", "ShopeePay", "Kartu Kredit 💳")
                    var assetSum = 0.0
                    var debtSum = 0.0
                    
                    walletNames.forEach { wName ->
                        val wBal = viewModel.getWalletBalance(wName)
                        if (wBal > 0) {
                            assetSum += wBal
                        } else if (wBal < 0) {
                            debtSum += kotlin.math.abs(wBal)
                        }
                    }
                    val netAsset = assetSum - debtSum

                    Card(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(32.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .background(theme.getCardBrush())
                                .padding(20.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                "Total Uang Bersih (Net Worth) 🌸",
                                fontSize = 11.sp,
                                color = theme.onSurface.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Rp${currencyFormatter.format(netAsset)}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = theme.primary
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Asset vs Debt metrics
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.White.copy(alpha = 0.45f))
                                        .padding(10.dp)
                                ) {
                                    MiniStatsColumn(
                                        label = "Total Aset",
                                        amount = assetSum,
                                        color = Color(0xFF4CAF50),
                                        icon = Icons.Default.AccountBalanceWallet
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.White.copy(alpha = 0.45f))
                                        .padding(10.dp)
                                ) {
                                    MiniStatsColumn(
                                        label = "Total Utang",
                                        amount = debtSum,
                                        color = Color(0xFFFF6584),
                                        icon = Icons.Default.CreditCard
                                    )
                                }
                            }
                        }
                    }
                }

                // Interactive Multi-Wallet Accounts Sliding List
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Dompet & Rekening Kakak 🎒",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.onSurface,
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 2.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        val accountTypes = mapOf(
                            "Dompet Utama" to Pair("💵 Tunai", "Tunai"),
                            "Tabungan" to Pair("🏦 Bank Mandiri", "Rekening Bank"),
                            "E-Money" to Pair("💳 GoPay", "E-Wallet"),
                            "ShopeePay" to Pair("📱 ShopeePay", "Virtual E-Wallet"),
                            "Kartu Kredit 💳" to Pair("🚨 Visa Kredit", "Kewajiban")
                        )

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(accountTypes.keys.toList()) { name ->
                                val wBal = viewModel.getWalletBalance(name)
                                val typeInfo = accountTypes[name] ?: Pair("💰 Lainnya", "Lainnya")
                                
                                Card(
                                    modifier = Modifier
                                        .width(150.dp)
                                        .clickable {  },
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(18.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(typeInfo.first, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = theme.onSurface)
                                        Text(typeInfo.second, fontSize = 9.sp, color = Color.Gray)
                                        Spacer(modifier = Modifier.height(14.dp))
                                        
                                        val amtColor = if (wBal >= 0) Color(0xFF4CAF50) else Color(0xFFFF6584)
                                        Text(
                                            text = "Rp${currencyFormatter.format(wBal)}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Black,
                                            color = amtColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Interactive Mascot Advice Block
                item {
                    CuteMascotBubble(
                        mascotType = mascot,
                        spendingPercentage = spendingRatio,
                        hasTransactions = transactions.isNotEmpty()
                    )
                }

                // Today Status / Header bar
                item {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 18.dp, vertical = 2.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daftar Transaksi Bulanan 📖",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.onSurface
                        )
                        Text(
                            text = "${transactions.size} transaksi",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (transactions.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("🥕", fontSize = 48.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Wah, catatan bulan ini masih kosong!\nYuk tap tombol '+' untuk mencatat jajanmu.",
                                    fontSize = 13.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp,
                                    modifier = Modifier.padding(horizontal = 32.dp)
                                )
                            }
                        }
                    }
                } else {
                    // Transaction List entries
                    items(
                        items = transactions,
                        key = { it.id }
                    ) { tx ->
                        TransactionRowItem(
                            transaction = tx,
                            themeColor = theme.primary,
                            onDelete = { viewModel.deleteTransaction(tx) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CuteDashboardHeader(
    theme: CuteTheme,
    selectedDate: Long,
    onDateSelected: (Long) -> Unit
) {
    val sdfMonth = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
    val currentMonthText = sdfMonth.format(Date(selectedDate))

    val calendar = Calendar.getInstance()
    val todayDate = calendar.get(Calendar.DAY_OF_MONTH)
    val todayMonth = calendar.get(Calendar.MONTH)
    val todayYear = calendar.get(Calendar.YEAR)

    // Calendar slide for the past 7 days and next 3 days
    val datesRange = remember {
        val list = mutableListOf<Long>()
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -5)
        repeat(10) {
            list.add(cal.timeInMillis)
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Bear / Cute Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFD6D6))
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🐻", fontSize = 20.sp)
                }
                Column {
                    Text(
                        text = "HELLO, KAKAK! ✨",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = currentMonthText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = theme.onSurface
                    )
                }
            }
            // Simple calendar icon to reset to today
            IconButton(
                onClick = { onDateSelected(System.currentTimeMillis()) },
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(2.dp)
            ) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = "Kembali ke Hari Ini",
                    tint = theme.accent,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Cute Slider dates row
        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(datesRange) { timeMillis ->
                val dateVal = Date(timeMillis)
                val daySdf = SimpleDateFormat("dd", Locale.getDefault())
                val dayNameSdf = SimpleDateFormat("EEE", Locale("id", "ID"))
                val dayNum = daySdf.format(dateVal)
                val dayName = dayNameSdf.format(dateVal)

                // Check if this date is selected
                val isSelected = isSameDay(timeMillis, selectedDate)

                val cardBg by animateColorAsState(
                    targetValue = if (isSelected) theme.primary else theme.cardBackground,
                    animationSpec = spring(),
                    label = "cal_card_color"
                )
                val textColor = if (isSelected) Color.White else theme.onSurface

                Box(
                    modifier = Modifier
                        .width(55.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(cardBg)
                        .clickable { onDateSelected(timeMillis) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = dayName.uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = dayNum,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = textColor
                        )
                        if (isSameDay(timeMillis, System.currentTimeMillis())) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .background(if (isSelected) Color.White else theme.accent, CircleShape)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionRowItem(
    transaction: TransactionEntity,
    themeColor: Color,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val formatter = DecimalFormat("#,###")
    val isExpense = transaction.type == "EXPENSE"

    val dateStr = SimpleDateFormat("dd MMM, HH:mm", Locale("id", "ID")).format(Date(transaction.dateMillis))

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDeleteConfirm = !showDeleteConfirm },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(14.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Cute round Category Icon Badge
                val catColor = try {
                    Color(android.graphics.Color.parseColor(transaction.categoryColorHex))
                } catch (e: Exception) {
                    themeColor
                }

                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(catColor.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = mapIconStringToVector(transaction.categoryIcon),
                        contentDescription = transaction.categoryName,
                        tint = catColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = transaction.categoryName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5E4E52)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (transaction.note.isNotEmpty()) transaction.note else dateStr,
                        fontSize = 11.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = (if (isExpense) "-" else "+") + "Rp${formatter.format(transaction.amount)}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isExpense) Color(0xFFFF94B0) else Color(0xFF8AE0C5)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = transaction.walletName,
                        fontSize = 10.sp,
                        color = Color.LightGray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        AnimatedVisibility(visible = showDeleteConfirm) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 4.dp, start = 12.dp, end = 12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hapus transaksi ini?",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = { showDeleteConfirm = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
                ) {
                    Text("Batal", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Button(
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6584)),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Hapus", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MiniStatsColumn(
    label: String,
    amount: Double,
    color: Color,
    icon: ImageVector
) {
    val formatter = DecimalFormat("#,###")
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                fontSize = 10.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Rp${formatter.format(amount)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5E4E52)
            )
        }
    }
}

fun isSameDay(time1: Long, time2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = time2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

fun mapIconStringToVector(name: String): ImageVector {
    return when (name) {
        "restaurant" -> Icons.Default.Restaurant
        "shopping_bag" -> Icons.Default.ShoppingBag
        "directions_car" -> Icons.Default.DirectionsCar
        "sports_esports" -> Icons.Default.SportsEsports
        "receipt_long" -> Icons.Default.ReceiptLong
        "medical_services" -> Icons.Default.MedicalServices
        "school" -> Icons.Default.School
        "card_giftcard" -> Icons.Default.CardGiftcard
        "category" -> Icons.Default.Category
        "payments" -> Icons.Default.Payments
        "trending_up" -> Icons.Default.TrendingUp
        "redeem" -> Icons.Default.Redeem
        "monetization_on" -> Icons.Default.MonetizationOn
        "savings" -> Icons.Default.Savings
        else -> Icons.Default.Star
    }
}
