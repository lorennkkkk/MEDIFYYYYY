package com.example.medifyyyyy.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Todo(
    val id: Long? = null,
    val user_id: String,
    val title: String,
    val time: String
)