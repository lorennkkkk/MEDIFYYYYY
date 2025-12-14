package com.example.medifyyyyy.ui.pages

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
    logId: Long? = null
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val isDark = isSystemInDarkTheme()

    // Warna Dinamis
    val primaryColor = if (isDark) MaterialTheme.colorScheme.primary else Color(0xFF00897B)
    val onPrimaryColor = if (isDark) MaterialTheme.colorScheme.onPrimary else Color.White

    // State Data
    var drugName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("Ringan") }

    // State Gambar
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var existingImageUrl by remember { mutableStateOf<String?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imageUri = uri
            imageBytes = context.contentResolver.openInputStream(uri)?.readBytes()
        }
    }

    val isLoading by viewModel.isLoading.collectAsState()
    val errorState by viewModel.error.collectAsState()

    // Logic Load Data Edit
    LaunchedEffect(logId) {
        if (logId != null) {
            viewModel.getDrugLog(logId)?.let { log ->
                drugName = log.drugName
                dosage = log.dosage ?: ""
                time = log.time ?: ""
                description = log.description ?: ""
                selectedStatus = log.status ?: "Ringan"
                existingImageUrl = log.imageUrl
            }
        }
    }

    // Logic Error Toast
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
                title = { Text(if (logId == null) "Tambah Log Efek Samping" else "Edit Log Efek Samping", color = onPrimaryColor) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = onPrimaryColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryColor)
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
            // 1. Bagian Gambar
            ImagePickerSection(
                imageUri = imageUri,
                existingImageUrl = existingImageUrl,
                primaryColor = primaryColor,
                onImageClick = { galleryLauncher.launch("image/*") },
                onClearImage = { imageUri = null; imageBytes = null }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Form Input
            Text("Detail Obat", style = MaterialTheme.typography.titleMedium, color = primaryColor, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            LogTextField(value = drugName, onValueChange = { drugName = it }, label = "Nama Obat", color = primaryColor)
            LogTextField(value = dosage, onValueChange = { dosage = it }, label = "Dosis (Contoh: 500mg)", color = primaryColor)
            LogTextField(value = time, onValueChange = { time = it }, label = "Waktu Minum (Contoh: Pagi, 08:00)", color = primaryColor)

            // 3. Pilihan Status
            SeveritySelector(
                selectedStatus = selectedStatus,
                primaryColor = primaryColor,
                onPrimaryColor = onPrimaryColor,
                isDark = isDark,
                onSelect = { selectedStatus = it }
            )

            LogTextField(value = description, onValueChange = { description = it }, label = "Deskripsi Efek Samping", color = primaryColor, isMultiLine = true)

            Spacer(modifier = Modifier.height(40.dp))

            // 4. Tombol Simpan
            Button(
                onClick = {
                    if (drugName.isBlank()) {
                        Toast.makeText(context, "Nama obat wajib diisi!", Toast.LENGTH_SHORT).show()
                    } else {
                        val newLog = DrugLog(
                            id = logId ?: 0,
                            drugName = drugName,
                            dosage = dosage,
                            time = time,
                            status = selectedStatus,
                            description = description,
                            hasImage = (imageUri != null) || (existingImageUrl != null)
                        )
                        val onDone = {
                            Toast.makeText(context, if (logId == null) "Log disimpan" else "Log diperbarui", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                        if (logId == null) viewModel.addDrugLog(newLog, imageBytes) { onDone() }
                        else viewModel.updateDrugLog(newLog, imageBytes) { onDone() }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(color = onPrimaryColor, modifier = Modifier.size(24.dp))
                else Text(if (logId == null) "Simpan Log" else "Update Log", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = onPrimaryColor)
            }
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

// --- Sub-Komponen (Untuk menyingkat kode utama) ---

@Composable
fun ImagePickerSection(
    imageUri: Uri?,
    existingImageUrl: String?,
    primaryColor: Color,
    onImageClick: () -> Unit,
    onClearImage: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, primaryColor),
        modifier = Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(20.dp)).clickable { onImageClick() }
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            val displayModel = imageUri ?: existingImageUrl
            if (displayModel != null) {
                AsyncImage(model = displayModel, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                IconButton(
                    onClick = onClearImage,
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).background(Color.Black.copy(0.5f), CircleShape).size(30.dp)
                ) { Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.padding(4.dp)) }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Image, null, tint = primaryColor, modifier = Modifier.size(48.dp))
                    Text("Ketuk untuk tambah foto", color = primaryColor)
                }
            }
        }
    }
}

@Composable
fun LogTextField(value: String, onValueChange: (String) -> Unit, label: String, color: Color, isMultiLine: Boolean = false) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().then(if (isMultiLine) Modifier.height(150.dp) else Modifier),
        shape = RoundedCornerShape(12.dp),
        maxLines = if (isMultiLine) 8 else 1,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = color,
            unfocusedBorderColor = color.copy(alpha = 0.5f)
        )
    )
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun SeveritySelector(
    selectedStatus: String,
    primaryColor: Color,
    onPrimaryColor: Color,
    isDark: Boolean,
    onSelect: (String) -> Unit
) {
    Text("Tingkat Keparahan", style = MaterialTheme.typography.bodyMedium, color = if(isDark) Color.LightGray else TealDark, fontWeight = FontWeight.SemiBold)
    Spacer(modifier = Modifier.height(8.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("Ringan", "Sedang", "Berat").forEach { level ->
            val isSelected = selectedStatus == level
            val bgColor = if (isSelected) {
                when (level) {
                    "Ringan" -> primaryColor
                    "Sedang" -> OrangePrimary
                    "Berat" -> OrangeDark
                    else -> primaryColor
                }
            } else MaterialTheme.colorScheme.surface
            val textColor = if (isSelected) onPrimaryColor else Color.Gray
            val borderColor = if (isSelected) Color.Transparent else primaryColor.copy(alpha = 0.5f)

            OutlinedButton(
                onClick = { onSelect(level) },
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
}