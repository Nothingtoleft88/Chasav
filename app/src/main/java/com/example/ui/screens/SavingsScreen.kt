package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.SavingGoalEntity
import com.example.ui.viewmodel.FinanceViewModel
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val theme by viewModel.currentTheme.collectAsStateWithLifecycle()
    val savingGoals by viewModel.savingGoals.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showCreateGoalDialog by remember { mutableStateOf(false) }

    // Dialog inputs
    var goalTitle by remember { mutableStateOf("") }
    var goalTarget by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("celebration") }
    var selectedColorHex by remember { mutableStateOf("#FFB7B2") }

    val currencyFormatter = DecimalFormat("#,###")

    val iconsList = listOf(
        "celebration" to Icons.Default.Celebration,
        "flight" to Icons.Default.Flight,
        "gamepad" to Icons.Default.Gamepad,
        "laptop" to Icons.Default.Laptop,
        "favorite" to Icons.Default.Favorite,
        "directions_car" to Icons.Default.DirectionsCar
    )

    val colorsList = listOf("#FFB7B2", "#FFDAC1", "#BFFCC6", "#A0C4FF", "#D5AAFF", "#FFC6FF")

    Scaffold(
        containerColor = theme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateGoalDialog = true },
                containerColor = theme.accent,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 16.dp, end = 8.dp)
                    .testTag("add_savings_goal_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Buat Target Baru")
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Text(
                    "Celengan Impian 🔮",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = theme.onSurface
                )
                Text(
                    "Tabung uang jajarmu untuk meraih barang impianmu satu per satu!",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top overview status card
                item {
                    val totalTarget = savingGoals.sumOf { it.targetAmount }
                    val totalSaved = savingGoals.sumOf { it.currentAmount }
                    Card(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, top = 12.dp)
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .background(theme.getCardBrush())
                                .padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🐷", fontSize = 48.sp)
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    "Total Tabungan Terkumpul",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    "Rp${currencyFormatter.format(totalSaved)}",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = theme.onSurface
                                )
                                Text(
                                    "Dari target total Rp${currencyFormatter.format(totalTarget)}",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        "Daftar Impian Kakak 🌟",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.onSurface,
                        modifier = Modifier.padding(horizontal = 18.dp)
                    )
                }

                if (savingGoals.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("☁️", fontSize = 42.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Belum ada target tabungan.\nYuk buat mimpi pertamamu!",
                                    fontSize = 13.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(savingGoals, key = { it.id }) { goal ->
                        SavingGoalCardItem(
                            goal = goal,
                            themeColor = theme.primary,
                            onContribute = { amount ->
                                viewModel.addSavingContribution(goal, amount)
                                Toast.makeText(context, "Hore! Berhasil menabung Rp${currencyFormatter.format(amount)}! 🎉", Toast.LENGTH_SHORT).show()
                            },
                            onWithdraw = { amount ->
                                if (amount <= goal.currentAmount) {
                                    viewModel.withdrawSavingContribution(goal, amount)
                                    Toast.makeText(context, "Berhasil mencairkan dana impian Rp${currencyFormatter.format(amount)}!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Uang tabunganmu kurang!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onDelete = { viewModel.deleteSavingGoal(goal) }
                        )
                    }
                }
            }
        }

        // Add Target Dialog Modal
        if (showCreateGoalDialog) {
            AlertDialog(
                onDismissRequest = { showCreateGoalDialog = false },
                title = { Text("Buat Target Impian Baru ✨", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            value = goalTitle,
                            onValueChange = { goalTitle = it },
                            placeholder = { Text("cth: Beli Album Kpop 🌸") },
                            label = { Text("Nama Barang/Impian") },
                            colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent),
                            modifier = Modifier.fillMaxWidth()
                        )

                        TextField(
                            value = goalTarget,
                            onValueChange = { goalTarget = it },
                            placeholder = { Text("cth: 300000") },
                            label = { Text("Target Jumlah Uang (Rp)") },
                            colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Icon Selector
                        Text("Pilih Ikon Lucu:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            iconsList.forEach { (name, vector) ->
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(if (selectedIcon == name) theme.primary else Color.LightGray.copy(alpha = 0.2f))
                                        .clickable { selectedIcon = name }
                                        .padding(6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(vector, contentDescription = name, tint = if (selectedIcon == name) Color.White else Color.Gray)
                                }
                            }
                        }

                        // Color Selector
                        Text("Pilih Warna Tema:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            colorsList.forEach { hex ->
                                val color = Color(android.graphics.Color.parseColor(hex))
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .clickable { selectedColorHex = hex }
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (selectedColorHex == hex) {
                                        Icon(Icons.Default.Check, contentDescription = "Active", tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val targetAmt = goalTarget.toDoubleOrNull() ?: 0.0
                            if (goalTitle.isNotEmpty() && targetAmt > 0) {
                                viewModel.addSavingGoal(goalTitle, targetAmt, selectedIcon, selectedColorHex)
                                showCreateGoalDialog = false
                                goalTitle = ""
                                goalTarget = ""
                                Toast.makeText(context, "Mimpi baru dicatat! Semangat menabung ya Kak! 💪✨", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Silakan lengkapi data dengan benar!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = theme.accent)
                    ) {
                        Text("Buat Cerita", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateGoalDialog = false }) {
                        Text("Batal")
                    }
                },
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}

@Composable
fun SavingGoalCardItem(
    goal: SavingGoalEntity,
    themeColor: Color,
    onContribute: (Double) -> Unit,
    onWithdraw: (Double) -> Unit,
    onDelete: () -> Unit
) {
    var showActionRow by remember { mutableStateOf(false) }
    var isNabungMode by remember { mutableStateOf(true) } // true: nabung, false: cairkan/ambil
    var amountText by remember { mutableStateOf("") }

    val currencyFormatter = DecimalFormat("#,###")

    val progress = (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f)

    val goalThemeColor = try {
        Color(android.graphics.Color.parseColor(goal.colorHex))
    } catch (e: Exception) {
        themeColor
    }

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .fillMaxWidth()
            .clickable { showActionRow = !showActionRow },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Round cute goal icon
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(goalThemeColor.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = mapSavingGoalIcon(goal.iconName),
                        contentDescription = goal.title,
                        tint = goalThemeColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = goal.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5E4E52)
                    )
                    Text(
                        text = "Terisi: Rp${currencyFormatter.format(goal.currentAmount)} / Rp${currencyFormatter.format(goal.targetAmount)}",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Delete option
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus Celengan", tint = Color.LightGray, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = goalThemeColor,
                trackColor = goalThemeColor.copy(alpha = 0.15f)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${(progress * 100).toInt()}% Tercapai",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = goalThemeColor
                )

                if (progress >= 1f) {
                    Text(
                        text = "Selamat! Celengan Penuh! 🎉",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1EAD8E)
                    )
                } else {
                    Text(
                        text = "Kurang Rp${currencyFormatter.format(goal.targetAmount - goal.currentAmount)} lagi!",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Expanded saving interactions
            AnimatedVisibility(visible = showActionRow) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Divider(color = Color.LightGray.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Segmented button to choose Nabung vs Cairkan
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF1EFF1))
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isNabungMode) goalThemeColor else Color.Transparent)
                                .clickable { isNabungMode = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("💖 Taruh Uang", fontSize = 11.sp, color = if (isNabungMode) Color.White else Color.Gray, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (!isNabungMode) goalThemeColor else Color.Transparent)
                                .clickable { isNabungMode = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("💸 Ambil Uang", fontSize = 11.sp, color = if (!isNabungMode) Color.White else Color.Gray, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextField(
                            value = amountText,
                            onValueChange = { amountText = it },
                            placeholder = { Text(if (isNabungMode) "cth: 50000" else "Ambil celengan...", fontSize = 12.sp) },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF7F5F6),
                                unfocusedContainerColor = Color(0xFFF7F5F6),
                                focusedIndicatorColor = goalThemeColor
                            ),
                            textStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = {
                                val amt = amountText.toDoubleOrNull() ?: 0.0
                                if (amt > 0) {
                                    if (isNabungMode) {
                                        onContribute(amt)
                                    } else {
                                        onWithdraw(amt)
                                    }
                                    amountText = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = goalThemeColor),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isNabungMode) "Nabung" else "Ambil",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

fun mapSavingGoalIcon(icon: String): ImageVector {
    return when (icon) {
        "celebration" -> Icons.Default.Celebration
        "flight" -> Icons.Default.Flight
        "gamepad" -> Icons.Default.Gamepad
        "laptop" -> Icons.Default.Laptop
        "favorite" -> Icons.Default.Favorite
        "directions_car" -> Icons.Default.DirectionsCar
        else -> Icons.Default.Savings
    }
}
