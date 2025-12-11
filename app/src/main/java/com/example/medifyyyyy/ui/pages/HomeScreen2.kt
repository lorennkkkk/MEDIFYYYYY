package com.example.medifyyyyy.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Logout


import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medifyyyyy.domain.model.RekomendasiObat
import com.example.medifyyyyy.ui.common.UiResult
import com.example.medifyyyyy.ui.viewmodel.AuthViewModel
import com.example.medifyyyyy.ui.viewmodel.RekomendasiObatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen2(
    RekomendasiObatViewModel: RekomendasiObatViewModel = viewModel(),
    onAdd: () -> Unit,
    onDetail: (String) -> Unit,
    onLogout: () -> Unit
) {
    val RekomendasiObatState by RekomendasiObatViewModel.RekomendasiObat.collectAsState()

    LaunchedEffect(Unit) {
        RekomendasiObatViewModel.loadRekomendasiObat()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rekomendasi Obat") },
                actions = {
                    IconButton(onClick = {
                        onLogout()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd, containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        when (RekomendasiObatState) {
            is UiResult.Loading -> Box(Modifier.padding(padding)) { CircularProgressIndicator() }
            is UiResult.Error -> Text(
                (RekomendasiObatState as UiResult.Error).message,
                modifier = Modifier.padding(16.dp)
            )
            is UiResult.Success -> RekomendasiObatList(
                list = (RekomendasiObatState as UiResult.Success<List<RekomendasiObat>>).data,
                onDelete = { RekomendasiObatViewModel.deleteObat(it) },
                onDetail = onDetail,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun RekomendasiObatList(
    list: List<RekomendasiObat>,
    onDelete: (String) -> Unit,
    onDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(list) { RekomendasiObat ->
            ElevatedCard(onClick = { onDetail(RekomendasiObat.id) }) {
                Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(Modifier.weight(1f)) {
                        Text(RekomendasiObat.title, style = MaterialTheme.typography.titleMedium)
                        RekomendasiObat.description?.let { Text(it, maxLines = 2) }
                    }
                    IconButton(onClick = { onDelete(RekomendasiObat.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
    }
}