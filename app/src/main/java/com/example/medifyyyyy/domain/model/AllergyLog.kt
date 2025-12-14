package com.example.medifyyyyy.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// digunakan buat ui
data class AllergyLog(
    val id: String = "",
    val title: String,
    val time: String,
    val dateLabel: String,
    val severity: String,
    val description: String,
    val iconType: String = "default"
)

// digunakan buat komunikasi dengan supabase
@Serializable
data class AllergyLogDto(
    val id: String? = null,
    val title: String,
    val time: String,
    @SerialName("date_label") val dateLabel: String, // Contoh: mapping snake_case ke camelCase
    val severity: String,
    val description: String,
    @SerialName("icon_type") val iconType: String? = "default"
)