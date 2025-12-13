package com.example.medifyyyyy.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.medifyyyyy.ui.common.*
import com.example.medifyyyyy.ui.theme.* // Mengimpor warna baru
import com.example.medifyyyyy.ui.viewmodel.LogViewModel

@Composable
fun SideEffectListScreen(navController: NavController, viewModel: LogViewModel) {
    val drugLogs by viewModel.drugLogs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TealPrimary)
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = CardWhite) // ✅ Ganti PureWhite
                    }
                    Text(
                        text = "Catatan Efek Samping",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = CardWhite // ✅ Ganti PureWhite
                    )
                    Spacer(modifier = Modifier.width(48.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Hari ini, 15 Desember 2024", color = CardWhite.copy(0.9f), fontSize = 14.sp)
                Text("${drugLogs.size} obat dicatat", color = CardWhite.copy(0.7f), fontSize = 12.sp)
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate("add_log") },
                containerColor = TealPrimary, // ✅ Ganti TealMedical
                contentColor = CardWhite,     // ✅ Ganti PureWhite
                shape = RoundedCornerShape(50),
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tambah Log")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            // --- Bagian Pencarian & Filter ---
            Column(modifier = Modifier.padding(20.dp)) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Cari nama obat...", color = Color.Gray) }, // ✅ Ganti TextGray
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardWhite, RoundedCornerShape(12.dp)), // ✅ Ganti PureWhite
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CardWhite, // ✅ Ganti PureWhite
                        unfocusedContainerColor = CardWhite,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Chip Aktif menggunakan TealPrimary
                    FilterChipItem("Semua", true, TealPrimary)

                    // Chip Tidak Aktif menggunakan warna netral (Gray)
                    FilterChipItem("Ringan", false, Color.Gray) // ✅ Ganti LightCyanGray
                    FilterChipItem("Sedang", false, Color.Gray)
                }
            }

            // --- List Data ---
            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (isLoading) {
                    item {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = TealPrimary) // ✅ Ganti TealMedical
                        }
                    }
                } else if (drugLogs.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(top = 50.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Tidak ada riwayat", color = Color.Gray, fontStyle = FontStyle.Italic)
                        }
                    }
                } else {
                    items(drugLogs) {
                        DrugLogCard(it)
                    }
                }
            }
        }
    }
}