package com.example.medifyyyyy.data.repositories

import android.util.Log
import com.example.medifyyyyy.data.remote.SupabaseHolder
import com.example.medifyyyyy.domain.mapper.BankObatMapper
import com.example.medifyyyyy.domain.model.BankObat
import com.example.medifyyyyy.domain.model.BankObatDto
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class BankObatRepository {

    private val postgrest get() = SupabaseHolder.client.postgrest
    private val storage get() = SupabaseHolder.client.storage.from("bankobat-images")

    private fun resolveImageUrl(path: String?): String? {
        if (path == null) return null
        // Bila bucket private gunakan signed URL:
        return storage.publicUrl(path)
    }

    suspend fun fetchBankObat(): List<BankObat> {
        val userId = SupabaseHolder.session()?.user?.id ?: return emptyList()
        val response = postgrest["bank_obat"].select {
            filter {
                eq("user_id", userId)
            }
            order("created_at", Order.DESCENDING)
        }
        val list = response.decodeList<BankObatDto>()
        return list.map { BankObatMapper.map(it, ::resolveImageUrl) }
    }

    suspend fun addObat(
        title: String,
        description: String?,
        imageFile: File?
    ): BankObat {

        val userId = SupabaseHolder.session()?.user?.id
            ?: throw IllegalStateException("User not logged in")


        var imagePath: String? = null
        if (imageFile != null) {
            imagePath = uploadImage(imageFile, userId)
        }

        val insert = postgrest["bank_obat"].insert(
            mapOf(
                "user_id" to userId,
                "title" to title,
                "description" to description,
                "image_url" to imagePath
            )
        ) { select() }

        val dto = insert.decodeSingle<BankObatDto>()
        return BankObatMapper.map(dto, ::resolveImageUrl)
    }

    private suspend fun uploadImage(file: File, uid: String): String = withContext(Dispatchers.IO) {
        // Simpan di folder milik user agar lolos policy: name LIKE auth.uid() || '/%'
        val objectName = "$uid/${UUID.randomUUID()}_${file.name}"
        storage.upload(objectName, file.readBytes())
        objectName // ini yang disimpan ke kolom image_path
    }

    suspend fun deleteObat(id: String) {
        postgrest["bank_obat"].delete {
            filter { eq("id", id) }
        }
    }

    suspend fun getObat(id: String): BankObat? {
        val response = postgrest["bank_obat"].select {
            filter { eq("id", id) }
            // JANGAN pakai single() di sini
        }
        Log.d("GET_BANKOBAT", "raw=" + (response.data ?: "null"))
        val dto = response.decodeList<BankObatDto>().firstOrNull() ?: return null
        return BankObatMapper.map(dto, ::resolveImageUrl)
    }
}