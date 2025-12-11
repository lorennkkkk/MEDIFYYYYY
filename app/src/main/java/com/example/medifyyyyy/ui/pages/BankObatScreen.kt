package com.example.medifyyyyy.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete


import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medifyyyyy.domain.model.BankObat
import com.example.medifyyyyy.ui.common.UiResult
import com.example.medifyyyyy.ui.viewmodel.BankObatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    BankObatViewModel: BankObatViewModel = viewModel(),
    onAdd: () -> Unit,
    onDetail: (String) -> Unit,
    onBack: () -> Unit
) {
    val BankObatState by BankObatViewModel.BankObat.collectAsState()

    LaunchedEffect(Unit) {
        BankObatViewModel.loadBankObat()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bank Obat Pribadi", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) },navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.secondary)
                    }
                })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd, containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        when (BankObatState) {
            is UiResult.Loading -> Box(Modifier.padding(padding)) { CircularProgressIndicator() }
            is UiResult.Error -> Text(
                (BankObatState as UiResult.Error).message,
                modifier = Modifier.padding(16.dp)
            )
            is UiResult.Success -> BankObatList(
                list = (BankObatState as UiResult.Success<List<BankObat>>).data,
                onDelete = { BankObatViewModel.deleteObat(it) },
                onDetail = onDetail,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun BankObatList(
    list: List<BankObat>,
    onDelete: (String) -> Unit,
    onDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(list) { BankObat ->
            Card(onClick = { onDetail(BankObat.id) }, colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
            )) {
                Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(Modifier.weight(1f)) {
                        Text(BankObat.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        BankObat.description?.let { Text(it, maxLines = 2) }
                    }
                    IconButton(onClick = { onDelete(BankObat.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}