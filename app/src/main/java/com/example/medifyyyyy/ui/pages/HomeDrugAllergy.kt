package com.example.medifyyyyy.ui.pages

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medifyyyyy.ui.common.*
import com.example.medifyyyyy.ui.nav.Screen
import com.example.medifyyyyy.ui.theme.* 
import com.example.medifyyyyy.ui.viewmodel.LogViewModel

@Composable
fun HomeDrugAllergy(navController: NavController, viewModel: LogViewModel) {
    val logs by viewModel.drugLogs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // --- Konfigurasi Dark Mode ---
    val isDark = isSystemInDarkTheme()
    
    // Warna Dinamis
    val dynamicPrimaryColor = if (isDark) MaterialTheme.colorScheme.primary else Color(0xFF00897B)
    val dynamicOnPrimaryColor = if (isDark) MaterialTheme.colorScheme.onPrimary else Color.White
    val sectionTitleColor = if (isDark) MaterialTheme.colorScheme.onBackground else TealDark
    
    val btnHistoryBg = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color.White
    val btnHistoryContent = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else TealDark

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background, 
        topBar = {
            HomeHeader(dynamicPrimaryColor = dynamicPrimaryColor, dynamicOnPrimaryColor = dynamicOnPrimaryColor)
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
        ) {
            item { GreetingCard() }

            item {
                ActionButtonsRow(
                    onAddClick = { navController.navigate("add_log") },
                    onHistoryClick = { navController.navigate("side_effect_log") },
                    dynamicPrimaryColor = dynamicPrimaryColor,
                    dynamicOnPrimaryColor = dynamicOnPrimaryColor,
                    btnHistoryBg = btnHistoryBg,
                    btnHistoryContent = btnHistoryContent,
                    sectionTitleColor = sectionTitleColor
                )
            }

            item { DailySummarySection() }

            item {
                RecentLogsSection(
                    logs = logs,
                    isLoading = isLoading,
                    dynamicPrimaryColor = dynamicPrimaryColor,
                    sectionTitleColor = sectionTitleColor,
                    isDark = isDark,
                    onLogClick = { log ->
                        navController.navigate(Screen.DetailLog.build(log.id.toString()))
                    }
                )
            }

            item { AlertCard() }
        }
    }
}
