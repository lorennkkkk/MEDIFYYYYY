package com.example.medifyyyyy.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.medifyyyyy.domain.model.AllergyFood
import com.example.medifyyyyy.ui.common.UiResult
import com.example.medifyyyyy.ui.viewmodel.AllergyFoodViewModel
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AllergyListScreen(
    viewModel: AllergyFoodViewModel,
    onNavigateToAdd: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onBack: () -> Unit
) {

    LaunchedEffect(Unit) {
        viewModel.loadAllergies()
    }
    val state by viewModel.allergyListState.collectAsState()
    val headerColor = Color(0xFF347D85)

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerColor)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Daftar Konsumsi", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Penyebab Alergi", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }
        },

        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = headerColor,
                contentColor = Color.White,
                modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth(0.9f)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tambah Info Makanan")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->

        // Content
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {

            // LIST KARTU
            when (val result = state) {
                is UiResult.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = headerColor)
                    }
                }
                is UiResult.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: ${result.message}", color = Color.Red)
                    }
                }
                is UiResult.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(result.data) { item ->
                            AllergyCard(
                                item = item,
                                onClick = { onNavigateToDetail(item.id) },
                                onDelete = { viewModel.deleteAllergy(item.id) }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun AllergyCard(item: AllergyFood, onClick: () -> Unit, onDelete: () -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("dd MMM, HH:mm")
        .withLocale(Locale("id", "ID"))
        .withZone(ZoneId.systemDefault())

    val dateString = try { formatter.format(item.createdAt) } catch (e: Exception) { "" }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            if (!item.imageUrl.isNullOrEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(item.imageUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray)
                )
            } else {
                Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)).background(Color.Gray))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(dateString, color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(item.description, maxLines = 2, color = Color.DarkGray, fontSize = 13.sp, lineHeight = 16.sp)
            }
            Column(verticalArrangement = Arrangement.Bottom) {
                Spacer(modifier = Modifier.height(40.dp))
                Row {
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Gray, modifier = Modifier.size(20.dp).clickable { onDelete() })
                }
            }
        }
    }
}