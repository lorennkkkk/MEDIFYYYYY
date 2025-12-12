package com.example.medifyyyyy.domain.model

data class AllergyLog(
    val id: String = "",
    val title: String,
    val time: String,
    val dateLabel: String,
    val severity: String,
    val description: String,
    val iconType: String = "default"
)