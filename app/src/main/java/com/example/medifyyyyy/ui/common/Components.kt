package com.example.medifyyyyy.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.medifyyyyy.domain.model.AllergyLog
import com.example.medifyyyyy.domain.model.DrugLog
import com.example.medifyyyyy.ui.theme.* 

@Composable
fun HeaderSimple() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(TealPrimary) 
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Text(
            text = "Riwayat Alergi",
            color = CardWhite, 
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun GreetingCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(vertical = 16.dp, horizontal = 20.dp)) {
            Text("Halo 👋", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TealDark)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Bagaimana kondisi alergimu hari ini?", color = Color.Gray, fontSize = 14.sp)
        }
    }
}

@Composable
fun LogItemCard(log: AllergyLog) {
    val severityColor = when (log.severity) {
        "Ringan" -> TealPrimary.copy(alpha = 0.7f)
        "Sedang" -> OrangePrimary
        "Berat" -> OrangeDark 
        else -> TealPrimary
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
            Box(modifier = Modifier.size(44.dp).background(BackgroundLight, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Eco, null, tint = TealPrimary)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(log.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TealDark)
                    Surface(color = severityColor, shape = RoundedCornerShape(6.dp)) {
                        Text(log.severity, color = CardWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                    }
                }
                Text("${log.time} • ${log.dateLabel}", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Text(log.description, fontSize = 13.sp, color = Color.Black, lineHeight = 18.sp)
            }
        }
    }
}

@Composable
fun DrugLogCard(log: DrugLog, onClick: () -> Unit = {}) {
    // Mapping status ke warna baru
    val statusColor = when (log.status) {
        "Ringan" -> TealPrimary
        "Sedang" -> OrangePrimary
        "Berat" -> OrangeDark
        else -> Color.Gray
    }

    // Menambahkan modifier clickable
    Card(
        colors = CardDefaults.cardColors(containerColor = CardWhite), 
        shape = RoundedCornerShape(16.dp), 
        elevation = CardDefaults.cardElevation(1.dp), 
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                // UPDATE: Menampilkan Gambar jika ada
                Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(BackgroundLight), contentAlignment = Alignment.Center) {
                    if (log.imageUrl != null) {
                        AsyncImage(
                            model = log.imageUrl,
                            contentDescription = "Foto Obat",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(Icons.Default.Medication, null, tint = TealPrimary)
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${log.drugName} ${log.dosage}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TealDark)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(statusColor, CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(log.status ?: "-", color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(log.time ?: "", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(log.description ?: "", fontSize = 13.sp, color = Color.Black, lineHeight = 18.sp)
                }
            }
        }
    }
}

@Composable
fun ActionButton(title: String, icon: ImageVector, bg: Color, contentColor: Color, onClick: () -> Unit) {
    // UPDATED: Menggunakan fillMaxWidth() agar tombol memenuhi ruang yang tersedia (weight)
    Card(
        colors = CardDefaults.cardColors(containerColor = bg), 
        elevation = CardDefaults.cardElevation(2.dp), 
        shape = RoundedCornerShape(16.dp), 
        modifier = Modifier.fillMaxWidth().height(90.dp).clickable { onClick() }
    ) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(
                modifier = Modifier.size(38.dp)
                    // Logika penyesuaian background icon
                    .background(if (bg == TealPrimary) CardWhite.copy(0.2f) else BackgroundLight, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = if (bg == TealPrimary) CardWhite else TealPrimary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = contentColor, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

@Composable
fun AlertCard() {
    // Menggunakan OrangeDark sebagai warna peringatan (pengganti AlertRed)
    Card(
        colors = CardDefaults.cardColors(containerColor = OrangeDark.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, OrangeDark.copy(0.3f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).background(OrangeDark.copy(0.2f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Warning, null, tint = OrangeDark, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text("Perhatian!", color = OrangeDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("Jangan lupa minum antihistamin.", color = TealDark, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun DailySummarySection() {
    Column {
        Text("Ringkasan Hari Ini", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TealDark)
        Spacer(modifier = Modifier.height(10.dp))
        Card(colors = CardDefaults.cardColors(containerColor = CardWhite), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Status Alergi", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TealDark)
                    Surface(color = OrangePrimary, shape = RoundedCornerShape(50)) {
                        Text("Sedang", color = CardWhite, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    StatItem("1", "Reaksi Hari Ini", Icons.Default.Warning, OrangePrimary)
                    StatItem("7", "Log Minggu Ini", Icons.Default.ShowChart, TealPrimary)
                }
            }
        }
    }
}

@Composable
fun StatItem(count: String, label: String, icon: ImageVector, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(40.dp).background(color.copy(0.2f), RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) { Icon(icon, null, tint = color) }
        Spacer(modifier = Modifier.height(6.dp))
        Text(count, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TealDark)
        Text(label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun FilterChipItem(text: String, isSelected: Boolean, color: Color) {
    Surface(color = if (isSelected) color else color.copy(alpha = 0.3f), shape = RoundedCornerShape(50), modifier = Modifier.height(32.dp).clickable { }) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(text, color = if (isSelected) CardWhite else TealDark, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}
