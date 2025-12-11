package com.example.medifyyyyy.domain.mapper

import com.example.medifyyyyy.domain.model.BankObat
import com.example.medifyyyyy.domain.model.BankObatDto
import java.time.Instant


object BankObatMapper {
    fun map(dto: BankObatDto, imageUrlResolver: (String?) -> String?): BankObat =
        BankObat (
            id = dto.id,
            title = dto.title,
            description = dto.description,
            imageUrl = imageUrlResolver(dto.image_url),
            createdAt = Instant.parse(dto.created_at)
        )
}