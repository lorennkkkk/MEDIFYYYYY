package com.example.medifyyyyy.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.medifyyyyy.domain.model.AllergyFood
import com.example.medifyyyyy.ui.common.UiResult
import com.example.medifyyyyy.ui.viewmodel.AllergyFoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailAllergyScreen(
    allergyId: String,
    viewModel: AllergyFoodViewModel,
    onBack: () -> Unit
) {
    // 1. Ambil State dari ViewModel
    val state by viewModel.detailState.collectAsState()

    // 2. Trigger ambil data saat halaman dibuka
    LaunchedEffect(key1 = allergyId) {
        viewModel.getDetailAllergy(allergyId)
    }

    // Warna Tema sesuai gambar (Teal Gelap)
    val themeColor = Color(0xFF347D85)
    val backgroundColor = Color(0xFFF2F5F8) // Abu-abu muda untuk background layar

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Judul di TopBar akan mengikuti data nama makanan jika sudah ada
                    val titleText = if (state is UiResult.Success) {
                        (state as UiResult.Success).data?.name ?: "Detail Alergi"
                    } else "Detail Alergi"

                    Text(
                        text = titleText,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = themeColor
                )
            )
        },
        // Hapus bottomBar sesuai permintaan
        bottomBar = {}
    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(backgroundColor) // Background abu-abu seluruh layar
        ) {
            when (state) {
                is UiResult.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = themeColor
                    )
                }
                is UiResult.Error -> {
                    // Menampilkan pesan error (mengambil pesan string)
                    val errorMsg = (state as UiResult.Error).message
                    Text(
                        text = errorMsg,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is UiResult.Success -> {
                    val item = (state as UiResult.Success).data
                    if (item != null) {
                        // TAMPILKAN KONTEN UTAMA
                        DetailContent(item = item)
                    } else {
                        Text("Data tidak ditemukan", modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
        }
    }
}

@Composable
fun DetailContent(item: AllergyFood) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp) // Jarak antara kartu dengan pinggir layar
    ) {
        // --- CARD PUTIH ---
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                // 1. GAMBAR (Full Width di atas Card)
                if (!item.imageUrl.isNullOrEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(item.imageUrl),
                        contentDescription = item.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp) // Tinggi gambar disesuaikan
                    )
                } else {
                    // Placeholder jika tidak ada gambar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No Image", color = Color.White)
                    }
                }

                // 2. KONTEN TEKS (Padding di dalam Card)
                Column(modifier = Modifier.padding(20.dp)) {
                    // Judul Makanan
                    Text(
                        text = item.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Deskripsi (Line height diatur agar enak dibaca)
                    Text(
                        text = item.description,
                        fontSize = 15.sp,
                        color = Color(0xFF4A4A4A), // Abu-abu gelap untuk teks bacaan
                        lineHeight = 24.sp, // Jarak antar baris biar tidak rapat
                        textAlign = TextAlign.Justify // Rata kanan kiri (opsional)
                    )
                }
            }
        }

        // Spacer bawah agar bisa discroll mentok
        Spacer(modifier = Modifier.height(30.dp))
    }
}