package com.example.medifyyyyy.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.medifyyyyy.domain.model.DrugLog
import com.example.medifyyyyy.ui.theme.*
import com.example.medifyyyyy.ui.viewmodel.LogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailLogScreen(
    navController: NavController,
    logId: Long,
    viewModel: LogViewModel
) {
    var log by remember { mutableStateOf<DrugLog?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Load data saat pertama kali dibuka
    LaunchedEffect(logId) {
        log = viewModel.getDrugLog(logId)
        isLoading = false
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Log?") },
            text = { Text("Apakah Anda yakin ingin menghapus catatan ini secara permanen?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteDrugLog(logId) {
                            showDeleteDialog = false
                            navController.popBackStack()
                        }
                    }
                ) {
                    Text("Hapus", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Detail Log", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                actions = {
                    if (!isLoading && log != null) {
                        IconButton(onClick = { navController.navigate("edit_log/$logId") }) {
                            Icon(Icons.Default.Edit, "Edit", tint = Color.White)
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, "Delete", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF00897B))
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF00897B))
            }
        } else if (log == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Data tidak ditemukan", color = Color.Gray)
            }
        } else {
            val item = log!!
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Header Info Obat
                Text(item.drugName, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TealDark)
                Text(item.dosage ?: "-", fontSize = 16.sp, color = Color.Gray)
                
                Spacer(modifier = Modifier.height(16.dp))

                // Info Waktu & Status
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = when(item.status) {
                            "Ringan" -> TealPrimary
                            "Sedang" -> OrangePrimary
                            "Berat" -> OrangeDark
                            else -> Color.Gray
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            item.status ?: "-",
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(item.time ?: "-", fontSize = 14.sp, color = TealDark)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Deskripsi
                Text("Deskripsi Efek Samping", fontWeight = FontWeight.Bold, color = TealDark)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    item.description ?: "Tidak ada deskripsi",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Gambar (Jika Ada)
                if (item.hasImage) {
                     Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().height(250.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().background(Color.LightGray)) {
                             // Menggunakan item.imageUrl yang sudah di-resolve di repository
                             if (item.imageUrl != null) {
                                 AsyncImage(
                                     model = item.imageUrl,
                                     contentDescription = "Foto Bukti",
                                     contentScale = ContentScale.Crop,
                                     modifier = Modifier.fillMaxSize()
                                 )
                             } else {
                                 // Fallback jika URL gagal di-resolve meskipun hasImage true
                                 CircularProgressIndicator()
                             }
                        }
                    }
                }
            }
        }
    }
}
