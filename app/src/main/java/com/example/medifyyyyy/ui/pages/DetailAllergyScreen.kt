package com.example.medifyyyyy.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShowChart
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
import coil.compose.rememberAsyncImagePainter
import com.example.medifyyyyy.domain.model.AllergyFood
import com.example.medifyyyyy.ui.common.UiResult
import com.example.medifyyyyy.ui.viewmodel.AllergyFoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailAllergyScreen(
    allergyId: String, // Kita terima ID dari Navigasi
    viewModel: AllergyFoodViewModel,
    onBack: () -> Unit
) {
    // 1. Ambil Data dari ViewModel berdasarkan ID
    val state by viewModel.allergyListState.collectAsState()

    // Cari item yang ID-nya cocok
    val item: AllergyFood? = remember(state, allergyId) {
        if (state is UiResult.Success) {
            (state as UiResult.Success<List<AllergyFood>>).data.find { it.id == allergyId }
        } else null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Judul di Header (Nama Makanan)
                    Text(item?.name ?: "Detail", color = Color.White, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF00897B))
            )
        },
        bottomBar = {
            // Bottom Bar Dummy (Biar mirip desain)
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") })
                NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.ShowChart, null) }, label = { Text("Artikel") })
                NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Default.Medication, null) }, label = { Text("Obat") })
                NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.Person, null) }, label = { Text("Profil") })
            }
        }
    ) { padding ->

        if (item == null) {
            // Tampilan kalau data tidak ketemu / masih loading
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Data tidak ditemukan atau sedang memuat...")
            }
        } else {
            // Tampilan Utama
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(Color(0xFFF5F5F5)) // Background abu muda
            ) {

                // Card Putih Besar
                Card(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        // FOTO BESAR
                        if (!item.imageUrl.isNullOrEmpty()) {
                            Image(
                                painter = rememberAsyncImagePainter(item.imageUrl),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp) // Tinggi foto
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.LightGray)
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp)).background(Color.Gray))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // JUDUL (Hitam Tebal)
                        Text(
                            text = item.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // DESKRIPSI (Teks Paragraf)
                        Text(
                            text = item.description,
                            fontSize = 14.sp,
                            lineHeight = 22.sp, // Jarak antar baris biar enak dibaca
                            color = Color.DarkGray,
                            textAlign = TextAlign.Justify
                        )
                    }
                }
            }
        }
    }
}