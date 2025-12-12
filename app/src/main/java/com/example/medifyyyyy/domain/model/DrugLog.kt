package com.example.medifyyyyy.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DrugLog(
    val id: String? = null,
    val drugName: String,
    val dosage: String? = "",
    val time: String? = "",
    val status: String? = "",
    val description: String? = "",
    val imageCount: Int = 0,
    val hasImage: Boolean = false
)