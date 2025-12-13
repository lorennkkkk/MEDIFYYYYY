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

    // UBAH: Sekarang menyimpan URI, bukan File
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    // State untuk representasi File yang akan di-upload (akan diubah saat simpan)
    var imageFile by remember { mutableStateOf<File?>(null) }

    val addState by viewModel.addSertifikatState.collectAsState()

    // 1. LAUNCHER UNTUK MEMILIH GAMBAR
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            imageUri = uri // Simpan URI yang dipilih
        }
    )

    // Efek samping untuk menangani navigasi setelah simpan
    LaunchedEffect(addState) {
        when (addState) {
            is UIResult.Success -> {
                viewModel.resetAddSertifikatState()
                navController.popBackStack()
            }
            is UIResult.Error -> {
                // Tampilkan pesan error di sini
                // Contoh: Log.e("AddVaksin", "Gagal: ${e.message}")
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 2. PANGGIL LAUNCHER KETIKA DIKLIK
            UploadImageSection(imageUri != null) {
                imagePickerLauncher.launch("image/*") // Membuka galeri
            }
            Spacer(Modifier.height(16.dp))

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
                    Icon(Icons.Filled.CalendarToday, contentDescription = "Pilih Tanggal",
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
                    // 3. LOGIKA SEBELUM SUBMIT: Konversi URI menjadi File nyata untuk Repositori
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
                // KRUSIAL: Sekarang bergantung pada imageUri (yang didapat dari galeri)
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

// FUNGSI UTILITY: Mengkonversi URI menjadi File (Wajib untuk Supabase upload)
// Fungsi ini harus didefinisikan di suatu tempat yang bisa diakses, jika tidak, taruh di file ini dulu.
fun uriToFile(context: android.content.Context, uri: Uri): File? {
    // Implementasi ini SANGAT kompleks dan bervariasi. Untuk tujuan demonstrasi/debugging:
    // Paling umum: Salin isi URI ke file sementara (temp file)

    // **ANDA HARUS MENGUBAH INI dengan implementasi yang BENAR**
    // (yang menangani ContentResolver dan I/O stream) di proyek nyata Anda.

    // Sebagai *placeholder* sederhana agar kompilasi berhasil:
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File(context.cacheDir, "temp_upload_${System.currentTimeMillis()}.jpg")
        tempFile.outputStream().use { output ->
            inputStream?.copyTo(output)
        }
        inputStream?.close()
        tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// --- KOMPONEN PENDUKUNG (TETAP SAMA) ---

@Composable
fun AddVaksinTopBar(x0: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Tambah Sertifikat Vaksin",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable {
                x0.popBackStack()
            }
        )
    }
}

@Composable
fun UploadImageSection(isUploaded: Boolean, onUploadClicked: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isUploaded) MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f) else Color.LightGray.copy(alpha = 0.2f))
            .padding(20.dp)
            .clickable(onClick = onUploadClicked)
    ) {
        if (!isUploaded) {
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