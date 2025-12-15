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
    val state by viewModel.detailState.collectAsState()

    LaunchedEffect(key1 = allergyId) {
        viewModel.getDetailAllergy(allergyId)
    }

    val themeColor = Color(0xFF347D85)
    val backgroundColor = Color(0xFFF2F5F8) // Abu-abu muda untuk background layar

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
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
        bottomBar = {}
    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(backgroundColor)
        ) {
            when (state) {
                is UiResult.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = themeColor
                    )
                }
                is UiResult.Error -> {
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
        // CARD
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                // GAMBAR
                if (!item.imageUrl.isNullOrEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(item.imageUrl),
                        contentDescription = item.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    )
                } else {
                    // Placeholder no gambar
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

                // KONTEN
                Column(modifier = Modifier.padding(20.dp)) {
                    // Judul Makanan
                    Text(
                        text = item.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = item.description,
                        fontSize = 15.sp,
                        color = Color(0xFF4A4A4A),
                        lineHeight = 24.sp,
                        textAlign = TextAlign.Justify
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}