package com.example.medifyyyyy.ui.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material.icons.filled.Warning


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BerandaFitur(
    onNavigateBankObat: () -> Unit,
    onNavigateFoodAllergy: () -> Unit,
    onNavigateSertifVaksin: () -> Unit,
    onLogout: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Beranda Fitur", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) },
                actions = { IconButton(onClick = { onLogout() }) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = MaterialTheme.colorScheme.secondary)
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 3.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /* TODO: Navigate Beranda */ }) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Beranda",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )}
                    IconButton(onClick = { onNavigateSertifVaksin() } ) {
                    Icon(
                        imageVector = Icons.Default.Vaccines,
                        contentDescription = "Vaksin",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )}
                    IconButton(onClick = { /* TODO: Navigate Log Alergi */ }) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Alergi",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )}
                    IconButton(onClick = { /* TODO: Navigate Todo */ }) {
                    Icon(
                        imageVector = Icons.Default.Checklist,
                        contentDescription = "To-Do",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )}
                    IconButton(onClick = { /* TODO: Navigate Profil */ }) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profil",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )}
                }
            }
        }

    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            FeatureCard(
                title = "Bank Obat Pribadi",
                onClick = onNavigateBankObat
            )
            FeatureCard(
                title = "Daftar Alergi Makanan",
                onClick = onNavigateFoodAllergy
            )
        }
    }
}

@Composable
fun FeatureCard(
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
