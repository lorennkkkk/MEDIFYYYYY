package com.example.medifyyyyy.ui.pages

import java.util.Date

data class SertifikatVaksin(
    val id: Int,
    val namaLengkap: String,
    val jenisVaksin: String,
    val dosis: String,
    val tanggalVaksinasi: Date,
    val tempatVaksinasi: String,
    val imageUrl: String // URL Publik dari gambar sertifikat
)

// UI Result Helper (Opsional, tapi bagus untuk state management)
sealed class UIResult<out T> {
    data object Idle : UIResult<Nothing>()
    data object Loading : UIResult<Nothing>()
    data class Success<T>(val data: T) : UIResult<T>()
    data class Error(val exception: Throwable) : UIResult<Nothing>()
}