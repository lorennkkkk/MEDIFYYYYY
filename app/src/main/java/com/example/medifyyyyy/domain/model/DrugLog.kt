package com.example.medifyyyyy.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class DrugLog(
    val id: Long = 0,
    
    @SerialName("drug_name")
    val drugName: String,
    
    val dosage: String? = "",
    val time: String? = "",
    val status: String? = "",
    val description: String? = "",
    
    @SerialName("image_count")
    val imageCount: Int = 0,
    
    @SerialName("has_image")
    val hasImage: Boolean = false,

    // Menyimpan lokasi file di Storage (misal: "uid/uuid.jpg")
    @SerialName("image_path")
    val imagePath: String? = null,

    // URL lengkap untuk ditampilkan di layar (tidak disimpan di DB)
    @Transient
    var imageUrl: String? = null
)