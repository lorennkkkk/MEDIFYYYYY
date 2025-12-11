package com.example.medifyyyyy.domain.mapper

import com.example.medifyyyyy.domain.model.RekomendasiObat
import com.example.medifyyyyy.domain.model.RekomendasiObatDto
import java.time.Instant

object RekomendasiObatMapper {

    fun map(dto: RekomendasiObatDto, imageUrlResolver: (String?) -> String?): RekomendasiObat =
        RekomendasiObat (
            id = dto.id,
            title = dto.title,
            description = dto.description,
            imageUrl = imageUrlResolver(dto.image_url),
            createdAt = Instant.parse(dto.created_at)
        )
}