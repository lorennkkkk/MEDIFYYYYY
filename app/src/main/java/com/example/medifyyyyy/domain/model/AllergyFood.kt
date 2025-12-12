package com.example.medifyyyyy.domain.model

import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class AllergyFoodDto(
    val id: String? = null,
    val name: String,
    val description: String,
    val image_url: String?,
    val user_id: String?,
    val created_at: String
)

data class AllergyFood(
    val id: String = "",
    val name: String,
    val description: String,
    val imageUrl: String?,
    val userId: String,
    val createdAt: Instant
)