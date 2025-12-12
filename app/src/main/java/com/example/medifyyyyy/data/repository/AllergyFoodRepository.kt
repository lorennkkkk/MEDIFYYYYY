package com.example.medifyyyyy.data.repositories

import android.util.Log
import com.example.medifyyyyy.data.remote.SupabaseHolder
import com.example.medifyyyyy.domain.mapper.AllergyFoodMapper
import com.example.medifyyyyy.domain.model.AllergyFood
import com.example.medifyyyyy.domain.model.AllergyFoodDto
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class AllergyFoodRepository {

    private val postgrest get() = SupabaseHolder.client.postgrest
    private val storage get() = SupabaseHolder.client.storage.from("allergy-images")

    private fun resolveImageUrl(path: String?): String? {
        if (path == null) return null
        return storage.publicUrl(path)
    }

    suspend fun getAllergyFoods(): List<AllergyFood> {
        val userId = SupabaseHolder.session()?.user?.id ?: return emptyList()

        val response = postgrest["allergy_foods"].select {
            filter {
                eq("user_id", userId)
            }
            order("created_at", Order.DESCENDING)
        }

        val list = response.decodeList<AllergyFoodDto>()

        return list.map { AllergyFoodMapper.map(it, ::resolveImageUrl) }
    }

    suspend fun addAllergyFood(
        name: String,
        description: String,
        imageBytes: ByteArray?
    ): AllergyFood {

        val userId = SupabaseHolder.session()?.user?.id
            ?: throw IllegalStateException("User not logged in")

        var imagePath: String? = null
        if (imageBytes != null) {
            imagePath = uploadImage(imageBytes, userId)
        }

        val insert = postgrest["allergy_foods"].insert(
            mapOf(
                "user_id" to userId,
                "name" to name,
                "description" to description,
                "image_url" to imagePath
            )
        ) { select() }

        val dto = insert.decodeSingle<AllergyFoodDto>()
        return AllergyFoodMapper.map(dto, ::resolveImageUrl)
    }

    private suspend fun uploadImage(bytes: ByteArray, uid: String): String = withContext(Dispatchers.IO) {
        val objectName = "$uid/${UUID.randomUUID()}.jpg"

        storage.upload(objectName, bytes)

        objectName
    }

    suspend fun deleteAllergyFood(id: String) {
        postgrest["allergy_foods"].delete {
            filter { eq("id", id) }
        }
    }

    suspend fun getAllergyFood(id: String): AllergyFood? {
        val response = postgrest["allergy_foods"].select {
            filter { eq("id", id) }
        }

        Log.d("GET_ALLERGY", "raw=" + (response.data ?: "null"))

        val dto = response.decodeList<AllergyFoodDto>().firstOrNull() ?: return null

        return AllergyFoodMapper.map(dto, ::resolveImageUrl)
    }
}