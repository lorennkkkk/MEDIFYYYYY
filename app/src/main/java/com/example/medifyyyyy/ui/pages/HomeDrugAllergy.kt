package com.example.medifyyyyy.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    
    // Warna Dinamis (Sesuai AddLogScreen & DetailLogScreen)
    // Light: Teal (0xFF00897B), Dark: Primary Theme Color
    val dynamicPrimaryColor = if (isDark) MaterialTheme.colorScheme.primary else Color(0xFF00897B)
    val dynamicOnPrimaryColor = if (isDark) MaterialTheme.colorScheme.onPrimary else Color.White
    
    val sectionTitleColor = if (isDark) MaterialTheme.colorScheme.onBackground else TealDark
    
    // Tombol Riwayat (Surface/Secondary)
    val btnHistoryBg = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color.White
    val btnHistoryContent = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else TealDark

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background, 
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(dynamicPrimaryColor)
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Profile Icon placeholder putih
                    Surface(shape = CircleShape, color = Color.White, modifier = Modifier.size(40.dp)) {}
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Daftar Alergi", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Pantauan Kesehatan", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    }
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(shape = CircleShape, color = if (isDark) Color.DarkGray else Color.Gray, modifier = Modifier.size(36.dp)) {}
                }
            }
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
                Column {
                    Text(
                        text = "Aksi Cepat",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = sectionTitleColor
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            // Tombol Tambah
                            ActionButton(
                                title = "Tambah Log",
                                icon = Icons.Default.Add,
                                bg = dynamicPrimaryColor,
                                contentColor = dynamicOnPrimaryColor,
                                onClick = { navController.navigate("add_log") }
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            // Tombol Riwayat
                            ActionButton(
                                title = "Riwayat",
                                icon = Icons.Outlined.Medication,
                                bg = btnHistoryBg,
                                contentColor = btnHistoryContent,
                                onClick = { navController.navigate("side_effect_log") }
                            )
                        }
                    }
                }
            }

            item { DailySummarySection() }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Log Terbaru",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = sectionTitleColor 
                    )
                    if (logs.isNotEmpty()) {
                        Text(
                            text = "Lihat Semua",
                            fontSize = 13.sp,
                            color = dynamicPrimaryColor, 
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = dynamicPrimaryColor) 
                    }
                }
            } else if (logs.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Belum ada riwayat tercatat.",
                            color = if (isDark) Color.LightGray else Color.Gray,
                            fontStyle = FontStyle.Italic,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                items(logs) { log ->
                    DrugLogCard(log, onClick = {
                        navController.navigate(Screen.DetailLog.build(log.id.toString()))
                    })
                }
            }

            item { AlertCard() }
        }
    }
}
