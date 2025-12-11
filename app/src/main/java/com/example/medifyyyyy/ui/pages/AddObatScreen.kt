package com.example.medifyyyyy.ui.pages

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medifyyyyy.ui.common.UiResult
import com.example.medifyyyyy.ui.viewmodel.BankObatViewModel
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBankObatScreen(
    viewModel: BankObatViewModel = viewModel(),
    onDone: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var imageFile by remember { mutableStateOf<File?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val addingState by viewModel.adding.collectAsState()
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Konversi URI ke File sementara

            // Amankan input stream pertama (untuk dikopi ke File)
            val tempInputStream = context.contentResolver.openInputStream(uri) ?: return@let

            //val input = context.contentResolver.openInputStream(uri)
            val temp = File(context.cacheDir, "gallery_${System.currentTimeMillis()}.jpg")
            tempInputStream?.use { ins ->
                temp.outputStream().use { outs ->
                    ins.copyTo(outs)
                }
            }

            imageFile = temp


            // *Penting*: Membuka stream baru karena stream sebelumnya sudah ditutup.
            val drawableStream = context.contentResolver.openInputStream(uri)
            val drawable = android.graphics.drawable.Drawable.createFromStream(
                drawableStream,
                uri.toString()
            )
            bitmap = drawable?.toBitmap() // Simpan Bitmap untuk ditampilkan

            // Pastikan stream kedua ditutup secara eksplisit setelah digunakan.
            drawableStream?.close()
        }
    }

//    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview())
//    { bmp-> bmp?.let {
//        val temp = File(context.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
//        temp.outputStream().use { os ->
//            it.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, os)
//        }
//        imageFile = temp
//        bitmap = it
//    }
//    }

    LaunchedEffect(addingState) {
        if (addingState is UiResult.Success) {
            onDone()
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Tambah Obat Pribadi", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) }) }) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(title, { title = it }, label = { Text("Judul") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(desc, { desc = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { galleryLauncher.launch("image/*") }) {
                    Icon(Icons.Default.Photo, contentDescription = "Galeri", tint = MaterialTheme.colorScheme.secondary)
                    Spacer(Modifier.width(8.dp))
                    Text("Galeri", color = MaterialTheme.colorScheme.primary)
                }
//                OutlinedButton(onClick = { cameraLauncher.launch(null) }) {
//                    Icon(Icons.Default.CameraAlt, contentDescription = "Kamera")
//                    Spacer(Modifier.width(8.dp))
//                    Text("Kamera")
//                }
            }
            bitmap?.let {
                Image(bitmap = it.asImageBitmap(), contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ){
            Button(
                onClick = { viewModel.addObat(title, desc.ifBlank { null }, imageFile) },
                enabled = title.isNotBlank() && addingState !is UiResult.Loading
            ) {
                Text(if (addingState is UiResult.Loading) "Menyimpan..." else "Simpan")
            }
            if (addingState is UiResult.Error) {
                Text((addingState as UiResult.Error).message, color = MaterialTheme.colorScheme.error)
            }
            }
        }
    }
}