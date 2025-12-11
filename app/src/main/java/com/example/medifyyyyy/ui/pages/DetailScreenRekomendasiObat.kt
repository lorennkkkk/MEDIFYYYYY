package com.example.medifyyyyy.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar


import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.medifyyyyy.domain.model.RekomendasiObat
import com.example.medifyyyyy.ui.viewmodel.RekomendasiObatViewModel

import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreenRekomendasiObat(
    id: String,
    viewModel: RekomendasiObatViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBack: () -> Unit
) {
    var RekomendasiObat by remember { mutableStateOf<RekomendasiObat?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(id) {
        scope.launch {
            RekomendasiObat = viewModel.getObat(id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Detail") }, navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            })
        }
    ) { padding ->
        if (RekomendasiObat == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                Modifier
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(RekomendasiObat!!.title, style = MaterialTheme.typography.headlineSmall)
                Text(RekomendasiObat!!.description ?: "-", style = MaterialTheme.typography.bodyLarge)
                RekomendasiObat!!.imageUrl?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = "Rekomendasi Obat Image",
                        modifier = Modifier.fillMaxWidth().height(240.dp)
                    )
                }
                val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")
                    .withZone(ZoneId.systemDefault())
                Text("Created: ${formatter.format(RekomendasiObat!!.createdAt)}",
                    style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}