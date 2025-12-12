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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.medifyyyyy.data.repository.LogRepository
import com.example.medifyyyyy.domain.model.AllergyLog
import com.example.medifyyyyy.ui.theme.* @OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLogScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var allergenName by remember { mutableStateOf("") }
    var symptoms by remember { mutableStateOf("") }
    var selectedSeverity by remember { mutableStateOf("Ringan") }
    val severityOptions = listOf("Ringan", "Sedang", "Berat")
    var isLoading by remember { mutableStateOf(false) }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { imageUri = it }

    Scaffold(
        containerColor = BackgroundLight, // ✅ SoftIce -> BackgroundLight
        topBar = {
            TopAppBar(title = { Text("Tambah Log Baru", fontWeight = FontWeight.Bold, color = CardWhite) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = CardWhite) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = TealPrimary))
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(scrollState).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = BackgroundLight), modifier = Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(20.dp))) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (imageUri != null) {
                        AsyncImage(model = imageUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        IconButton(onClick = { imageUri = null }, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).background(Color.Black.copy(0.5f), CircleShape).size(30.dp)) { Icon(Icons.Default.Close, null, tint = CardWhite, modifier = Modifier.padding(4.dp)) }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Default.Image, null, tint = TealPrimary, modifier = Modifier.size(48.dp)); Text("Belum ada foto dipilih", color = TealPrimary) }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { Toast.makeText(context, "Kamera...", Toast.LENGTH_SHORT).show() }, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)) { Icon(Icons.Default.CameraAlt, null, tint = CardWhite); Spacer(modifier = Modifier.width(8.dp)); Text("Kamera", color = CardWhite) }
                // Tombol Galeri: Menggunakan TealDark sebagai alternatif warna sekunder
                Button(onClick = { galleryLauncher.launch("image/*") }, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = TealDark)) { Icon(Icons.Default.PhotoLibrary, null, tint = CardWhite); Spacer(modifier = Modifier.width(8.dp)); Text("Galeri", color = CardWhite) }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("Detail Alergi", style = MaterialTheme.typography.titleMedium, color = TealPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = allergenName, onValueChange = { allergenName = it }, label = { Text("Nama Alergen") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = CardWhite, unfocusedContainerColor = CardWhite, focusedBorderColor = TealPrimary, unfocusedBorderColor = TealPrimary.copy(alpha = 0.5f)))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Tingkat Keparahan", style = MaterialTheme.typography.bodyMedium, color = TealDark, fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                severityOptions.forEach { level ->
                    val isSelected = selectedSeverity == level
                    // Mapping warna tombol severity
                    val bgColor = if (isSelected) { when (level) { "Ringan" -> TealPrimary; "Sedang" -> OrangePrimary; "Berat" -> OrangeDark; else -> TealPrimary } } else CardWhite
                    val textColor = if (isSelected) CardWhite else Color.Gray
                    val borderColor = if (isSelected) Color.Transparent else TealPrimary.copy(alpha = 0.5f)
                    OutlinedButton(onClick = { selectedSeverity = level }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(50), colors = ButtonDefaults.outlinedButtonColors(containerColor = bgColor), border = BorderStroke(1.dp, borderColor)) { Text(text = level, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = symptoms, onValueChange = { symptoms = it }, label = { Text("Gejala") }, modifier = Modifier.fillMaxWidth().height(150.dp), shape = RoundedCornerShape(12.dp), maxLines = 8, colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = CardWhite, unfocusedContainerColor = CardWhite, focusedBorderColor = TealPrimary, unfocusedBorderColor = TealPrimary.copy(alpha = 0.5f)))
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = { /* Logic Simpan */ },
                modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = TealPrimary), enabled = !isLoading
            ) { if (isLoading) CircularProgressIndicator(color = CardWhite) else Text("Simpan Log", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CardWhite) }
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}