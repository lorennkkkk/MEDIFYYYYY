package com.example.medifyyyyy.domain.mapper

import com.example.medifyyyyy.domain.model.SertifikatVaksinDto
import com.example.medifyyyyy.ui.pages.SertifikatVaksin
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SertifikatVaksinMapper {

    private val supabaseDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())

    /**
     * Mengkonversi DTO dari Supabase menjadi Domain Model.
     * @param resolveImageUrl Fungsi untuk mendapatkan URL publik dari path storage.
     */
    fun map(dto: SertifikatVaksinDto, resolveImageUrl: (String?) -> String?): SertifikatVaksin {
        val imageUrl = resolveImageUrl(dto.image_url)
        val tanggal: Date = try {
            // Asumsi format tanggal di Supabase cocok dengan SimpleDateFormat
            supabaseDateFormat.parse(dto.tanggal_vaksinasi) ?: Date()
        } catch (e: Exception) {
            // Log atau handle error parsing
            Date()
        }

        return SertifikatVaksin(
            id = dto.id,
            namaLengkap = dto.nama_lengkap,
            jenisVaksin = dto.jenis_vaksin,
            dosis = dto.dosis,
            tanggalVaksinasi = tanggal,
            tempatVaksinasi = dto.tempat_vaksinasi,
            imageUrl = imageUrl ?: ""
        )
    }
}