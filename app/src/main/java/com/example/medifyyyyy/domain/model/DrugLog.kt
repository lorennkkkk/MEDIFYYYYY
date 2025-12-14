package com.example.medifyyyyy.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DrugLog(
    val id: String? = null,
    
    @SerialName("drug_name")
    val drugName: String,
    
    val dosage: String? = "",
    val time: String? = "",
    val status: String? = "",
    val description: String? = "",
    
    @SerialName("image_count")
    val imageCount: Int = 0,
    
    @SerialName("has_image")
    val hasImage: Boolean = false
)