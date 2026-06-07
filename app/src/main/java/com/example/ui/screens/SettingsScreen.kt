package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.ui.companion.MascotType
import com.example.ui.theme.CuteTheme
import com.example.ui.viewmodel.FinanceViewModel

data class FinanceBadge(
    val title: String,
    val description: String,
    val iconEmoji: String,
    val isUnlocked: Boolean
)

@Composable
fun SettingsScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val theme by viewModel.currentTheme.collectAsStateWithLifecycle()
    val currentMascot by viewModel.currentMascot.collectAsStateWithLifecycle()
    val transactions by viewModel.currentMonthTransactions.collectAsStateWithLifecycle()
    val budgets by viewModel.currentMonthBudgets.collectAsStateWithLifecycle()
    val savingGoals by viewModel.savingGoals.collectAsStateWithLifecycle()
    val serverActive by viewModel.serverActive.collectAsStateWithLifecycle()
    val serverUrl by viewModel.serverUrl.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isDriveSyncing.collectAsStateWithLifecycle()
    val driveConnected by viewModel.driveConnected.collectAsStateWithLifecycle()
    val lastDriveBackup by viewModel.lastDriveBackupTime.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // SAF Launchers for file import/export
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

    val openJsonLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.importBackupJson(
                context = context,
                uri = uri,
                onSuccess = {
                    Toast.makeText(context, "Sukses! Database dipulihkan dari data cadangan 🎉💕", Toast.LENGTH_LONG).show()
                },
                onError = { err ->
                    Toast.makeText(context, "Gagal memulihkan database: ${err.message}", Toast.LENGTH_LONG).show()
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

    // Compute stats for achievement badges
    val totalExpense = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    val globalBudget = budgets.firstOrNull { it.categoryName == "GLOBAL" }?.amount ?: 1500000.0
    val ratio = if (globalBudget > 0) totalExpense / globalBudget else 0.0

    val badges = remember(transactions, savingGoals, ratio) {
        listOf(
            FinanceBadge(
                title = "Hemat Master 🌟",
                description = "Berhasil membelanjakan <50% sisa anggaran bulanan Kakak.",
                iconEmoji = "🥇",
                isUnlocked = transactions.isNotEmpty() && ratio < 0.5
            ),
            FinanceBadge(
                title = "Gemar Menabung 🐷",
                description = "Punya minimal 1 daftar Celengan Impian aktif.",
                iconEmoji = "🏦",
                isUnlocked = savingGoals.isNotEmpty()
            ),
            FinanceBadge(
                title = "Pencatat Disiplin ✍️",
                description = "Mencatat minimal 5 transaksi di bulan berjalan.",
                iconEmoji = "📝",
                isUnlocked = transactions.size >= 5
            ),
            FinanceBadge(
                title = "Tanaman Makmur 🌸",
                description = "Rasio belanja tetap di zona hijau aman (<40%).",
                iconEmoji = "🌸",
                isUnlocked = transactions.isNotEmpty() && ratio < 0.4
            )
        )
    }

    Scaffold(
        containerColor = theme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 60.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Text(
                        "Konfigurasi & Companion ⚙️",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = theme.onSurface
                    )
                    Text(
                        "Ubah tema imut, pilih maskot, dan raih penghargaan menabung!",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Theme customizer card
            item {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Text(
                            "Tema Warna Pastel 🎨",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.onSurface
                        )
                        Text(
                            "Pilih tema warna soft kesukaan Kakak",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            CuteTheme.values().forEach { t ->
                                val isActive = theme == t
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            viewModel.setTheme(t)
                                            Toast.makeText(context, "Tema diganti ke ${t.displayName}! 💕", Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(45.dp)
                                            .clip(CircleShape)
                                            .background(t.primary)
                                            .padding(8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isActive) {
                                            Icon(Icons.Default.Check, contentDescription = "Active", tint = Color.White)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = t.displayName.split(" ")[0],
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isActive) theme.accent else Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Mascot Companion Picker
            item {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Text(
                            "Pilih Teman Maskot Keuangan 🐰",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.onSurface
                        )
                        Text(
                            "Maskot akan setia menyapa dan memberikan tips hemat di beranda Kakak!",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MascotType.values().forEach { m ->
                                val isActive = currentMascot == m
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(if (isActive) m.color.copy(alpha = 0.35f) else Color(0xFFF7F5F6))
                                        .clickable {
                                            viewModel.setMascot(m)
                                            Toast.makeText(context, "Mulai sekarang ${m.displayName.split(" ")[0]} siap menemanimu! ✨", Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(12.dp)
                                        .testTag("mascot_picker_${m.id}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(m.emoji, fontSize = 28.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = m.displayName.split(" ")[0],
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = theme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Companion PC local web server card
            item {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Companion Web Server PC 🖥️",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = theme.onSurface
                                )
                                Text(
                                    "Hubungkan ke PC/Laptop untuk download format berkas web & CSV lokal.",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                )
                            }
                            Switch(
                                checked = serverActive,
                                onCheckedChange = { isChecked ->
                                    viewModel.setServerEnabled(isChecked)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = theme.primary,
                                    uncheckedThumbColor = Color.Gray,
                                    uncheckedTrackColor = Color.LightGray.copy(alpha = 0.4f)
                                )
                            )
                        }

                        if (serverActive) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(theme.background.copy(alpha = 0.5f))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        "Alamat Server PC Kakak: 🔗",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = theme.primary
                                    )
                                    Text(
                                        text = serverUrl,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = theme.onSurface,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        "Cara Akses Cepat:\n" +
                                        "1. Pastikan HP & PC tersambung ke jaringan Wi-Fi yang sama.\n" +
                                        "2. Buka browser internet di PC Kakak (Chrome/Edge/Safari).\n" +
                                        "3. Masukkan alamat URL di atas untuk mengunduh laporan Excel/CSV atau view sirkulasi uang offline! 📊",
                                        fontSize = 10.sp,
                                        color = Color.DarkGray,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Database backup & restore management card (SAF and Google Drive cloud)
            item {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Text(
                            "Ekspor & Impor Data 💾",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.onSurface
                        )
                        Text(
                            "Kelola berkas sirkulasi kas Kakak secara aman baik offline maupun sinkronisasi cloud.",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // --- 1. LOCAL DATA BACKUP (.json) ---
                        Text(
                            "Cadangan Lokal (.json) 📁",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.primary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                        )
                        Text(
                            "Unduh database utuh atau pulihkan kembali lewat berkas backup.",
                            fontSize = 10.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { createJsonLauncher.launch("money_plus_backup.json") },
                                colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Ekspor JSON 📤", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            Button(
                                onClick = { openJsonLauncher.launch(arrayOf("application/json")) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray.copy(alpha = 0.2f)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Impor JSON 📥", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = theme.onSurface)
                            }
                        }

                        Divider(
                            color = Color.LightGray.copy(alpha = 0.3f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        // --- 2. EXCEL COMPATIBLE (.csv) ---
                        Text(
                            "Format Spreadsheet Excel (.csv) 📊",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.primary,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                        Text(
                            "Simpan laporan sirkulasi uang Kakak dalam format tabel Excel untuk dianalisis di komputer.",
                            fontSize = 10.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Button(
                            onClick = { createCsvLauncher.launch("money_plus_laporan.csv") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8FDF5)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Unduh Laporan Excel / CSV 📈🍰",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1EAD8E)
                            )
                        }

                        Divider(
                            color = Color.LightGray.copy(alpha = 0.3f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        // --- 3. GOOGLE DRIVE CLOUD SYNC ---
                        Text(
                            "Google Drive Cloud Backup ☁️",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.primary,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                        Text(
                            "Otomatiskan atau simpan laporannya ke awan menggunakan Akun Google Drive pribadi secara privat.",
                            fontSize = 10.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        if (isSyncing) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(theme.background.copy(alpha = 0.4f))
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    color = theme.primary,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Menghubungi Google Drive... ☁️✨",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = theme.onSurface
                                )
                            }
                        } else {
                            if (!driveConnected) {
                                Button(
                                    onClick = {
                                        viewModel.linkGoogleDriveAccount {
                                            Toast.makeText(context, "Google Drive berhasil dihubungkan! ☁️🎉", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = theme.accent),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Hubungkan Google Drive 🔑",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
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
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "Terhubung dengan Akun Google! ✅",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1EAD8E)
                                            )
                                            Text(
                                                text = "Putus Sambungan 💔",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Red,
                                                modifier = Modifier.clickable {
                                                    viewModel.unlinkGoogleDrive()
                                                    Toast.makeText(context, "Akun Google Drive dilepas.", Toast.LENGTH_SHORT).show()
                                                }
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = if (lastDriveBackup != null) "Cadangan terakhir: $lastDriveBackup 🌸"
                                                   else "Belum ada cadangan awan berjalan.",
                                            fontSize = 10.sp,
                                            color = Color.DarkGray
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Button(
                                                onClick = {
                                                    viewModel.backupToGoogleDriveCloud(
                                                        onSuccess = {
                                                            Toast.makeText(context, "Berhasil mencadangkan data ke Google Drive Cloud! ☁️💝", Toast.LENGTH_LONG).show()
                                                        },
                                                        onError = {
                                                            Toast.makeText(context, "Aduh: $it", Toast.LENGTH_SHORT).show()
                                                        }
                                                    )
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Cadangkan ☁️", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            }

                                            Button(
                                                onClick = {
                                                    viewModel.restoreFromGoogleDriveCloud(
                                                        onSuccess = {
                                                            Toast.makeText(context, "Data berhasil disinkronisasi/pulihkan dari Cloud! 🔄🧸", Toast.LENGTH_LONG).show()
                                                        },
                                                        onError = {
                                                            Toast.makeText(context, "Aduh: $it", Toast.LENGTH_SHORT).show()
                                                        }
                                                    )
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray.copy(alpha = 0.2f)),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Pulihkan 🔄", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = theme.onSurface)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Achievements Badge list
            item {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Text(
                            "Lencana Prestasi Saving 🏆",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.onSurface
                        )
                        Text(
                            "Menabung dengan asyik dan penuhi koleksi lencanamu!",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            badges.forEach { badge ->
                                val toneColor = if (badge.isUnlocked) theme.primary else Color.LightGray.copy(alpha = 0.5f)
                                val textColor = if (badge.isUnlocked) theme.onSurface else Color.LightGray

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (badge.isUnlocked) theme.background.copy(alpha = 0.4f) else Color(0xFFF9F7F9))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(45.dp)
                                            .clip(CircleShape)
                                            .background(toneColor.copy(alpha = 0.25f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(badge.iconEmoji, fontSize = 24.sp)
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            badge.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textColor
                                        )
                                        Text(
                                            badge.description,
                                            fontSize = 11.sp,
                                            color = if (badge.isUnlocked) Color.Gray else Color.LightGray,
                                            lineHeight = 14.sp
                                        )
                                    }

                                    if (badge.isUnlocked) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Color(0xFFE8FDF5))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                "Raih",
                                                color = Color(0xFF1EAD8E),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Color(0xFFF1EFF1))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                "Terkunci",
                                                color = Color.LightGray,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
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
}
