package com.example.medifyyyyy.data.repository

import android.util.Log
import com.example.medifyyyyy.data.remote.SupabaseHolder
import com.example.medifyyyyy.domain.mapper.RekomendasiObatMapper
import com.example.medifyyyyy.domain.model.RekomendasiObat
import com.example.medifyyyyy.domain.model.RekomendasiObatDto
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class RekomendasiObatRepository {

    private val postgrest get() = SupabaseHolder.client.postgrest
    private val storage get() = SupabaseHolder.client.storage.from("Rekomendasiobat-images")

    private fun resolveImageUrl(path: String?): String? {
        if (path == null) return null
        // Bila bucket private gunakan signed URL:
        return storage.publicUrl(path)
    }

    suspend fun fetchRekomendasiObat(): List<RekomendasiObat> {
        val userId = SupabaseHolder.session()?.user?.id ?: return emptyList()
        val response = postgrest["Rekomendasi_obat"].select {
            filter {
                eq("user_id", userId)
            }
            order("created_at", Order.DESCENDING)
        }
        val list = response.decodeList<RekomendasiObatDto>()
        return list.map { RekomendasiObatMapper.map(it, ::resolveImageUrl) }
    }

    suspend fun addObat(
        title: String,
        description: String?,
        imageFile: File?
    ): RekomendasiObat {

        val userId = SupabaseHolder.session()?.user?.id
            ?: throw IllegalStateException("User not logged in")


        var imagePath: String? = null
        if (imageFile != null) {
            imagePath = uploadImage(imageFile, userId)
        }

        val insert = postgrest["Rekomendasi_obat"].insert(
            mapOf(
                "user_id" to userId,
                "title" to title,
                "description" to description,
                "image_url" to imagePath
            )
        ) { select() }

        val dto = insert.decodeSingle<RekomendasiObatDto>()
        return RekomendasiObatMapper.map(dto, ::resolveImageUrl)
    }

    private suspend fun uploadImage(file: File, uid: String): String = withContext(Dispatchers.IO) {
        // Simpan di folder milik user agar lolos policy: name LIKE auth.uid() || '/%'
        val objectName = "$uid/${UUID.randomUUID()}_${file.name}"
        storage.upload(objectName, file.readBytes())
        objectName // ini yang disimpan ke kolom image_path
    }

    suspend fun deleteObat(id: String) {
        postgrest["Rekomendasi_obat"].delete {
            filter { eq("id", id) }
        }
    }

    suspend fun getObat(id: String): RekomendasiObat? {
        val response = postgrest["Rekomendasi_obat"].select {
            filter { eq("id", id) }
            // JANGAN pakai single() di sini
        }
        Log.d("GET_REKOMENDASIOBAT", "raw=" + (response.data ?: "null"))
        val dto = response.decodeList<RekomendasiObatDto>().firstOrNull() ?: return null
        return RekomendasiObatMapper.map(dto, ::resolveImageUrl)
    }
}