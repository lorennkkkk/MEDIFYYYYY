package com.example.medifyyyyy.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SertifikatVaksinDto(
    val id: Int,
    val user_id: String,
    val nama_lengkap: String,
    val jenis_vaksin: String,
    val dosis: String,
    // Supabase timestamp biasanya String ISO 8601
    val tanggal_vaksinasi: String,
    val tempat_vaksinasi: String,
    val image_path: String? // Path gambar di storage
)