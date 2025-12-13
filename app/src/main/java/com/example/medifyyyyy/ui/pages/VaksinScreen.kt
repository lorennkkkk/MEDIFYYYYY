package com.example.medifyyyyy.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.medifyyyyy.ui.viewmodel.VaksinViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun VaksinScreen(
    navController: NavController,
    viewModel: VaksinViewModel = viewModel(),
    // Navigasi onAddVaksin yang dihandle oleh AppNavHost
    onAddVaksin: () -> Unit,
) {
    val sertifikatListState by viewModel.sertifikatListState.collectAsState()

    Scaffold(
        // PERBAIKAN: topBar = { VaksinTopBar(navController) } DIHAPUS TOTAL

        floatingActionButton = {
            FloatingActionButton(
                // PERBAIKAN: Menggunakan lambda onAddVaksin yang sudah diperbaiki
                onClick = { onAddVaksin() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Tambah Sertifikat")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->

        when (val state = sertifikatListState) {
            is UIResult.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is UIResult.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text("Gagal memuat data: ${state.exception.localizedMessage ?: "Unknown Error"}")
                }
            }
            is UIResult.Success -> {
                if (state.data.isEmpty()) {
                    // Panggilan ke Composable yang kini terdefinisi
                    EmptyVaksinState(modifier = Modifier.padding(paddingValues))
                } else {
                    // Panggilan ke Composable yang kini terdefinisi
                    VaksinListContent(
                        sertifikatList = state.data,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
            UIResult.Idle -> {}
        }
    }
}

// --- DEFINISI KOMPONEN PENDUKUNG (Semua fungsi Composable di luar fungsi utama) ---
// Note: VaksinTopBar Dihapus

@Composable
fun VaksinListContent(sertifikatList: List<SertifikatVaksin>, modifier: Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(sertifikatList) { sertifikat ->
            SertifikatVaksinCard(sertifikat = sertifikat)
        }
    }
}

@Composable
fun EmptyVaksinState(modifier: Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Tidak ada data sertifikat",
            tint = Color.LightGray,
            modifier = Modifier.size(96.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Belum ada sertifikat vaksin yang diunggah.",
            color = Color.Gray,
            fontSize = 16.sp
        )
    }
}

@Composable
fun SertifikatVaksinCard(sertifikat: SertifikatVaksin) {
    val dateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { /* Tampilkan detail/gambar sertifikat */ },
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Science,
                contentDescription = sertifikat.jenisVaksin,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .padding(8.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = sertifikat.namaLengkap,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${sertifikat.jenisVaksin} (${sertifikat.dosis})",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "Tanggal: ${dateFormatter.format(sertifikat.tanggalVaksinasi)}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
