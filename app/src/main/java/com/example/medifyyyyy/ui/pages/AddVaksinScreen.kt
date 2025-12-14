package com.example.medifyyyyy.ui.pages

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.medifyyyyy.ui.viewmodel.VaksinViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.util.Log
import android.webkit.MimeTypeMap

@Composable
fun AddVaksinScreen(
    navController: NavController,
    viewModel: VaksinViewModel = viewModel(),
    onBack: () -> Unit = {},
) {
    val context = LocalContext.current

    // State Formulir
    var namaLengkap by remember { mutableStateOf("Budi Hartono") }
    var jenisVaksin by remember { mutableStateOf("AstraZeneca") }
    var dosis by remember { mutableStateOf("Booster") }
    var tanggalVaksinasi by remember { mutableStateOf<Date?>(Date()) }
    var tempatVaksinasi by remember { mutableStateOf("Klinik Sehat") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageFile by remember { mutableStateOf<File?>(null) }

    val addState by viewModel.addSertifikatState.collectAsState()

    // LAUNCHER UNTUK MEMILIH GAMBAR
    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        uri ?: return@rememberLauncherForActivityResult
    }

    // PERBAIKAN: Efek samping untuk menangani navigasi dan error
    LaunchedEffect(addState) {
        when (addState) {
            is UIResult.Success -> {
                Log.d("VaksinUpload", "Upload dan Simpan BERHASIL!")
                viewModel.resetAddSertifikatState()
                navController.popBackStack()
            }
            is UIResult.Error -> {
                val exception = (addState as UIResult.Error).exception
                val errorMessage = exception.localizedMessage ?: "Error tidak diketahui."

                // KRUSIAL: Logging error yang detail
                Log.e("VaksinUpload", "Upload GAGAL: $errorMessage", exception)

                if (exception is IllegalStateException && errorMessage.contains("User not logged in")) {
                    // Jika token kedaluwarsa, biarkan AppNavHost menavigasi ke Login
                } else {
                    // Error lain (Storage/Postgrest gagal)
                    // Anda bisa menambahkan Snackbar/Toast di sini untuk memberi tahu user.
                }

                viewModel.resetAddSertifikatState()
            }
            else -> {}
        }
    }

    val isLoading = addState is UIResult.Loading

    Scaffold(
        topBar = { AddVaksinTopBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            UploadImageSection(imageUri) {
                if (!isLoading) {
                    imagePickerLauncher.launch("image/*") // Membuka galeri
                }
            }
            Spacer(Modifier.height(16.dp))

            // Formulir Input (Tetap sama)
            OutlinedTextField(value = namaLengkap, onValueChange = { namaLengkap = it }, label = { Text("Nama Lengkap") }, modifier = Modifier.fillMaxWidth(), enabled = !isLoading)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = jenisVaksin, onValueChange = { jenisVaksin = it }, label = { Text("Jenis Vaksin") }, modifier = Modifier.fillMaxWidth(), enabled = !isLoading)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = dosis, onValueChange = { dosis = it }, label = { Text("Dosis") }, modifier = Modifier.fillMaxWidth(), enabled = !isLoading)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = tanggalVaksinasi?.let { SimpleDateFormat("dd/MM/yyyy", Locale("id", "ID")).format(it) } ?: "Pilih tanggal",
                onValueChange = { /* readOnly */ },
                label = { Text("Tanggal Vaksinasi") },
                trailingIcon = {
                    Icon(Icons.Filled.CalendarToday, contentDescription = "Pil zih Tanggal",
                        Modifier.clickable { tanggalVaksinasi = Date() }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                enabled = !isLoading
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = tempatVaksinasi, onValueChange = { tempatVaksinasi = it }, label = { Text("Tempat Vaksinasi") }, modifier = Modifier.fillMaxWidth(), enabled = !isLoading)
            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    // KRUSIAL: Konversi URI menjadi File sebelum dikirim ke ViewModel/Repository
                    if (imageUri != null) {
                        imageFile = uriToFile(context, imageUri!!)
                    }

                    if (jenisVaksin.isNotBlank() && dosis.isNotBlank() && tanggalVaksinasi != null && tempatVaksinasi.isNotBlank() && imageFile != null) {
                        viewModel.addSertifikat(
                            namaLengkap = namaLengkap,
                            jenisVaksin = jenisVaksin,
                            dosis = dosis,
                            tanggal = tanggalVaksinasi!!,
                            tempatVaksinasi = tempatVaksinasi,
                            imageFile = imageFile
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading && imageUri != null,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Text("Simpan Sertifikat", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}


// FUNGSI UTILITY KRUSIAL: Mengkonversi URI Content Provider menjadi File sementara
fun uriToFile(context: android.content.Context, uri: Uri): File? {
    val contentResolver = context.contentResolver.openInputStream(uri) ?: return null
    val mime = context.contentResolver.getType(uri) ?: "image/*"
    val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime) ?: "img"
    val tempFile = File(context.cacheDir, "temp_upload_${System.currentTimeMillis()}.$extension")

    return try {
        contentResolver.use { inputStream ->
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        if (tempFile.exists() && tempFile.length() > 0) {
            tempFile

        } else {
            null
        }
    } catch (e: Exception) {
        android.util.Log.e("VaksinUpload", "Error converting URI to File: ${e.message}")
        e.printStackTrace()
        null
    }
}


// --- KOMPONEN PENDUKUNG ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVaksinTopBar(navController: NavController) {
    // PERBAIKAN: Menggunakan TopAppBar standar Material 3 untuk navigasi yang benar
    TopAppBar(
        title = { Text(text = "Tambah Sertifikat Vaksin", fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali")
            }
        }
    )
}

@Composable
fun UploadImageSection(imageUri: Uri?, onUploadClicked: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (imageUri!=null) MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f) else Color.LightGray.copy(alpha = 0.2f))
            .padding(20.dp)
            .clickable(onClick = onUploadClicked)
    ) {
        val isUploaded = imageUri == null

        if (isUploaded) {
            Icon(
                Icons.Filled.UploadFile,
                contentDescription = "Upload Sertifikat",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text("Unggah Foto Sertifikat Vaksin", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            Text("Tap untuk unggah file JPG/PNG", color = Color.Gray, fontSize = 12.sp)
        } else {
            Icon(
                Icons.Default.AutoAwesome,
                contentDescription = "Sertifikat Terunggah",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text("Sertifikat berhasil dipilih!", color = Color(0xFF4CAF50), fontWeight = FontWeight.SemiBold)
            Text("Tap untuk ganti foto", color = Color.Gray, fontSize = 12.sp)
        }
    }
}