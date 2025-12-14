package com.example.medifyyyyy.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.medifyyyyy.ui.common.*
import com.example.medifyyyyy.ui.nav.Screen // Import Screen untuk navigasi
import com.example.medifyyyyy.ui.theme.*
import com.example.medifyyyyy.ui.viewmodel.LogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SideEffectListScreen(navController: NavController, viewModel: LogViewModel) {
    val drugLogs by viewModel.drugLogs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    // --- Konfigurasi Dark Mode (Agar sama dengan HomeDrugAllergy) ---
    val isDark = isSystemInDarkTheme()
    val dynamicPrimaryColor = if (isDark) MaterialTheme.colorScheme.primary else Color(0xFF00897B)
    val dynamicOnPrimaryColor = if (isDark) MaterialTheme.colorScheme.onPrimary else Color.White

    // State untuk Search
    var searchQuery by remember { mutableStateOf("") }

    // Gunakan DisposableEffect untuk mendengarkan siklus hidup (ON_RESUME)
    // Ini memastikan data di-refresh setiap kali layar ini tampil kembali
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.fetchData()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background, 
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            text = "Catatan Efek Samping", 
                            color = MaterialTheme.colorScheme.primary, 
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "${drugLogs.size} obat dicatat",
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 14.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Back", 
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate("add_log") },
                containerColor = dynamicPrimaryColor, // Sesuaikan warna FAB
                contentColor = dynamicOnPrimaryColor,     
                shape = RoundedCornerShape(50),
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tambah Log")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            // --- Bagian Pencarian & Filter ---
            Column(modifier = Modifier.padding(20.dp)) {
                // Menggunakan SearchBarSection dari Components.kt yang sudah mendukung tema
                SearchBarSection(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it }
                )
                
                Spacer(modifier = Modifier.height(16.dp))

            }

            // Filter List berdasarkan Search Query
            val filteredLogs = drugLogs.filter { 
                it.drugName.contains(searchQuery, ignoreCase = true) 
            }

            // --- List Data ---
            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (isLoading) {
                    item {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = dynamicPrimaryColor) 
                        }
                    }
                } else if (filteredLogs.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(top = 50.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Tidak ada riwayat yang cocok", color = Color.Gray, fontStyle = FontStyle.Italic)
                        }
                    }
                } else {
                    items(filteredLogs) { log ->
                        // Menambahkan aksi klik untuk melihat detail
                        DrugLogCard(log, onClick = {
                            navController.navigate(Screen.DetailLog.build(log.id.toString()))
                        })
                    }
                }
            }
        }
    }
}
