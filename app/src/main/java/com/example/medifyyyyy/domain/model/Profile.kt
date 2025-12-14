package com.example.medifyyyyy.domain.model


@kotlinx.serialization.Serializable
data class Profile(
    val id: String,
    val first_name: String,
    val last_name: String,
    val username: String,
    val email: String,
    val profile_image_url: String? = null
)