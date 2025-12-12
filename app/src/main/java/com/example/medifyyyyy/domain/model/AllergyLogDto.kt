package com.example.medifyyyyy.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AllergyLogDto(
    val id: String? = null,
    val title: String,
    val time: String,
    val dateLabel: String,
    val severity: String,
    val description: String,
    val iconType: String? = "default"
)