package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.database.AppDatabase
import com.example.data.repository.FinanceRepository
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.FinanceViewModel
import com.example.ui.viewmodel.FinanceViewModelFactory

enum class FinanceTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    BOOKS("Books", Icons.Filled.MenuBook, Icons.Outlined.MenuBook, "tab_books"),
    WALLET("Wallet", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet, "tab_wallet"),
    CHARTS("Charts", Icons.Filled.PieChart, Icons.Outlined.PieChart, "tab_charts"),
    MORE("More", Icons.Filled.MoreHoriz, Icons.Outlined.MoreHoriz, "tab_more")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Core room init
        val database = AppDatabase.getDatabase(this)
        val repository = FinanceRepository(database.financeDao())
        val factory = FinanceViewModelFactory(application, repository)
        val viewModel = ViewModelProvider(this, factory)[FinanceViewModel::class.java]

        setContent {
            val themeState by viewModel.currentTheme.collectAsStateWithLifecycle()
            var showSplash by remember { mutableStateOf(true) }

            // Pass colors manually based on current selected Theme option
            val customColorScheme = lightColorScheme(
                primary = themeState.primary,
                background = themeState.background,
                surface = themeState.surface,
                onPrimary = themeState.onPrimary,
                onSurface = themeState.onSurface,
                tertiary = themeState.accent
            )

            MaterialTheme(
                colorScheme = customColorScheme,
                typography = com.example.ui.theme.Typography
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = themeState.background
                ) {
                    AnimatedContent(
                        targetState = showSplash,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(600)) togetherWith fadeOut(animationSpec = tween(500))
                        },
                        label = "splash_to_main_transition"
                    ) { isSplash ->
                        if (isSplash) {
                            CuteSplashIntroScreen(
                                themeState = themeState,
                                onFinish = { showSplash = false }
                            )
                        } else {
                            MainAppContent(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: FinanceViewModel) {
    var activeTab by remember { mutableStateOf(FinanceTab.BOOKS) }
    var showAddScreen by remember { mutableStateOf(false) }

    val themeState by viewModel.currentTheme.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            if (!showAddScreen) {
                CustomCuteBottomNavigation(
                    activeTab = activeTab,
                    onTabSelected = { activeTab = it },
                    activeColor = themeState.primary,
                    onSurfaceColor = themeState.onSurface,
                    cardBg = themeState.cardBackground
                )
            }
        },
        contentWindowInsets = WindowInsets.navigationBars,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Screen Transitions
            AnimatedContent(
                targetState = activeTab,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "active_screen_transition"
            ) { targetTab ->
                when (targetTab) {
                    FinanceTab.BOOKS -> WalletScreen(
                        viewModel = viewModel,
                        onNavigateToAdd = { showAddScreen = true }
                    )
                    FinanceTab.WALLET -> SavingsScreen(
                        viewModel = viewModel
                    )
                    FinanceTab.CHARTS -> StatsScreen(
                        viewModel = viewModel
                    )
                    FinanceTab.MORE -> MoreScreen(
                        viewModel = viewModel,
                        onNavigateToSavings = { activeTab = FinanceTab.WALLET },
                        onNavigateToBudgets = { activeTab = FinanceTab.MORE }
                    )
                }
            }

            // Slide Up Overlay for Add Transaction Input screen
            AnimatedVisibility(
                visible = showAddScreen,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                AddTransactionScreen(
                    viewModel = viewModel,
                    onNavigateBack = { showAddScreen = false }
                )
            }
        }
    }
}

@Composable
fun CustomCuteBottomNavigation(
    activeTab: FinanceTab,
    onTabSelected: (FinanceTab) -> Unit,
    activeColor: Color,
    onSurfaceColor: Color,
    cardBg: Color
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        shadowElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FinanceTab.values().forEach { tab ->
                val isSelected = activeTab == tab
                val itemColor = if (isSelected) activeColor else Color.Gray

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag(tab.testTag)
                ) {
                    Icon(
                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                        contentDescription = tab.title,
                        tint = itemColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = tab.title,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = itemColor
                    )
                }
            }
        }
    }
}

@Composable
fun CuteSplashIntroScreen(
    themeState: com.example.ui.theme.CuteTheme,
    onFinish: () -> Unit
) {
    var progress by remember { mutableStateOf(0f) }
    var scaleMascots by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        // Soft animated entry scale
        androidx.compose.animation.core.animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = androidx.compose.animation.core.tween(800, easing = androidx.compose.animation.core.FastOutSlowInEasing)
        ) { valValue, _ ->
            scaleMascots = valValue
        }

        // Custom progressive timer tick for 2.0 seconds
        val steps = 40
        for (i in 1..steps) {
            kotlinx.coroutines.delay(50)
            progress = i.toFloat() / steps
        }
        onFinish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(
                    listOf(
                        themeState.background,
                        themeState.primary.copy(alpha = 0.20f),
                        themeState.primary.copy(alpha = 0.40f)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Shiny Floating Logo Sparkle
            Text(
                text = "✨🌸✨",
                fontSize = 32.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Dynamic scaling cards row for mascots
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .graphicsLayer {
                        scaleX = scaleMascots
                        scaleY = scaleMascots
                        alpha = scaleMascots
                    }
            ) {
                // Pio 🐰 Container (Pink)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFFFC0CB).copy(alpha = 0.8f))
                        .padding(12.dp)
                ) {
                    Text("🐰", fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Pio", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                // Coco 🐱 Container (Peach)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFFFDAB9).copy(alpha = 0.8f))
                        .padding(12.dp)
                ) {
                    Text("🐱", fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Coco", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                // Onal 🐷 Container (Rose)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFFFB6C1).copy(alpha = 0.8f))
                        .padding(12.dp)
                ) {
                    Text("🐷", fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Onal", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = "Charisna Saving",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = themeState.onSurface,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle
            Text(
                text = "Kelola Keuangan & Tabungan Ceria Bersama Pio, Coco, dan Onal! 🌸✨",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Progress loader
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White.copy(alpha = 0.4f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                        .clip(RoundedCornerShape(4.dp))
                        .background(themeState.primary)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Clickable Enter Button to quick action
            Button(
                onClick = onFinish,
                colors = ButtonDefaults.buttonColors(containerColor = themeState.accent),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Masuk Beranda 🎀",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}
