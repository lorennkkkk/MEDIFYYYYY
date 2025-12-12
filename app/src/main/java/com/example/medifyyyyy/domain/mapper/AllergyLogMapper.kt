package com.example.medifyyyyy.domain.mapper

import com.example.medifyyyyy.domain.model.AllergyLog
import com.example.medifyyyyy.domain.model.AllergyLogDto

object AllergyLogMapper {
    fun map(dto: AllergyLogDto): AllergyLog {
        return AllergyLog(
            id = dto.id ?: "",
            title = dto.title,
            time = dto.time,
            dateLabel = dto.dateLabel,
            severity = dto.severity,
            description = dto.description,
            iconType = dto.iconType ?: "default"
        )
    }

    fun mapToDto(domain: AllergyLog): AllergyLogDto {
        return AllergyLogDto(
            title = domain.title,
            time = domain.time,
            dateLabel = domain.dateLabel,
            severity = domain.severity,
            description = domain.description,
            iconType = domain.iconType
        )
    }
}