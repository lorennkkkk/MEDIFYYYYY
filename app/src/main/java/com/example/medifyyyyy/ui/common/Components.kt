package com.example.medifyyyyy.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.medifyyyyy.domain.model.AllergyLog
import com.example.medifyyyyy.domain.model.DrugLog
import com.example.medifyyyyy.ui.theme.* 

// --- BASE COMPONENTS ---

@Composable
private fun BaseLogCard(
    title: String,
    subtitle: String,
    description: String,
    iconContent: @Composable () -> Unit,
    statusContent: @Composable () -> Unit,
    onClick: (() -> Unit)? = null
) {
    Card(
        // Menggunakan warna surface agar adaptif (Putih di Light, Gelap di Dark)
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp), 
        modifier = Modifier.fillMaxWidth().then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) { iconContent() }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                    statusContent()
                }
                Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 18.sp)
            }
        }
    }
}

// --- KOMPONEN HomeDrugAllergy ---

@Composable
fun HomeHeader(dynamicPrimaryColor: Color, dynamicOnPrimaryColor: Color, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(dynamicPrimaryColor)
            .padding(horizontal = 24.dp, vertical = 24.dp) 
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = dynamicOnPrimaryColor
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Daftar Alergi Obat", color = dynamicOnPrimaryColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Pantauan Kesehatan", color = dynamicOnPrimaryColor.copy(alpha = 0.8f), fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun SearchBarSection(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Cari nama obat...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
        leadingIcon = { Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
fun ActionButtonsRow(
    onAddClick: () -> Unit,
    onHistoryClick: () -> Unit,
    dynamicPrimaryColor: Color,
    dynamicOnPrimaryColor: Color,
    btnHistoryBg: Color,
    btnHistoryContent: Color,
    sectionTitleColor: Color
) {
    Column {
        Text(
            text = "Aksi Cepat",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = sectionTitleColor
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                ActionButton(
                    title = "Tambah Log",
                    icon = Icons.Default.Add,
                    bg = dynamicPrimaryColor,
                    contentColor = dynamicOnPrimaryColor,
                    onClick = onAddClick
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                ActionButton(
                    title = "Riwayat",
                    icon = Icons.Outlined.Medication,
                    bg = btnHistoryBg,
                    contentColor = btnHistoryContent,
                    onClick = onHistoryClick
                )
            }
        }
    }
}

@Composable
fun RecentLogsSection(
    logs: List<DrugLog>,
    isLoading: Boolean,
    dynamicPrimaryColor: Color,
    sectionTitleColor: Color,
    isDark: Boolean,
    onLogClick: (DrugLog) -> Unit
) {
    // Bungkus dalam Column agar aman di dalam LazyColumn item
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Log Terbaru",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = sectionTitleColor 
            )
            if (logs.isNotEmpty()) {
                Text(
                    text = "Lihat Semua",
                    fontSize = 13.sp,
                    color = dynamicPrimaryColor, 
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = dynamicPrimaryColor) 
            }
        } else if (logs.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = "Belum ada riwayat tercatat.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = FontStyle.Italic,
                    fontSize = 13.sp
                )
            }
        } else {
            // Tampilkan beberapa log terbaru saja (misal: 3 log)
            logs.take(3).forEach { log ->
                DrugLogCard(log, onClick = { onLogClick(log) })
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

// --- GENERAL COMPONENTS ---

@Composable
fun LogItemCard(log: AllergyLog) {
    val severityColor = when (log.severity) {
        "Ringan" -> TealPrimary.copy(alpha = 0.7f)
        "Sedang" -> OrangePrimary
        "Berat" -> OrangeDark 
        else -> TealPrimary
    }
    BaseLogCard(
        title = log.title,
        subtitle = "${log.time} • ${log.dateLabel}",
        description = log.description,
        iconContent = { Icon(Icons.Default.Eco, null, tint = TealPrimary) },
        statusContent = {
            Surface(color = severityColor, shape = RoundedCornerShape(6.dp)) {
                Text(log.severity, color = CardWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
            }
        }
    )
}

@Composable
fun DrugLogCard(log: DrugLog, onClick: () -> Unit = {}) {
    val statusColor = when (log.status) {
        "Ringan" -> TealPrimary
        "Sedang" -> OrangePrimary
        "Berat" -> OrangeDark
        else -> Color.Gray
    }
    BaseLogCard(
        title = "${log.drugName} ${log.dosage}",
        subtitle = log.time ?: "",
        description = log.description ?: "",
        onClick = onClick,
        iconContent = {
            if (log.imageUrl != null) {
                AsyncImage(
                    model = log.imageUrl, 
                    contentDescription = "Foto", 
                    contentScale = ContentScale.Crop, 
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(Icons.Default.Medication, null, tint = MaterialTheme.colorScheme.primary)
            }
        },
        statusContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).background(statusColor, CircleShape))
                Spacer(modifier = Modifier.width(4.dp))
                Text(log.status ?: "-", color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun ActionButton(
    title: String, 
    icon: ImageVector, 
    bg: Color, 
    contentColor: Color, 
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = bg), 
        elevation = CardDefaults.cardElevation(2.dp), 
        shape = RoundedCornerShape(16.dp), 
        modifier = modifier.fillMaxWidth().height(90.dp).clickable { onClick() }
    ) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(
                modifier = Modifier.size(38.dp)
                    // UPDATE: Ganti BackgroundLight dengan warna primary dengan alpha
                    .background(if (bg == TealPrimary || bg == MaterialTheme.colorScheme.primary) CardWhite.copy(0.2f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = if (bg == TealPrimary || bg == MaterialTheme.colorScheme.primary) CardWhite else MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = contentColor, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

@Composable
fun GreetingCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(vertical = 16.dp, horizontal = 20.dp)) {
            Text("Halo 👋", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Bagaimana kondisi alergimu hari ini?", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        }
    }
}

@Composable
fun AlertCard() {
    Card(
        // Menggunakan warna Error Container dari tema agar adaptif
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.background.copy(alpha=0.2f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text("Perhatian!", color = MaterialTheme.colorScheme.onErrorContainer, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("Jangan lupa minum antihistamin.", color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 12.sp)
            }
        }
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
