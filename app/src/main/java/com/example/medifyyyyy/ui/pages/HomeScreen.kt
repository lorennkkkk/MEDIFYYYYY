package com.example.medifyyyyy.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.medifyyyyy.ui.theme.* // Memanggil warna baru (TealPrimary, BackgroundLight, dll)
import com.example.medifyyyyy.ui.viewmodel.LogViewModel

@Composable
fun HomeScreen(navController: NavController, viewModel: LogViewModel) {
    val logs by viewModel.allergyLogs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        containerColor = BackgroundLight, // Ganti SoftIce
        topBar = { HeaderSimple() }
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
                        color = TealDark // Ganti TextDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            // Tombol Tambah: Background TealPrimary, Teks CardWhite
                            ActionButton(
                                title = "Tambah Log",
                                icon = Icons.Default.Add,
                                bg = TealPrimary,
                                contentColor = CardWhite,
                                onClick = { navController.navigate("add_log") }
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            // Tombol Riwayat: Background CardWhite, Teks TealDark
                            ActionButton(
                                title = "Riwayat",
                                icon = Icons.Outlined.Medication,
                                bg = CardWhite,
                                contentColor = TealDark,
                                onClick = { navController.navigate("side_effects") }
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
                        color = TealDark // Ganti TextDark
                    )
                    if (logs.isNotEmpty()) {
                        Text(
                            text = "Lihat Semua",
                            fontSize = 13.sp,
                            color = TealPrimary, // Ganti TealMedical
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = TealPrimary) // Ganti TealMedical
                    }
                }
            } else if (logs.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Belum ada riwayat tercatat.",
                            color = Color.Gray,
                            fontStyle = FontStyle.Italic,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                items(logs) { log ->
                    LogItemCard(log)
                }
            }

            item { AlertCard() }
        }
    }
}