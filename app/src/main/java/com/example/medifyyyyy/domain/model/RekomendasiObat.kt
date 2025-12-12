package com.example.medifyyyyy.domain.model

import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class RekomendasiObatDto(
    val id: String,
    val user_id: String,
    val title: String,
    val description: String? = null,
    val image_url: String? = null,
    val created_at: String
)

data class RekomendasiObat(
    val id: String,
    val title: String,
    val description: String?,
    val imageUrl: String?, // URL siap pakai di UI
    val createdAt: Instant
)