package com.example.medifyyyyy.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    // State Formulir
    var namaLengkap by remember { mutableStateOf("Budi Hartono") }
    var jenisVaksin by remember { mutableStateOf("AstraZeneca") }
    var dosis by remember { mutableStateOf("Booster") }
    var tanggalVaksinasi by remember { mutableStateOf<Date?>(Date()) } // Default ke hari ini
    var tempatVaksinasi by remember { mutableStateOf("Klinik Sehat") }
    var imageFile by remember { mutableStateOf<File?>(null) } // File yang akan di-upload

    val addState by viewModel.addSertifikatState.collectAsState()

    // Efek samping untuk menangani navigasi setelah simpan
    LaunchedEffect(addState) {
        when (addState) {
            is UIResult.Success -> {
                viewModel.resetAddSertifikatState() // Reset state
                navController.popBackStack() // Kembali ke daftar
            }
            is UIResult.Error -> {
                // Tampilkan Snackbar/Toast jika ada error
                // Misalnya: Snackbar("Gagal menyimpan: ${(addState as UIResult.Error).exception.message}")
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
            // Ketika tombol di UploadImageSection ditekan, update state imageFile
            UploadImageSection(imageFile != null) {
                // Simulasi file yang dipilih/diambil
                imageFile = File("path/to/temp/certificate_${System.currentTimeMillis()}.jpg")
            }
            Spacer(Modifier.height(16.dp))

            // ... Formulir Input (Sama seperti sebelumnya, tambahkan `enabled = !isLoading`)
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
                        // Simulasi Date Picker, di real project gunakan proper DatePickerDialog
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
                enabled = !isLoading && imageFile != null, // Hanya enable jika tidak loading dan file sudah dipilih
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

// **UploadImageSection (Pembaruan untuk menggunakan Boolean)**
@Composable
fun UploadImageSection(isUploaded: Boolean, onUploadClicked: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isUploaded) MaterialTheme.colorScheme.secondary else Color.LightGray.copy(alpha = 0.2f))
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
                Icons.Default.AutoAwesome, // Ganti dengan icon centang
                contentDescription = "Sertifikat Terunggah",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text("Sertifikat berhasil diunggah!", color = Color(0xFF4CAF50), fontWeight = FontWeight.SemiBold)
            Text("Tap untuk ganti foto", color = Color.Gray, fontSize = 12.sp)
        }
    }
}