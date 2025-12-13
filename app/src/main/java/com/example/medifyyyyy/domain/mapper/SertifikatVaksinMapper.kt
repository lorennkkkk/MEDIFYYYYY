package com.example.medifyyyyy.domain.mapper

import com.example.medifyyyyy.domain.model.SertifikatVaksin
import com.example.medifyyyyy.domain.model.SertifikatVaksinDto

object SertifikatVaksinMapper {

    fun map(
        dto: SertifikatVaksinDto,
        resolveImageUrl: (String?) -> String?
    ): SertifikatVaksin {

        return SertifikatVaksin(
            id = dto.id,
            namaLengkap = dto.nama_lengkap,
            jenisVaksin = dto.jenis_vaksin,
            dosis = dto.dosis,
            tanggalVaksinasi = dto.tanggal_vaksinasi,
            tempatVaksinasi = dto.tempat_vaksinasi,
            imageUrl = resolveImageUrl(dto.image_url)
        )
    }
}
