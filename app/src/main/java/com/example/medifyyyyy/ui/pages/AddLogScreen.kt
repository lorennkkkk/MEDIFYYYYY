package com.example.medifyyyyy.ui.pages

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.medifyyyyy.domain.model.DrugLog
import com.example.medifyyyyy.ui.theme.*
import com.example.medifyyyyy.ui.viewmodel.LogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLogScreen(
    navController: NavController,
    viewModel: LogViewModel = viewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // State untuk Form DrugLog
    var drugName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("Ringan") }
    
    // State untuk Image
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { imageUri = it }

    val statusOptions = listOf("Ringan", "Sedang", "Berat")
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Tambah Log Efek Samping", color = Color.White) },
                navigationIcon = { 
                    IconButton(onClick = { navController.popBackStack() }) { 
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White) 
                    } 
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF00897B))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp) // Disamakan dengan AddAllergyScreen
                .verticalScroll(scrollState)
        ) {
            // --- Bagian Gambar ---
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BackgroundLight),
                border = BorderStroke(1.dp, Color(0xFF00897B)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (imageUri != null) {
                        AsyncImage(model = imageUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        IconButton(
                            onClick = { imageUri = null },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(Color.Black.copy(0.5f), CircleShape)
                                .size(30.dp)
                        ) { Icon(Icons.Default.Close, null, tint = CardWhite, modifier = Modifier.padding(4.dp)) }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Image, null, tint = Color(0xFF00897B), modifier = Modifier.size(48.dp))
                            Text("Belum ada foto dipilih", color = Color(0xFF00897B))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            
            // Tombol Galeri
            Button(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealDark)
            ) {
                Icon(Icons.Default.PhotoLibrary, null, tint = CardWhite)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pilih dari Galeri", color = CardWhite)
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // --- Form Input ---
            Text("Detail Obat", style = MaterialTheme.typography.titleMedium, color = Color(0xFF00897B), fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = drugName,
                onValueChange = { drugName = it },
                label = { Text("Nama Obat") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = CardWhite, unfocusedContainerColor = CardWhite, focusedBorderColor = Color(0xFF00897B), unfocusedBorderColor = Color(0xFF00897B).copy(alpha = 0.5f))
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = dosage,
                onValueChange = { dosage = it },
                label = { Text("Dosis (Contoh: 500mg)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = CardWhite, unfocusedContainerColor = CardWhite, focusedBorderColor = Color(0xFF00897B), unfocusedBorderColor = Color(0xFF00897B).copy(alpha = 0.5f))
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = time,
                onValueChange = { time = it },
                label = { Text("Waktu Minum (Contoh: Pagi, 08:00)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = CardWhite, unfocusedContainerColor = CardWhite, focusedBorderColor = Color(0xFF00897B), unfocusedBorderColor = Color(0xFF00897B).copy(alpha = 0.5f))
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Tingkat Keparahan", style = MaterialTheme.typography.bodyMedium, color = TealDark, fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                statusOptions.forEach { level ->
                    val isSelected = selectedStatus == level
                    val bgColor = if (isSelected) {
                        when (level) {
                            "Ringan" -> Color(0xFF00897B)
                            "Sedang" -> OrangePrimary
                            "Berat" -> OrangeDark
                            else -> Color(0xFF00897B)
                        }
                    } else CardWhite
                    val textColor = if (isSelected) CardWhite else Color.Gray
                    val borderColor = if (isSelected) Color.Transparent else Color(0xFF00897B).copy(alpha = 0.5f)
                    
                    OutlinedButton(
                        onClick = { selectedStatus = level },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = bgColor),
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Text(text = level, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Deskripsi Efek Samping") },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 8,
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = CardWhite, unfocusedContainerColor = CardWhite, focusedBorderColor = Color(0xFF00897B), unfocusedBorderColor = Color(0xFF00897B).copy(alpha = 0.5f))
            )
            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    if (drugName.isBlank()) {
                        Toast.makeText(context, "Nama obat wajib diisi!", Toast.LENGTH_SHORT).show()
                    } else {
                        val newLog = DrugLog(
                            drugName = drugName,
                            dosage = dosage,
                            time = time,
                            status = selectedStatus,
                            description = description,
                            hasImage = imageUri != null,
                            imageCount = if (imageUri != null) 1 else 0
                        )
                        viewModel.addDrugLog(newLog) {
                            Toast.makeText(context, "Log berhasil disimpan", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B)),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = CardWhite, modifier = Modifier.size(24.dp))
                } else {
                    Text("Simpan Log", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CardWhite)
                }
            }
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}
