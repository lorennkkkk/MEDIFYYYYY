package com.example.medifyyyyy.domain.mapper

import com.example.medifyyyyy.domain.model.AllergyFood
import com.example.medifyyyyy.domain.model.AllergyFoodDto
import java.time.Instant

object AllergyFoodMapper {
    fun map(dto: AllergyFoodDto, imageUrlResolver: (String?) -> String?): AllergyFood {
        return AllergyFood(
            id = dto.id ?: "",
            name = dto.name,
            description = dto.description,
            imageUrl = imageUrlResolver(dto.image_url),
            userId = dto.user_id ?: "",
            createdAt = Instant.parse(dto.created_at)
        )
    }
}