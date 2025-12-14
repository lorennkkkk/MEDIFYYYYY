package com.example.medifyyyyy.ui.pages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date
@Serializable
data class SertifikatVaksin(
    val id: String,
    @SerialName("nama_lengkap")
    val namaLengkap: String,
    @SerialName("jenis_vaksin")
    val jenisVaksin: String,
    val dosis: String,
    @SerialName("tanggal_vaksinasi")
    val tanggalVaksinasi: String, // ⚠️ lihat poin 3
    @SerialName("tempat_vaksinasi")
    val tempatVaksinasi: String,
    @SerialName("image_url")
    val imageUrl: String
)

// UI Result Helper (Opsional, tapi bagus untuk state management)
sealed class UIResult<out T> {
    data object Idle : UIResult<Nothing>()
    data object Loading : UIResult<Nothing>()
    data class Success<T>(val data: T) : UIResult<T>()
    data class Error(val exception: Throwable) : UIResult<Nothing>()
}