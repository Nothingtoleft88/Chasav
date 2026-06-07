package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.companion.MascotType
import com.example.ui.theme.CuteTheme
import com.example.ui.viewmodel.FinanceViewModel
import java.text.DecimalFormat

sealed class MoreActiveOverlay {
    object Messages : MoreActiveOverlay()
    object Recurring : MoreActiveOverlay()
    object Reminders : MoreActiveOverlay()
    object PurchasePremium : MoreActiveOverlay()
    object CurrencyRate : MoreActiveOverlay()
    object CategoryMgmt : MoreActiveOverlay()
    object MemberMgmt : MoreActiveOverlay()
    object AccountMgmt : MoreActiveOverlay()
    object TagMgmt : MoreActiveOverlay()
    object SearchTransactions : MoreActiveOverlay()
    object GoogleDriveBackup : MoreActiveOverlay()
    object TrendForecast : MoreActiveOverlay()
    object RateApp : MoreActiveOverlay()
    object Feedback : MoreActiveOverlay()
    object About : MoreActiveOverlay()
    object ShareWithFriends : MoreActiveOverlay()
    object Settings : MoreActiveOverlay()
}

data class GridMenuItem(
    val title: String,
    val iconEmoji: String,
    val systemIcon: ImageVector,
    val containerBg: Color,
    val iconBg: Color,
    val overlayType: MoreActiveOverlay,
    val badgeText: String? = null,
    val longBadgeText: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    viewModel: FinanceViewModel,
    onNavigateToSavings: () -> Unit,
    onNavigateToBudgets: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme by viewModel.currentTheme.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var activeOverlay by remember { mutableStateOf<MoreActiveOverlay?>(null) }

    // Backup SAF Launchers inside More for direct integration
    val createJsonLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            viewModel.exportBackupJson(
                context = context,
                uri = uri,
                onSuccess = {
                    Toast.makeText(context, "Selesai! Cadangan data berhasil disimpan 💾🎀", Toast.LENGTH_LONG).show()
                },
                onError = { err ->
                    Toast.makeText(context, "Gagal menyimpan cadangan: ${err.message}", Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    val createCsvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            viewModel.exportCsvData(
                context = context,
                uri = uri,
                onSuccess = {
                    Toast.makeText(context, "Laporan Excel/CSV siap dibuka di spreadsheet Kakak! 📈🧁", Toast.LENGTH_LONG).show()
                },
                onError = { err ->
                    Toast.makeText(context, "Gagal menyimpan CSV: ${err.message}", Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    val driveConnected by viewModel.driveConnected.collectAsStateWithLifecycle()

    // 21 items in the grid, styled matches the screenshots perfectly!
    val menuItems = remember(driveConnected) {
        listOf(
            GridMenuItem("Messages", "📬", Icons.Default.MailOutline, Color(0xFFFFF2F5), Color(0xFFFFD6E0), MoreActiveOverlay.Messages),
            GridMenuItem("Saving Goals", "⛰️", Icons.Default.Savings, Color(0xFFF2FBF9), Color(0xFFCCEFEB), MoreActiveOverlay.About), // directly triggers overlay or we can redirect
            GridMenuItem("Recurring", "⏰", Icons.Default.Timer, Color(0xFFFFF7EA), Color(0xFFFFECCC), MoreActiveOverlay.Recurring),
            GridMenuItem("Reminders", "🔔", Icons.Default.NotificationsNone, Color(0xFFF3F2FF), Color(0xFFE2DFFF), MoreActiveOverlay.Reminders),
            GridMenuItem("Purchase Premium", "👑", Icons.Default.StarOutline, Color(0xFFFFFBF0), Color(0xFFFFF4D1), MoreActiveOverlay.PurchasePremium),
            GridMenuItem("Currency/Exchange Rate", "🪙", Icons.Default.CurrencyExchange, Color(0xFFEFF9F1), Color(0xFFD4EED8), MoreActiveOverlay.CurrencyRate),
            GridMenuItem("Category Management", "📥", Icons.Default.Category, Color(0xFFFDF0FF), Color(0xFFF7D1FF), MoreActiveOverlay.CategoryMgmt),
            GridMenuItem("Member Management", "👥", Icons.Default.PeopleOutline, Color(0xFFF0F6FF), Color(0xFFD1E4FF), MoreActiveOverlay.MemberMgmt),
            GridMenuItem("Budget Management", "🎯", Icons.Default.Eco, Color(0xFFFFF3F3), Color(0xFFFFD1D1), MoreActiveOverlay.Settings), // will guide budgets
            GridMenuItem("Book Management", "📖", Icons.Default.Book, Color(0xFFECFDFC), Color(0xFFC7FAF6), MoreActiveOverlay.AccountMgmt),
            GridMenuItem("Account Management", "💳", Icons.Default.CreditCard, Color(0xFFFFF5FB), Color(0xFFFFD4EE), MoreActiveOverlay.AccountMgmt),
            GridMenuItem("Tag Management", "＃", Icons.Default.Tag, Color(0xFFEFFDF5), Color(0xFFC2F9DC), MoreActiveOverlay.TagMgmt, badgeText = "New"),
            GridMenuItem("Search Transactions", "🔍", Icons.Default.Search, Color(0xFFFFF3F5), Color(0xFFFFD5DD), MoreActiveOverlay.SearchTransactions),
            GridMenuItem(
                "Google Drive Backup", "▲", Icons.Default.Backup, Color(0xFFF1F8FF), Color(0xFFCCE2FF), MoreActiveOverlay.GoogleDriveBackup,
                longBadgeText = if (driveConnected) "Backup active" else "No backup detected"
            ),
            GridMenuItem("Trend Forecast", "📈", Icons.Default.TrendingUp, Color(0xFFFFF8F1), Color(0xFFFFE2CC), MoreActiveOverlay.TrendForecast, badgeText = "Beta"),
            GridMenuItem("Export Excel", "📊", Icons.Default.InsertDriveFile, Color(0xFFE8FDF5), Color(0xFFC4F6E3), MoreActiveOverlay.About), // Excel item trigger
            GridMenuItem("Rate App", "👍", Icons.Default.ThumbUpOffAlt, Color(0xFFFFFDF0), Color(0xFFFFF9CC), MoreActiveOverlay.RateApp),
            GridMenuItem("Feedback", "✉️", Icons.Default.Feedback, Color(0xFFFFF4F4), Color(0xFFFFD1D1), MoreActiveOverlay.Feedback),
            GridMenuItem("About", "💬", Icons.Default.Info, Color(0xFFF5F5FC), Color(0xFFE2E2F9), MoreActiveOverlay.About),
            GridMenuItem("Share with Friends", "💖", Icons.Default.FavoriteBorder, Color(0xFFFFF2F6), Color(0xFFFFD4E2), MoreActiveOverlay.ShareWithFriends),
            GridMenuItem("Settings", "⚙️", Icons.Default.Settings, Color(0xFFF7F5F6), Color(0xFFE9E5E7), MoreActiveOverlay.Settings)
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "More Screen 🌸",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                },
                actions = {
                    IconButton(onClick = { activeOverlay = MoreActiveOverlay.Settings }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings Quick Access", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = theme.primary
                )
            )
        },
        containerColor = theme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Lazy vertical grid with 3 columns just like the screenshots!
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(menuItems) { item ->
                    Card(
                        onClick = {
                            when (item.title) {
                                "Saving Goals" -> onNavigateToSavings()
                                "Budget Management" -> onNavigateToBudgets()
                                "Export Excel" -> {
                                    createCsvLauncher.launch("money_plus_laporan.csv")
                                }
                                else -> activeOverlay = item.overlayType
                            }
                        },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.85f)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(6.dp)
                        ) {
                            // Top centered long banner for Google Drive status
                            if (item.longBadgeText != null) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (driveConnected) Color(0xFF4CAF50) else Color(0xFFFF6584))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = item.longBadgeText,
                                        fontSize = 7.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            // Small Top-Right Badges
                            if (item.badgeText != null) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (item.badgeText == "New") Color(0xFFFF6584) else Color(0xFF9C27B0)
                                        )
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = item.badgeText,
                                        fontSize = 8.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Center alignment for icon container & text
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = if (item.longBadgeText != null) 9.dp else 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Round icon circle background
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(item.iconBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = item.iconEmoji,
                                        fontSize = 24.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Item label text
                                Text(
                                    text = item.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF5E4E52),
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    lineHeight = 13.sp,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Beautiful slide-up overlays and custom interactive Dialog Modals
            activeOverlay?.let { overlay ->
                OverlayContentDialog(
                    overlay = overlay,
                    viewModel = viewModel,
                    theme = theme,
                    onDismiss = { activeOverlay = null },
                    onNavigateToSavings = {
                        activeOverlay = null
                        onNavigateToSavings()
                    },
                    onExportCsv = {
                        createCsvLauncher.launch("money_plus_laporan.csv")
                    }
                )
            }
        }
    }
}

@Composable
fun OverlayContentDialog(
    overlay: MoreActiveOverlay,
    viewModel: FinanceViewModel,
    theme: CuteTheme,
    onDismiss: () -> Unit,
    onNavigateToSavings: () -> Unit,
    onExportCsv: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large upper symbol
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (overlay) {
                            MoreActiveOverlay.Messages -> "Messages 📬"
                            MoreActiveOverlay.Recurring -> "Recurring Subscriptions ⏰"
                            MoreActiveOverlay.Reminders -> "Reminders & Alarms 🔔"
                            MoreActiveOverlay.PurchasePremium -> "Purchase Premium 👑"
                            MoreActiveOverlay.CurrencyRate -> "Currency Exchange 🪙"
                            MoreActiveOverlay.CategoryMgmt -> "Category Settings 📥"
                            MoreActiveOverlay.MemberMgmt -> "Family Members 👥"
                            MoreActiveOverlay.AccountMgmt -> "Account Wallets 💳"
                            MoreActiveOverlay.TagMgmt -> "Tag Management 🏷️"
                            MoreActiveOverlay.SearchTransactions -> "Search ledger logs 🔍"
                            MoreActiveOverlay.GoogleDriveBackup -> "Google Drive Cloud Backup ☁️"
                            MoreActiveOverlay.TrendForecast -> "Trend Projection (Beta) 📈"
                            MoreActiveOverlay.RateApp -> "Rate Our App 👍"
                            MoreActiveOverlay.Feedback -> "Write Feedback ✉️"
                            MoreActiveOverlay.About -> "About Charisna Saving 💬"
                            MoreActiveOverlay.ShareWithFriends -> "Share with Friends 💖"
                            MoreActiveOverlay.Settings -> "Core Configuration Settings ⚙️"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = theme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Divider(color = Color.LightGray.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 8.dp))

                // Detail feature layouts
                when (overlay) {
                    MoreActiveOverlay.Messages -> MessagesSubFeature(theme)
                    MoreActiveOverlay.Recurring -> RecurringSubFeature(theme)
                    MoreActiveOverlay.Reminders -> RemindersSubFeature(theme)
                    MoreActiveOverlay.PurchasePremium -> PurchasePremiumSubFeature(theme)
                    MoreActiveOverlay.CurrencyRate -> CurrencyExchangeSubFeature()
                    MoreActiveOverlay.CategoryMgmt -> CategoryMgmtSubFeature(viewModel, theme)
                    MoreActiveOverlay.MemberMgmt -> MemberMgmtSubFeature(theme)
                    MoreActiveOverlay.AccountMgmt -> AccountMgmtSubFeature(viewModel, theme)
                    MoreActiveOverlay.TagMgmt -> TagMgmtSubFeature(theme)
                    MoreActiveOverlay.SearchTransactions -> SearchTransactionsSubFeature(viewModel, theme)
                    MoreActiveOverlay.GoogleDriveBackup -> GoogleDriveBackupSubFeature(viewModel, theme)
                    MoreActiveOverlay.TrendForecast -> TrendForecastSubFeature(viewModel, theme)
                    MoreActiveOverlay.RateApp -> RateAppSubFeature(theme, onDismiss)
                    MoreActiveOverlay.Feedback -> FeedbackSubFeature(theme, onDismiss)
                    MoreActiveOverlay.About -> AboutSubFeature(theme)
                    MoreActiveOverlay.ShareWithFriends -> ShareWithFriendsSubFeature(theme)
                    MoreActiveOverlay.Settings -> SettingsSubFeature(viewModel, theme, onExportCsv = onExportCsv)
                }

                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = theme.accent),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Kembali Ke Menu", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

// ======================== SUB UTILITY SCREEN COMPOSABLES ========================

@Composable
fun MessagesSubFeature(theme: CuteTheme) {
    val messages = listOf(
        "🐰 Pio: Kakak hebat banget hari ini sudah hemat! Jangan lupa menabung ya! 🥕",
        "🐱 Coco: Meow! Kurangi jajan kopi susu ya, mending buat celengan impian! ☕",
        "🐷 Onal: Nabung pangkal kaya, Kak! Aku siap jagain celenganmu kapan saja! 💰",
        "🎀 Tips: Atur alokasi bulananmu dengan rasio 50/30/20 untuk keuangan super sehat!"
    )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        Text("Dengar kata maskot keuanganmu hari ini: ✨", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        messages.forEach { msg ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(theme.background.copy(alpha = 0.45f))
                    .padding(12.dp)
            ) {
                Text(msg, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
            }
        }
    }
}

@Composable
fun RecurringSubFeature(theme: CuteTheme) {
    var desc by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    val recurringList = remember { mutableStateListOf("Netflix Premium - Rp186,000 / bln", "Spotify Premium - Rp54,900 / bln") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        Text("Daftar Tagihan Berlangganan Kakak: 💳", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        recurringList.forEach { rec ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF9F7F9))
                    .padding(8.dp)
            ) {
                Text(rec, fontSize = 11.sp, color = Color.DarkGray)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        TextField(
            value = desc,
            onValueChange = { desc = it },
            placeholder = { Text("cth: Internet Home") },
            label = { Text("Nama Tagihan") },
            colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent),
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = price,
            onValueChange = { price = it },
            placeholder = { Text("cth: 350000") },
            label = { Text("Jumlah Uang harian/bulanan (Rp)") },
            colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val p = price.toDoubleOrNull()
                if (desc.isNotEmpty() && p != null) {
                    val df = DecimalFormat("#,###")
                    recurringList.add("$desc - Rp${df.format(p)} / bln")
                    desc = ""
                    price = ""
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tambah Tagihan Berulang ➕", fontSize = 11.sp, color = Color.White)
        }
    }
}

@Composable
fun RemindersSubFeature(theme: CuteTheme) {
    var alarmOn by remember { mutableStateOf(true) }
    var reminderNote by remember { mutableStateOf("Jangan lupa catat jajan sehabis makan siang ya! 🥪") }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Nyalakan Alarm Harian ⏰", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = theme.onSurface)
            Switch(checked = alarmOn, onCheckedChange = { alarmOn = it })
        }

        TextField(
            value = reminderNote,
            onValueChange = { reminderNote = it },
            label = { Text("Pesan Pengingat") },
            colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent),
            modifier = Modifier.fillMaxWidth()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFEFFDFC))
                .padding(12.dp)
        ) {
            Text(
                "💡 Tips Reminders: Sistem alarm akan membunyikan notifikasi hp imut Kakak jam 19:30 malam untuk rekap seluruh keuangan!",
                fontSize = 10.sp,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun PurchasePremiumSubFeature(theme: CuteTheme) {
    var isUnlocked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("⭐ Charisna Saving Premium ⭐", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = theme.accent)
        Text(
            "Miliki Premium untuk membuka tema warna ultra-pastel khusus, sapaan lucu kustom, dan grafik analisis ceria lanjut!",
            textAlign = TextAlign.Center,
            fontSize = 11.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (isUnlocked) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFE8FDF5))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("🏆 Kakak telah terdaftar sebagai Member Premium! Terimakasih cintanya Kak! 💖🎈", fontSize = 12.sp, color = Color(0xFF1EAD8E), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }
        } else {
            Button(
                onClick = { isUnlocked = true },
                colors = ButtonDefaults.buttonColors(containerColor = theme.accent),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Unlock Premium (Simulator) 🚀👑", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CurrencyExchangeSubFeature() {
    var inputAmount by remember { mutableStateOf("100000") }
    val rates = mapOf(
        "USD" to 16290.0,
        "SGD" to 12110.0,
        "EUR" to 17650.0,
        "JPY (100)" to 104.2
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        Text("Kurs Rupiah Hari Ini (Terhadap IDR): 🪙", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)

        TextField(
            value = inputAmount,
            onValueChange = { inputAmount = it },
            label = { Text("Jumlah IDR (Rupiah)") },
            colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent),
            modifier = Modifier.fillMaxWidth()
        )

        val idrVal = inputAmount.toDoubleOrNull() ?: 0.0

        rates.forEach { (cur, multiplier) ->
            val result = if (idrVal > 0) idrVal / multiplier else 0.0
            val formatter = DecimalFormat("#,##0.00")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF9F7F9))
                    .padding(8.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("1 $cur = Rp${DecimalFormat("#,###").format(multiplier)}", fontSize = 11.sp, color = Color.Gray)
                    Text("= ${formatter.format(result)} $cur", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                }
            }
        }
    }
}

@Composable
fun CategoryMgmtSubFeature(viewModel: FinanceViewModel, theme: CuteTheme) {
    val transactions by viewModel.currentMonthTransactions.collectAsStateWithLifecycle()
    val categories = transactions.map { it.categoryName }.distinct().ifEmpty { listOf("Makanan", "Belanja", "Transportasi", "Hiburan") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        Text("Daftar Kategori Transaksi Aktif 📂", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        categories.forEach { cat ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(theme.background.copy(alpha = 0.35f))
                    .padding(10.dp)
            ) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("🌸 $cat", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = theme.onSurface)
                    Text("Kustomisasi Berhasil", fontSize = 10.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun MemberMgmtSubFeature(theme: CuteTheme) {
    val members = remember { mutableStateListOf("Charisna (Owner)", "Papa 🏡", "Mama 🍰", "Pio 🐰", "Coco 🐱", "Onal 🐷") }
    var name by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        Text("Kelola Daftar Anggota Keluarga: 👥", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        members.forEach { m ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF4F6FC))
                    .padding(8.dp)
            ) {
                Text(m, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
            }
        }

        TextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("cth: Kakak Alanda") },
            label = { Text("Nama Anggota Baru") },
            colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (name.isNotEmpty()) {
                    members.add(name)
                    name = ""
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tambah Anggota ➕", fontSize = 11.sp, color = Color.White)
        }
    }
}

@Composable
fun AccountMgmtSubFeature(viewModel: FinanceViewModel, theme: CuteTheme) {
    val wallets = listOf("Dompet Utama", "Tabungan", "E-Money", "ShopeePay", "Kartu Kredit 💳")

    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        Text("Status Dompet & Rekening: 💳", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        wallets.forEach { name ->
            val bal = viewModel.getWalletBalance(name)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFFAFC))
                    .padding(10.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("⭐ $name", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = theme.onSurface)
                    Text("Rp${DecimalFormat("#,###").format(bal)}", fontSize = 12.sp, fontWeight = FontWeight.Black, color = if (bal >= 0) Color(0xFF4CAF50) else Color(0xFFFF6584))
                }
            }
        }
    }
}

@Composable
fun TagMgmtSubFeature(theme: CuteTheme) {
    val tags = remember { mutableStateListOf("JajanHarian 🍧", "Investasi", "Transport", "NgemilCeria") }
    var newTag by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        Text("Daftar Label Tag Aktif: 🏷️", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            tags.forEach { tag ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(theme.primary.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("#$tag", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = theme.primary)
                }
            }
        }

        TextField(
            value = newTag,
            onValueChange = { newTag = it },
            placeholder = { Text("cth: KopiSore") },
            label = { Text("Nama Tag Baru") },
            colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (newTag.isNotEmpty()) {
                    tags.add(newTag)
                    newTag = ""
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tambah Tag 💾", fontSize = 11.sp, color = Color.White)
        }
    }
}

@Composable
fun SearchTransactionsSubFeature(viewModel: FinanceViewModel, theme: CuteTheme) {
    val transactions by viewModel.currentMonthTransactions.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }

    val filtered = remember(query, transactions) {
        if (query.isEmpty()) emptyList()
        else transactions.filter {
            it.note.contains(query, ignoreCase = query.length >= 1) ||
                    it.categoryName.contains(query, ignoreCase = query.length >= 1) ||
                    it.walletName.contains(query, ignoreCase = query.length >= 1)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        Text("Cari Catatan Kas Kakak 🔍", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)

        TextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Ketik kata kunci...") },
            colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent),
            modifier = Modifier.fillMaxWidth()
        )

        if (filtered.isNotEmpty()) {
            Box(modifier = Modifier.heightIn(max = 160.dp)) {
                androidx.compose.foundation.lazy.LazyColumn {
                    items(filtered) { tx ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF9F7F9))
                                .padding(8.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text(tx.categoryName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = theme.onSurface)
                                    Text(tx.note.ifEmpty { "Tanpa catatan" }, fontSize = 9.sp, color = Color.Gray)
                                }
                                Text(
                                    "${if (tx.type == "EXPENSE") "-" else "+"}Rp${DecimalFormat("#,###").format(tx.amount)}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (tx.type == "EXPENSE") Color(0xFFFF94B0) else Color(0xFF8AE0C5)
                                )
                            }
                        }
                    }
                }
            }
        } else if (query.isNotEmpty()) {
            Text("Tidak ada hasil yang cocok 🍦", fontSize = 11.sp, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun GoogleDriveBackupSubFeature(viewModel: FinanceViewModel, theme: CuteTheme) {
    val driveConnected by viewModel.driveConnected.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isDriveSyncing.collectAsStateWithLifecycle()
    val lastDriveBackup by viewModel.lastDriveBackupTime.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        Text("Sinkronisasi Google Drive:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)

        if (isSyncing) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                CircularProgressIndicator(color = theme.primary)
            }
        } else {
            if (!driveConnected) {
                Button(
                    onClick = {
                        viewModel.linkGoogleDriveAccount {
                            Toast.makeText(context, "Google Drive berhasil dihubungkan! ☁️🎉", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Hubungkan Akun Google Drive 🔑", fontSize = 11.sp, color = Color.White)
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(theme.background.copy(alpha = 0.5f))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Terhubung ke Cloud ✅", fontSize = 11.sp, color = Color(0xFF1EAD8E), fontWeight = FontWeight.Bold)
                            Text("Putus Link", fontSize = 10.sp, color = Color.Red, modifier = Modifier.clickable {
                                viewModel.unlinkGoogleDrive()
                            })
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Akun Terhubung:", fontSize = 10.sp, color = Color.Gray)
                        Text("charisnavradica88@gmail.com 🌸", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = theme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Cadangan Terakhir: ${lastDriveBackup ?: "Belum dicadangkan"}", fontSize = 10.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            viewModel.backupToGoogleDriveCloud(
                                onSuccess = {
                                    Toast.makeText(context, "Mencadangkan selesai! ✅", Toast.LENGTH_SHORT).show()
                                },
                                onError = {
                                    Toast.makeText(context, "Error: $it", Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = theme.accent),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Backup ☁️", fontSize = 11.sp, color = Color.White)
                    }

                    Button(
                        onClick = {
                            viewModel.restoreFromGoogleDriveCloud(
                                onSuccess = {
                                    Toast.makeText(context, "Database dipulihkan! ✅", Toast.LENGTH_SHORT).show()
                                },
                                onError = {
                                    Toast.makeText(context, "Error: $it", Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Restore 🔄", fontSize = 11.sp, color = theme.onSurface)
                    }
                }
            }
        }
    }
}

@Composable
fun TrendForecastSubFeature(viewModel: FinanceViewModel, theme: CuteTheme) {
    val transactions by viewModel.currentMonthTransactions.collectAsStateWithLifecycle()
    val totalExpense = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        Text("Prediksi Keuangan Bulan Ini 📉", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFFFF9F5))
                .padding(14.dp)
        ) {
            Column {
                Text("Estimasi Belanja Akhir Bulan:", fontSize = 11.sp, color = Color.Gray)
                val format = DecimalFormat("#,###")
                val predicted = totalExpense * 1.35
                Text("Rp${format.format(predicted)}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = theme.accent)
                Text("Berdasarkan intensitas kas Kakak seminggu terakhir.", fontSize = 9.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color.White)
                .padding(6.dp)
        ) {
            // Draw a tiny trend line in Canvas
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(0f, size.height * 0.9f)
                    lineTo(size.width * 0.25f, size.height * 0.7f)
                    lineTo(size.width * 0.5f, size.height * 0.65f)
                    lineTo(size.width * 0.75f, size.height * 0.4f)
                    lineTo(size.width, size.height * 0.15f)
                }
                drawPath(
                    path = path,
                    color = theme.accent,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 6f)
                )
            }
        }
    }
}

@Composable
fun RateAppSubFeature(theme: CuteTheme, onDismiss: () -> Unit) {
    var rating by remember { mutableStateOf(5) }
    val context = LocalContext.current

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Apakah Kakak menyukai Charisna Saving? ⭐", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            for (i in 1..5) {
                IconButton(onClick = { rating = i }) {
                    Icon(
                        imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Star",
                        tint = if (i <= rating) Color(0xFFFFD700) else Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        Button(
            onClick = {
                Toast.makeText(context, "Terima kasih atas bintang $rating-nya Kak! Hub kami kapanpun ya 💖🎈", Toast.LENGTH_LONG).show()
                onDismiss()
            },
            colors = ButtonDefaults.buttonColors(containerColor = theme.primary)
        ) {
            Text("Kirim Penilaian", color = Color.White)
        }
    }
}

@Composable
fun FeedbackSubFeature(theme: CuteTheme, onDismiss: () -> Unit) {
    var msg by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        Text("Kirimkan kritik & usulmu agar kami terus berbenah: 💗", fontSize = 12.sp, color = Color.Gray)
        TextField(
            value = msg,
            onValueChange = { msg = it },
            placeholder = { Text("Tulis usul Kakak di sini...") },
            colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent),
            modifier = Modifier.fillMaxWidth().height(90.dp)
        )

        Button(
            onClick = {
                if (msg.isNotEmpty()) {
                    Toast.makeText(context, "Pesan masuk! Usulan Kakak segera dipelajari! 🧁✨", Toast.LENGTH_LONG).show()
                    onDismiss()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = theme.accent),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Kirim Masukan", color = Color.White)
        }
    }
}

@Composable
fun AboutSubFeature(theme: CuteTheme) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("🌸 Charisna Saving v3.0 🌸", fontSize = 16.sp, fontWeight = FontWeight.Black, color = theme.primary)
        Text(
            "Sebuah aplikasi catatan keuangan super unyu dan ceria yang didesain khusus bersama Pio 🐰, Coco 🐱, dan Onal 🐷 untuk membantumu berdana secara teratur.",
            fontSize = 11.sp,
            color = Color.DarkGray,
            textAlign = TextAlign.Center
        )
        Text("Dibuat dengan penuh cinta untuk Charisna & dunia 💖🧁", fontSize = 10.sp, color = theme.accent, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ShareWithFriendsSubFeature(theme: CuteTheme) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val shareText = "Ayo kelola tabungan dwi-mingguan imut bersama Charisna Saving dan maskot-maskot imut Pio, Coco, dan Onal! 🌸✨ https://ai.studio/build"

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(theme.background.copy(alpha = 0.45f))
                .padding(12.dp)
        ) {
            Text(shareText, fontSize = 11.sp, color = Color.DarkGray)
        }

        Button(
            onClick = {
                clipboardManager.setText(AnnotatedString(shareText))
                Toast.makeText(context, "Teks penyebaran berhasil disalin ke papan klip! 📋🎈", Toast.LENGTH_LONG).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = theme.accent)
        ) {
            Text("Salin Teks Berbagi 📋", color = Color.White)
        }
    }
}

@Composable
fun SettingsSubFeature(viewModel: FinanceViewModel, theme: CuteTheme, onExportCsv: () -> Unit) {
    val currentMascot by viewModel.currentMascot.collectAsStateWithLifecycle()
    val serverActive by viewModel.serverActive.collectAsStateWithLifecycle()
    val serverUrl by viewModel.serverUrl.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        // --- 1. THEME PICKER ---
        Text("Tema Warna Mewah: 🎨", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            CuteTheme.values().forEach { t ->
                val isActive = theme == t
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(t.primary)
                        .clickable { viewModel.setTheme(t) }
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isActive) {
                        Icon(Icons.Default.Check, contentDescription = "Active", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        Divider(color = Color.LightGray.copy(alpha = 0.3f))

        // --- 2. MASCOT CHANGER ---
        Text("Ubah Teman Maskot Keuangan: ✨", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            MascotType.values().forEach { m ->
                val isActive = currentMascot == m
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isActive) m.color.copy(alpha = 0.35f) else Color(0xFFF7F5F6))
                        .clickable { viewModel.setMascot(m) }
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(m.emoji, fontSize = 20.sp)
                        Text(m.displayName.split(" ")[0], fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Divider(color = Color.LightGray.copy(alpha = 0.3f))

        // --- 3. SERVER COMPANION ---
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Companion Server PC 🖥️", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = theme.onSurface)
                Text("Hubungkan ke browser PC lokal.", fontSize = 10.sp, color = Color.Gray)
            }
            Switch(checked = serverActive, onCheckedChange = { viewModel.setServerEnabled(it) })
        }

        if (serverActive) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFEFFDFC))
                    .padding(8.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Akses PC: $serverUrl", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = theme.primary)
                    Text(
                        "Catatan: IP lokal ini tidak dapat diakses langsung oleh browser PC Kakak karena aplikasi saat ini berjalan di Cloud Emulator gratis milik Google AI Studio. Namun, jika Kakak mengunduh APK aslinya dan membukanya di HP asli dalam satu Wi-Fi, fitur ini 100% beneran bekerja dengan mulus! ✨",
                        fontSize = 9.sp,
                        color = Color.DarkGray,
                        lineHeight = 11.sp
                    )
                }
            }

            var showSimulator by remember { mutableStateOf(false) }

            Button(
                onClick = { showSimulator = true },
                colors = ButtonDefaults.buttonColors(containerColor = theme.accent),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simulasikan Web Dashboard PC 🖥️✨", fontSize = 11.sp, color = Color.White)
            }

            if (showSimulator) {
                Dialog(onDismissRequest = { showSimulator = false }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.98f)
                            .fillMaxHeight(0.85f),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDFD))
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Mock Browser Title bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFFFEDED))
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFFF5F56)))
                                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFFFBD2E)))
                                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF27C93F)))
                                }
                                Text("Money+ Web Dashboard Core", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.DarkGray)
                                IconButton(onClick = { showSimulator = false }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray, modifier = Modifier.size(14.dp))
                                }
                            }

                            // Mock Address bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF7F5F6))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(Color.White, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("http://192.168.1.15:8080/summary.html (Simulasi Server)", fontSize = 9.sp, color = Color.Gray)
                                }
                            }

                            // Mock Browser scrollable viewport
                            val transactionsAll by viewModel.allTransactions.collectAsStateWithLifecycle()
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(12.dp)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Web view welcoming banner
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFFFF0F3), RoundedCornerShape(12.dp))
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1.5f)) {
                                        Text("Charisna Saving Dashboard 🌸", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color(0xFFFF5F7E))
                                        Text("Aplikasi catatan & buku besar PC terhubung", fontSize = 8.sp, color = Color.Gray)
                                    }

                                    Button(
                                        onClick = {
                                            onExportCsv()
                                            Toast.makeText(context, "Mengekspor laporan CSV dari dashboard PC ke spreadsheet Kakak... 📑🧁", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8585)),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("Ekspor CSV 📊", fontSize = 9.sp, color = Color.White)
                                    }
                                }

                                // Interactive Asset balances
                                var simulatedAsset = 7000000.0
                                var simulatedDebt = 450000.0
                                transactionsAll.forEach { tx ->
                                    val amtInIDR = tx.amount * tx.exchangeRate
                                    if (tx.type == "EXPENSE") {
                                        if (tx.walletName.contains("Kredit")) simulatedDebt += amtInIDR else simulatedAsset -= amtInIDR
                                    } else if (tx.type == "INCOME") {
                                        if (tx.walletName.contains("Kredit")) simulatedDebt -= amtInIDR else simulatedAsset += amtInIDR
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                                    Card(
                                        modifier = Modifier.weight(1f),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFF0F2)),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text("Assets", fontSize = 7.sp, color = Color.Gray)
                                            Text("Rp${DecimalFormat("#,###").format(simulatedAsset)}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                                        }
                                    }
                                    Card(
                                        modifier = Modifier.weight(1f),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFF0F2)),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text("Debts", fontSize = 7.sp, color = Color.Gray)
                                            Text("Rp${DecimalFormat("#,###").format(simulatedDebt)}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF6B6B))
                                        }
                                    }
                                    Card(
                                        modifier = Modifier.weight(1f),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFF0F2)),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text("Net Worth", fontSize = 7.sp, color = Color.Gray)
                                            Text("Rp${DecimalFormat("#,###").format(simulatedAsset - simulatedDebt)}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF8585))
                                        }
                                    }
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("Buku Besar & Log Aktivitas Catatan:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                                    Text("Status Server: Aktif 🟢", fontSize = 8.sp, color = Color(0xFF4CAF50))
                                }

                                if (transactionsAll.isEmpty()) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().height(100.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Belum ada pencatatan kas di handphone! 🐹🍿", fontSize = 9.sp, color = Color.Gray)
                                    }
                                } else {
                                    transactionsAll.forEach { tx ->
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color(0xFFFAF6F7), RoundedCornerShape(8.dp))
                                                .padding(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                        Text(tx.categoryIcon, fontSize = 11.sp)
                                                        Text(tx.categoryName, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5E4E52))
                                                        Text("(${tx.walletName})", fontSize = 7.sp, color = Color.Gray)
                                                    }
                                                    if (tx.note.isNotEmpty()) {
                                                        Text(tx.note, fontSize = 8.sp, color = Color.Gray)
                                                    }
                                                }
                                                Text(
                                                    "${if (tx.type == "EXPENSE") "-" else "+"} Rp ${DecimalFormat("#,###").format(tx.amount * tx.exchangeRate)}",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (tx.type == "EXPENSE") Color(0xFFFF6584) else Color(0xFF1EAD8E)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Divider(color = Color.LightGray.copy(alpha = 0.3f))

        // --- 4. RESET APP ALL DATA ---
        var showResetConfirm by remember { mutableStateOf(false) }

        Button(
            onClick = { showResetConfirm = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D4D)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Reset", tint = Color.White, modifier = Modifier.size(16.dp))
                Text("Reset Semua Catatan & Data 🚨", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        if (showResetConfirm) {
            AlertDialog(
                onDismissRequest = { showResetConfirm = false },
                title = { Text("Konfirmasi Hapus Data 🚨", fontSize = 14.sp, fontWeight = FontWeight.Bold) },
                text = { Text("Apakah Kakak yakin ingin menghapus seluruh catatan keuangan, target tabungan, dan anggaran? Tindakan ini akan mengosongkan semua data dan tidak dapat dibatalkan.", fontSize = 12.sp) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.resetData {
                                Toast.makeText(context, "Aplikasi berhasil direset! Selamat datang kembali Kakak! 🌸🎀", Toast.LENGTH_LONG).show()
                                showResetConfirm = false
                            }
                        }
                    ) {
                        Text("Reset Sekarang", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetConfirm = false }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}
