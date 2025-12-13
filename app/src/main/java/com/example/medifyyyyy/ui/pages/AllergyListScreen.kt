package com.example.medifyyyyy.ui.pages

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
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
    onNavigateToDetail: (String) -> Unit
) {
    // 1. Ambil Data saat halaman dibuka
    LaunchedEffect(Unit) {
        viewModel.loadAllergies()
    }

    // 2. Baca status data
    val state by viewModel.allergyListState.collectAsState()

    // State untuk Search Bar
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = Color(0xFF00897B),
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

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            // --- HEADER ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF00897B))
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = Color.White, modifier = Modifier.size(40.dp)) {}
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Daftar Alergi", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Pantauan Kesehatan", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    }
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(shape = CircleShape, color = Color.Gray, modifier = Modifier.size(36.dp)) {}
                }
            }

            // --- SEARCH BAR ---
            Box(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)),
                    placeholder = { Text("Cari nama makanan...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color(0xFF00897B)
                    )
                )
            }

            // --- LIST KARTU ---
            when (val result = state) {
                is UiResult.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF00897B))
                    }
                }
                is UiResult.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: ${result.message}", color = Color.Red)
                    }
                }
                is UiResult.Success -> {
                    val filteredList = result.data.filter {
                        it.name.contains(searchQuery, ignoreCase = true)
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Kalau 'items' masih merah, pastikan import androidx.compose.foundation.lazy.items ada di atas
                        items(filteredList) { item ->
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
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray, modifier = Modifier.size(20.dp).clickable { })
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Gray, modifier = Modifier.size(20.dp).clickable { onDelete() })
                }
            }
        }
    }
}