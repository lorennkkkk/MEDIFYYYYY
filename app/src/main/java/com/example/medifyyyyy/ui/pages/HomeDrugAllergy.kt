package com.example.medifyyyyy.ui.pages

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medifyyyyy.ui.common.*
import com.example.medifyyyyy.ui.nav.Screen
import com.example.medifyyyyy.ui.theme.* 
import com.example.medifyyyyy.ui.viewmodel.LogViewModel

@OptIn(ExperimentalMaterial3Api::class)
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
            TopAppBar(
                title = { 
                    Text(
                        "Daftar Alergi Obat", 
                        color = MaterialTheme.colorScheme.primary, 
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Back", 
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp)
        ) {
            // 1. Ucapan Halo
            item { GreetingCard() }

            // 2. Aksi Cepat (Tombol Tambah & Riwayat)
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

            // 3. Log Terbaru
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

            // 4. Alert Card (Paling Bawah)
            item { AlertCard() }
        }
    }
}
