package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.CuteCategory
import com.example.ui.theme.DEFAULT_CATEGORIES
import com.example.ui.viewmodel.FinanceViewModel
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: FinanceViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme by viewModel.currentTheme.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // --- State Hook Fields ---
    var transactionType by remember { mutableStateOf("EXPENSE") } // "EXPENSE", "INCOME", "TRANSFER"
    val isExpense = transactionType == "EXPENSE"

    val categories = remember(transactionType) {
        if (transactionType == "TRANSFER") {
            listOf(CuteCategory("Transfer", "swap_horiz", "#A0C4FF", false))
        } else {
            DEFAULT_CATEGORIES.filter { it.isExpense == isExpense }
        }
    }
    var selectedCategory by remember(transactionType) {
        mutableStateOf(categories.firstOrNull())
    }

    var amountString by remember { mutableStateOf("0") }
    var note by remember { mutableStateOf("") }
    var walletName by remember { mutableStateOf("Dompet Utama") }
    var transferToWallet by remember { mutableStateOf("Tabungan") }
    var ledgerName by remember { mutableStateOf("Sari-sari (Harian)") }
    var memberName by remember { mutableStateOf("Pribadi") }
    var currencyCode by remember { mutableStateOf("IDR") }
    var exchangeRate by remember { mutableStateOf(1.0) }
    var tagsInput by remember { mutableStateOf("") }
    var isPeriodic by remember { mutableStateOf(false) }
    var periodInterval by remember { mutableStateOf("BULANAN") }

    // --- Dropdown States ---
    var showWalletDropdown by remember { mutableStateOf(false) }
    var showTransferTargetDropdown by remember { mutableStateOf(false) }
    var showLedgerDropdown by remember { mutableStateOf(false) }
    var showMemberDropdown by remember { mutableStateOf(false) }
    var showCurrencyDropdown by remember { mutableStateOf(false) }
    var showPeriodDropdown by remember { mutableStateOf(false) }

    val currencyFormatter = DecimalFormat("#,###")

    val wallets = listOf("Dompet Utama", "Tabungan", "E-Money", "ShopeePay", "Kartu Kredit 💳")
    val ledgers = listOf("Sari-sari (Harian)", "Pekerjaan 💼", "Keluarga 🏡", "Liburan ✈️")
    val members = listOf("Pribadi", "Ibu 👩", "Ayah 👨", "Adek 👶", "Teman 👥")
    val currencies = listOf(
        com.example.ui.viewmodel.CurrencyInfo("IDR", "Rp", 1.0),
        com.example.ui.viewmodel.CurrencyInfo("USD", "$", 16300.0),
        com.example.ui.viewmodel.CurrencyInfo("SGD", "S$", 12100.0),
        com.example.ui.viewmodel.CurrencyInfo("JPY", "¥", 105.0),
        com.example.ui.viewmodel.CurrencyInfo("EUR", "€", 17500.0)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catat Keuangan Imut 💕", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = theme.onSurface) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = theme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = theme.background)
            )
        },
        containerColor = theme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Scrollable fields form container (This prevents overflow gracefully!)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(androidx.compose.foundation.rememberScrollState())
            ) {
                // Amount Input Display Area
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Type Selector Row (Suports 3 Tabs: EXPENSE, INCOME, TRANSFER)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(theme.background)
                                .padding(3.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            val activeColor = theme.primary
                            val inactiveColor = Color.Transparent

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (transactionType == "EXPENSE") activeColor else inactiveColor)
                                    .clickable { transactionType = "EXPENSE" }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "💸 Keluar",
                                    color = if (transactionType == "EXPENSE") Color.White else Color.Gray,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (transactionType == "INCOME") activeColor else inactiveColor)
                                    .clickable { transactionType = "INCOME" }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "💰 Masuk",
                                    color = if (transactionType == "INCOME") Color.White else Color.Gray,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (transactionType == "TRANSFER") activeColor else inactiveColor)
                                    .clickable { transactionType = "TRANSFER" }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "🔄 Transfer",
                                    color = if (transactionType == "TRANSFER") Color.White else Color.Gray,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Input display Row featuring Currency rates
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = selectedCategory?.name ?: "Pilih Kategori",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Bold
                                )
                                if (currencyCode != "IDR") {
                                    val idrVal = (amountString.toDoubleOrNull() ?: 0.0) * exchangeRate
                                    Text(
                                        text = "≈ Rp${currencyFormatter.format(idrVal)} IDR",
                                        fontSize = 10.sp,
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            
                            val symbol = currencies.firstOrNull { it.code == currencyCode }?.symbol ?: ""
                            val numVal = amountString.toDoubleOrNull() ?: 0.0
                            Text(
                                text = "$symbol${currencyFormatter.format(numVal)}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = when (transactionType) {
                                    "EXPENSE" -> Color(0xFFFF6584)
                                    "INCOME" -> Color(0xFF1EAD8E)
                                    else -> Color(0xFF2C97DE)
                                }
                            )
                        }
                    }
                }

                // Currency selector pill row
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 2.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    currencies.forEach { curr ->
                        val isSel = currencyCode == curr.code
                        val pillBg = if (isSel) theme.accent else Color.White
                        val pillText = if (isSel) Color.White else Color.Gray
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(pillBg)
                                .clickable {
                                    currencyCode = curr.code
                                    exchangeRate = curr.rateToIdr
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = curr.code,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = pillText
                            )
                        }
                    }
                }

                // Categories Grid Area
                if (transactionType != "TRANSFER") {
                    Text(
                        text = "Pilih Kategori 🍧",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.onSurface,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp)
                    )

                    // Categories Grid via row chunks to avoid verticalScroll crash!
                    val chunked = categories.chunked(4)
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        chunked.forEach { rowList ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                rowList.forEach { cat ->
                                    val isSelected = selectedCategory?.name == cat.name
                                    val catColor = try {
                                        Color(android.graphics.Color.parseColor(cat.colorHex))
                                    } catch (e: Exception) {
                                        theme.primary
                                    }

                                    val scaleModifier = if (isSelected) Modifier.background(catColor.copy(alpha = 0.3f))
                                                        else Modifier.background(Color.White)

                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1.1f)
                                            .clip(RoundedCornerShape(16.dp))
                                            .clickable { selectedCategory = cat },
                                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                                        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 0.5.dp)
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .then(scaleModifier)
                                                .padding(4.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(CircleShape)
                                                    .background(catColor.copy(alpha = 0.2f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = mapIconStringToVector(cat.iconName),
                                                    contentDescription = cat.name,
                                                    tint = catColor,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = cat.name,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = theme.onSurface,
                                                maxLines = 1,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                                // Pad empty columns if less than 4 items
                                if (rowList.size < 4) {
                                    val gap = 4 - rowList.size
                                    for (i in 0 until gap) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }

                // Advanced forms: Memo, Wallets, Ledgers, Members, tags
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Note Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Catatan 📝",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.onSurface,
                                modifier = Modifier.width(90.dp)
                            )
                            TextField(
                                value = note,
                                onValueChange = { note = it },
                                placeholder = { Text("Makan soto ayam...", fontSize = 12.sp) },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = theme.primary,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                textStyle = TextStyle(fontSize = 12.sp, color = theme.onSurface),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Wallets choosing
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (transactionType == "TRANSFER") "Rekening Asal" else "Dompet 💳",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.onSurface,
                                modifier = Modifier.width(90.dp)
                            )

                            // Source Wallet
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(theme.background)
                                    .clickable { showWalletDropdown = true }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = walletName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = theme.onSurface
                                    )
                                    Text("▼", fontSize = 10.sp, color = Color.Gray)
                                }

                                DropdownMenu(
                                    expanded = showWalletDropdown,
                                    onDismissRequest = { showWalletDropdown = false }
                                ) {
                                    wallets.forEach { wallet ->
                                        DropdownMenuItem(
                                            text = { Text(wallet, fontSize = 12.sp) },
                                            onClick = {
                                                walletName = wallet
                                                showWalletDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Target Wallet for Inter-account Transfers
                        if (transactionType == "TRANSFER") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Ke Rekening",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = theme.onSurface,
                                    modifier = Modifier.width(90.dp)
                                )

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(theme.background)
                                        .clickable { showTransferTargetDropdown = true }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = transferToWallet,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = theme.onSurface
                                        )
                                        Text("▼", fontSize = 10.sp, color = Color.Gray)
                                    }

                                    DropdownMenu(
                                        expanded = showTransferTargetDropdown,
                                        onDismissRequest = { showTransferTargetDropdown = false }
                                    ) {
                                        wallets.filter { it != walletName }.forEach { wallet ->
                                            DropdownMenuItem(
                                                text = { Text(wallet, fontSize = 12.sp) },
                                                onClick = {
                                                    transferToWallet = wallet
                                                    showTransferTargetDropdown = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Book Ledger Choice Menu
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Buku Catatan",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.onSurface,
                                modifier = Modifier.width(90.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(theme.background)
                                    .clickable { showLedgerDropdown = true }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(ledgerName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = theme.onSurface)
                                    Text("▼", fontSize = 10.sp, color = Color.Gray)
                                }

                                DropdownMenu(
                                    expanded = showLedgerDropdown,
                                    onDismissRequest = { showLedgerDropdown = false }
                                ) {
                                    ledgers.forEach { ledg ->
                                        DropdownMenuItem(
                                            text = { Text(ledg, fontSize = 12.sp) },
                                            onClick = {
                                                ledgerName = ledg
                                                showLedgerDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Family Member Choice
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Anggota",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.onSurface,
                                modifier = Modifier.width(90.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(theme.background)
                                    .clickable { showMemberDropdown = true }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(memberName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = theme.onSurface)
                                    Text("▼", fontSize = 10.sp, color = Color.Gray)
                                }

                                DropdownMenu(
                                    expanded = showMemberDropdown,
                                    onDismissRequest = { showMemberDropdown = false }
                                ) {
                                    members.forEach { mem ->
                                        DropdownMenuItem(
                                            text = { Text(mem, fontSize = 12.sp) },
                                            onClick = {
                                                memberName = mem
                                                showMemberDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Hashtag contextual tags Input bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Konteks Tags",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.onSurface,
                                modifier = Modifier.width(90.dp)
                            )
                            TextField(
                                value = tagsInput,
                                onValueChange = { tagsInput = it },
                                placeholder = { Text("e.g. kopi, liburan, warung (pisah koma)", fontSize = 11.sp) },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = theme.primary,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                textStyle = TextStyle(fontSize = 11.sp, color = theme.onSurface),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Periodic Transaction Scheduling
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Auto-Ulangi Transaksi? ⏰",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.onSurface
                            )
                            Switch(
                                checked = isPeriodic,
                                onCheckedChange = { isPeriodic = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = theme.accent)
                            )
                        }

                        if (isPeriodic) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Frekuensi Ulang",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = theme.onSurface,
                                    modifier = Modifier.width(90.dp)
                                )

                                val intervals = listOf("HARIAN", "MINGGUAN", "BULANAN")
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(theme.background)
                                        .clickable { showPeriodDropdown = true }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(periodInterval, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = theme.onSurface)
                                        Text("▼", fontSize = 10.sp, color = Color.Gray)
                                    }

                                    DropdownMenu(
                                        expanded = showPeriodDropdown,
                                        onDismissRequest = { showPeriodDropdown = false }
                                    ) {
                                        intervals.forEach { iVal ->
                                            DropdownMenuItem(
                                                text = { Text(iVal, fontSize = 12.sp) },
                                                onClick = {
                                                    periodInterval = iVal
                                                    showPeriodDropdown = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Keyboard at the bottom
            CuteCalculatorKeyboard(
                amountStr = amountString,
                onAmountChange = { amountString = it },
                onSave = {
                    val finalAmount = amountString.toDoubleOrNull() ?: 0.0
                    val finalCat = selectedCategory
                    if (finalAmount <= 0.0) {
                        Toast.makeText(context, "Jumlah uang harus lebih besar dari 0!", Toast.LENGTH_SHORT).show()
                    } else if (finalCat == null && transactionType != "TRANSFER") {
                        Toast.makeText(context, "Silakan pilih salah satu kategori ya Kak!", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.addTransaction(
                            amount = finalAmount,
                            type = transactionType,
                            categoryName = if (transactionType == "TRANSFER") "Transfer Antar Rekening" else finalCat!!.name,
                            categoryIcon = if (transactionType == "TRANSFER") "swap_horiz" else finalCat!!.iconName,
                            categoryColorHex = if (transactionType == "TRANSFER") "#80C4FF" else finalCat!!.colorHex,
                            dateMillis = System.currentTimeMillis(),
                            note = note,
                            walletName = walletName,
                            transferToWallet = if (transactionType == "TRANSFER") transferToWallet else null,
                            ledgerName = ledgerName,
                            memberName = memberName,
                            currencyCode = currencyCode,
                            exchangeRate = exchangeRate,
                            tagsCsv = tagsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }.joinToString(","),
                            isPeriodic = isPeriodic,
                            periodInterval = if (isPeriodic) periodInterval else "NONE"
                        )
                        Toast.makeText(context, "Hore! Catatan berhasil ditambahkan! 🎉", Toast.LENGTH_SHORT).show()
                        onNavigateBack()
                    }
                },
                themeColor = theme.primary,
                accentColor = theme.accent
            )
        }
    }
}

@Composable
fun CuteCalculatorKeyboard(
    amountStr: String,
    onAmountChange: (String) -> Unit,
    onSave: () -> Unit,
    themeColor: Color,
    accentColor: Color
) {
    val buttons = listOf(
        listOf("1", "2", "3", "Del"),
        listOf("4", "5", "6", "C"),
        listOf("7", "8", "9", "0"),
        listOf(".", "00", "Simpan 🌸")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                row.forEach { char ->
                    val isSave = char.startsWith("Simpan")
                    val isAction = char == "Del" || char == "C"

                    val weight = if (isSave) 2f else 1f

                    val btnColor = when {
                        isSave -> accentColor
                        isAction -> themeColor.copy(alpha = 0.25f)
                        else -> themeColor.copy(alpha = 0.1f)
                    }
                    val txtColor = when {
                        isSave -> Color.White
                        isAction -> Color(0xFF5E4E52)
                        else -> Color(0xFF5E4E52)
                    }

                    Box(
                        modifier = Modifier
                            .weight(weight)
                            .height(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(btnColor)
                            .clickable {
                                handleKeyboardPress(char, amountStr, onAmountChange, onSave)
                            }
                            .testTag("calc_key_$char"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (char == "Del") {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = txtColor, modifier = Modifier.size(18.dp))
                        } else {
                            Text(
                                text = char,
                                fontSize = if (isSave) 14.sp else 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = txtColor
                            )
                        }
                    }
                }
            }
        }
    }
}

fun handleKeyboardPress(
    char: String,
    amountStr: String,
    onAmountChange: (String) -> Unit,
    onSave: () -> Unit
) {
    when (char) {
        "Simpan 🌸" -> onSave()
        "C" -> onAmountChange("0")
        "Del" -> {
            if (amountStr.length <= 1) {
                onAmountChange("0")
            } else {
                onAmountChange(amountStr.dropLast(1))
            }
        }
        "." -> {
            if (!amountStr.contains(".")) {
                onAmountChange(amountStr + ".")
            }
        }
        "00" -> {
            if (amountStr != "0") {
                onAmountChange(amountStr + "00")
            }
        }
        else -> {
            // Typing digital values
            if (amountStr == "0") {
                onAmountChange(char)
            } else {
                onAmountChange(amountStr + char)
            }
        }
    }
}
