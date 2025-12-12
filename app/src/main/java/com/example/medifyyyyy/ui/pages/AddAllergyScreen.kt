package com.example.medifyyyyy.ui.pages

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.medifyyyyy.ui.common.UiResult
import com.example.medifyyyyy.ui.viewmodel.AllergyFoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAllergyScreen(
    viewModel: AllergyFoodViewModel,
    onBack: () -> Unit // Navigasi untuk kembali
) {
    val context = LocalContext.current

    // --- STATE (Variabel Penyimpan Data) ---
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // State untuk Gambar (URI buat preview, ByteArray buat upload)
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedImageBytes by remember { mutableStateOf<ByteArray?>(null) }

    // --- LAUNCHER GALERI ---
    // Ini alat untuk membuka Galeri HP
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            // Magic: Ubah Gambar jadi "Tepung" (ByteArray) biar bisa dikirim ke Supabase
            val inputStream = context.contentResolver.openInputStream(uri)
            selectedImageBytes = inputStream?.readBytes()
        }
    }

    // --- CEK STATUS UPLOAD (Loading/Sukses/Gagal) ---
    val uploadState by viewModel.adding.collectAsState()

    // Efek Samping: Kalau sukses, langsung tutup halaman ini (kembali)
    LaunchedEffect(uploadState) {
        if (uploadState is UiResult.Success) {
            Toast.makeText(context, "Berhasil disimpan!", Toast.LENGTH_SHORT).show()
            viewModel.resetAddState() // Reset biar gak error pas dibuka lagi
            onBack()
        } else if (uploadState is UiResult.Error) {
            Toast.makeText(context, (uploadState as UiResult.Error).message, Toast.LENGTH_LONG).show()
            viewModel.resetAddState()
        }
    }

    // --- UI TAMPILAN ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Info Makanan", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF00897B)) // Warna Teal mirip desain
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()) // Biar bisa discroll kalau HP kecil
        ) {

            // 1. Input Nama
            Text("Nama Makanan", color = Color(0xFF00897B), fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Contoh: Udang Balado") },
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Input Deskripsi
            Text("Deskripsi", color = Color(0xFF00897B), fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp), // Lebih tinggi buat nulis panjang
                placeholder = { Text("Tulis deskripsi kandungan yang memicu alergi...") },
                shape = RoundedCornerShape(8.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Upload Foto
            Text("Upload Foto", color = Color(0xFF00897B), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Tombol Kamera (Sementara pakai Toast dulu, karena setup Kamera agak rumit)
                Button(
                    onClick = { Toast.makeText(context, "Fitur Kamera menyusul ya!", Toast.LENGTH_SHORT).show() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Kamera")
                }

                // Tombol Galeri
                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") }, // Buka Galeri
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = Color(0xFF00897B))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Galeri", color = Color(0xFF00897B))
                }
            }

            // Preview Foto (Kalau user sudah pilih foto)
            if (selectedImageUri != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(selectedImageUri),
                        contentDescription = "Preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 4. Tombol Aksi (Batal & Simpan)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00897B))
                ) {
                    Text("Batal")
                }

                Button(
                    onClick = {
                        // PANGGIL VIEWMODEL DISINI
                        viewModel.addAllergy(name, description, selectedImageBytes)
                    },
                    enabled = name.isNotEmpty() && description.isNotEmpty() && uploadState !is UiResult.Loading,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (uploadState is UiResult.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Simpan Log")
                    }
                }
            }
        }
    }
}