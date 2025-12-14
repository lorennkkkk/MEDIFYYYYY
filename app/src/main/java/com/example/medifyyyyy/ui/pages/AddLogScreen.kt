package com.example.medifyyyyy.ui.pages

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
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
    viewModel: LogViewModel = viewModel(),
    logId: Long? = null // Parameter opsional untuk mode edit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Cek Dark Mode
    val isDark = isSystemInDarkTheme()
    val dynamicPrimaryColor = if (isDark) MaterialTheme.colorScheme.primary else Color(0xFF00897B)
    val dynamicOnPrimaryColor = if (isDark) MaterialTheme.colorScheme.onPrimary else Color.White

    // State untuk Form DrugLog
    var drugName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("Ringan") }
    
    // State untuk Image
    var imageUri by remember { mutableStateOf<Uri?>(null) }       // Untuk preview gambar baru yang dipilih
    var imageBytes by remember { mutableStateOf<ByteArray?>(null) } // Data gambar untuk diupload
    var existingImageUrl by remember { mutableStateOf<String?>(null) } // URL gambar lama (saat edit)

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imageUri = uri
            // Baca data gambar dari URI menjadi ByteArray
            val inputStream = context.contentResolver.openInputStream(uri)
            imageBytes = inputStream?.readBytes()
        }
    }

    val statusOptions = listOf("Ringan", "Sedang", "Berat")
    val isLoading by viewModel.isLoading.collectAsState()
    
    // Observe error state from ViewModel
    val errorState by viewModel.error.collectAsState()

    // Mode Edit: Load data jika logId ada
    LaunchedEffect(logId) {
        if (logId != null) {
            val log = viewModel.getDrugLog(logId)
            if (log != null) {
                drugName = log.drugName
                dosage = log.dosage ?: ""
                time = log.time ?: ""
                description = log.description ?: ""
                selectedStatus = log.status ?: "Ringan"
                existingImageUrl = log.imageUrl // Simpan URL gambar lama
            }
        }
    }

    // Show Toast when error occurs
    LaunchedEffect(errorState) {
        errorState?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(if (logId == null) "Tambah Log Efek Samping" else "Edit Log Efek Samping", color = dynamicOnPrimaryColor) },
                navigationIcon = { 
                    IconButton(onClick = { navController.popBackStack() }) { 
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = dynamicOnPrimaryColor) 
                    } 
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = dynamicPrimaryColor)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            // --- Bagian Gambar ---
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, dynamicPrimaryColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { galleryLauncher.launch("image/*") }
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    // Logika Tampilan: Prioritaskan gambar baru (URI), lalu gambar lama (URL), lalu Placeholder
                    val displayModel = imageUri ?: existingImageUrl

                    if (displayModel != null) {
                        AsyncImage(
                            model = displayModel,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { 
                                imageUri = null
                                imageBytes = null
                                // Jangan hapus existingImageUrl di sini jika ingin membatalkan pilihan baru dan kembali ke gambar lama,
                                // tapi jika maksudnya "hapus gambar", kita butuh state tambahan.
                                // Untuk sekarang, tombol X hanya membatalkan gambar yang BARU dipilih.
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(Color.Black.copy(0.5f), CircleShape)
                                .size(30.dp)
                        ) { Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.padding(4.dp)) }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Image, null, tint = dynamicPrimaryColor, modifier = Modifier.size(48.dp))
                            Text("Ketuk untuk tambah foto", color = dynamicPrimaryColor)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            
            // --- Form Input ---
            Text("Detail Obat", style = MaterialTheme.typography.titleMedium, color = dynamicPrimaryColor, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = drugName,
                onValueChange = { drugName = it },
                label = { Text("Nama Obat") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = dynamicPrimaryColor,
                    unfocusedBorderColor = dynamicPrimaryColor.copy(alpha = 0.5f)
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = dosage,
                onValueChange = { dosage = it },
                label = { Text("Dosis (Contoh: 500mg)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = dynamicPrimaryColor,
                    unfocusedBorderColor = dynamicPrimaryColor.copy(alpha = 0.5f)
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = time,
                onValueChange = { time = it },
                label = { Text("Waktu Minum (Contoh: Pagi, 08:00)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = dynamicPrimaryColor,
                    unfocusedBorderColor = dynamicPrimaryColor.copy(alpha = 0.5f)
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Tingkat Keparahan", style = MaterialTheme.typography.bodyMedium, color = if(isDark) Color.LightGray else TealDark, fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                statusOptions.forEach { level ->
                    val isSelected = selectedStatus == level
                    val bgColor = if (isSelected) {
                        when (level) {
                            "Ringan" -> dynamicPrimaryColor
                            "Sedang" -> OrangePrimary
                            "Berat" -> OrangeDark
                            else -> dynamicPrimaryColor
                        }
                    } else MaterialTheme.colorScheme.surface
                    val textColor = if (isSelected) dynamicOnPrimaryColor else Color.Gray
                    val borderColor = if (isSelected) Color.Transparent else dynamicPrimaryColor.copy(alpha = 0.5f)
                    
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
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = dynamicPrimaryColor,
                    unfocusedBorderColor = dynamicPrimaryColor.copy(alpha = 0.5f)
                )
            )
            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    if (drugName.isBlank()) {
                        Toast.makeText(context, "Nama obat wajib diisi!", Toast.LENGTH_SHORT).show()
                    } else {
                        // Objek DrugLog dasar
                        val newLog = DrugLog(
                            id = logId ?: 0,
                            drugName = drugName,
                            dosage = dosage,
                            time = time,
                            status = selectedStatus,
                            description = description,
                            // Field imageCount dan hasImage akan dihandle di repository berdasarkan imageBytes
                            // Tapi kita set defaultnya di sini
                            hasImage = (imageUri != null) || (existingImageUrl != null)
                        )
                        
                        if (logId == null) {
                            // CREATE: Kirim imageBytes (bisa null jika tidak ada gambar)
                            viewModel.addDrugLog(newLog, imageBytes) {
                                Toast.makeText(context, "Log berhasil disimpan", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        } else {
                            // UPDATE: Kirim imageBytes (null jika gambar tidak berubah)
                            viewModel.updateDrugLog(newLog, imageBytes) {
                                Toast.makeText(context, "Log berhasil diperbarui", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = dynamicPrimaryColor),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = dynamicOnPrimaryColor, modifier = Modifier.size(24.dp))
                } else {
                    Text(if (logId == null) "Simpan Log" else "Update Log", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = dynamicOnPrimaryColor)
                }
            }
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}
