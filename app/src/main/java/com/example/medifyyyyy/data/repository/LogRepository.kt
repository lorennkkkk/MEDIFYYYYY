package com.example.medifyyyyy.data.repository

import android.util.Log
import com.example.medifyyyyy.data.remote.SupabaseHolder
import com.example.medifyyyyy.domain.mapper.AllergyLogMapper
import com.example.medifyyyyy.domain.model.AllergyLog
import com.example.medifyyyyy.domain.model.AllergyLogDto
import com.example.medifyyyyy.domain.model.DrugLog
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.time.Duration.Companion.minutes

class LogRepository {
    private val supabase = SupabaseHolder.client
    
    // Akses ke bucket storage
    private val storage get() = supabase.storage.from("drug-log-images")

    // Helper untuk mengubah path gambar menjadi URL (Signed URL valid 1 jam)
    private suspend fun resolveImageUrl(path: String?): String? {
        if (path.isNullOrEmpty()) return null
        return try {
            // Gunakan createSignedUrl karena bucket bersifat PRIVATE
            storage.createSignedUrl(path, expiresIn = 60.minutes)
        } catch (e: Exception) {
            Log.e("LogRepo", "Failed to resolve image URL: ${e.message}")
            null
        }
    }

    // Upload Gambar ke Supabase Storage
    private suspend fun uploadImage(userId: String, imageBytes: ByteArray): String {
        // Nama file acak menggunakan UUID, disimpan di folder user
        val fileName = "$userId/${UUID.randomUUID()}.jpg"
        storage.upload(fileName, imageBytes, upsert = true)
        return fileName
    }

    private suspend fun deleteImage(path: String?) {
        if (path == null) return
        try {
            storage.delete(listOf(path))
        } catch (e: Exception) {
            Log.e("LogRepo", "Gagal hapus gambar: ${e.message}")
        }
    }

    // --- ALERGI ---
    fun getAllergyLogs(): Flow<List<AllergyLog>> = flow {
        try {
            val dtos = supabase.from("allergy_logs").select().decodeList<AllergyLogDto>()
            val domainList = dtos.map { AllergyLogMapper.map(it) }
            emit(domainList)
        } catch (e: Exception) {
            Log.e("LogRepo", "Error fetching allergy: ${e.message}")
            throw e
        }
    }.flowOn(Dispatchers.IO)

    suspend fun addAllergyLog(log: AllergyLog) {
        try {
            val dto = AllergyLogMapper.mapToDto(log)
            supabase.from("allergy_logs").insert(dto)
            Log.d("LogRepo", "Success insert allergy")
        } catch (e: Exception) {
            Log.e("LogRepo", "Error insert allergy: ${e.message}")
            throw e
        }
    }

    // --- OBAT ---

    // DTO untuk Insert
    @Serializable
    private data class DrugLogInsert(
        @SerialName("user_id") val userId: String,
        @SerialName("drug_name") val drugName: String,
        val dosage: String?,
        val time: String?,
        val status: String?,
        val description: String?,
        @SerialName("image_count") val imageCount: Int,
        @SerialName("has_image") val hasImage: Boolean,
        @SerialName("image_path") val imagePath: String?
    )

    // DTO untuk Update
    @Serializable
    private data class DrugLogUpdate(
        @SerialName("drug_name") val drugName: String,
        val dosage: String?,
        val time: String?,
        val status: String?,
        val description: String?,
        @SerialName("image_count") val imageCount: Int,
        @SerialName("has_image") val hasImage: Boolean,
        @SerialName("image_path") val imagePath: String?
    )

    // READ (List)
    suspend fun getDrugLogs(): List<DrugLog> = withContext(Dispatchers.IO) {
        try {
            // Gunakan SupabaseHolder.session() untuk mendapatkan user ID (konsisten dengan SertifikatVaksinRepository)
            val currentUserId = SupabaseHolder.session()?.user?.id
            
            if (currentUserId == null) {
                Log.e("LogRepo", "User not logged in during getDrugLogs")
                return@withContext emptyList()
            }

            val columns = Columns.raw("id, drug_name, dosage, time, status, description, image_count, has_image, image_path")
            
            val result = supabase.from("drug_logs").select(columns = columns) {
                filter {
                    eq("user_id", currentUserId)
                }
                order("created_at", Order.DESCENDING)
            }
            
            val data = result.decodeList<DrugLog>()
            
            // Resolve URL gambar untuk setiap item
            data.forEach { log ->
                 log.imageUrl = resolveImageUrl(log.imagePath) 
            }
            
            Log.d("LogRepo", "Fetched ${data.size} drug logs for user $currentUserId")
            data
        } catch (e: Exception) {
            Log.e("LogRepo", "Error fetching drugs: ${e.message}")
            throw e
        }
    }

    // READ (Single Item)
    suspend fun getDrugLog(id: Long): DrugLog? = withContext(Dispatchers.IO) {
        try {
            val columns = Columns.raw("id, drug_name, dosage, time, status, description, image_count, has_image, image_path")
            val result = supabase.from("drug_logs").select(columns = columns) {
                filter { eq("id", id) }
            }.decodeSingleOrNull<DrugLog>()
            
            // Resolve Image URL jika ada
            result?.let { it.imageUrl = resolveImageUrl(it.imagePath) }
            
            result
        } catch (e: Exception) {
            Log.e("LogRepo", "Error fetching drug detail: ${e.message}")
            throw e
        }
    }

    // CREATE
    suspend fun addDrugLog(log: DrugLog, imageBytes: ByteArray? = null) {
        try {
            val currentUserId = SupabaseHolder.session()?.user?.id
                ?: throw Exception("User not logged in")

            // 1. Upload gambar jika ada bytes
            var finalImagePath: String? = null
            if (imageBytes != null) {
                finalImagePath = uploadImage(currentUserId, imageBytes)
            }

            // 2. Simpan path gambar ke database
            val insertData = DrugLogInsert(
                userId = currentUserId,
                drugName = log.drugName,
                dosage = log.dosage,
                time = log.time,
                status = log.status,
                description = log.description,
                imageCount = if (imageBytes != null) 1 else 0,
                hasImage = imageBytes != null,
                imagePath = finalImagePath
            )
            
            supabase.from("drug_logs").insert(insertData)
            Log.d("LogRepo", "Success insert drug log")
        } catch (e: Exception) {
            Log.e("LogRepo", "Error insert drug log: ${e.message}")
            throw e
        }
    }

    // UPDATE
    suspend fun updateDrugLog(log: DrugLog, imageBytes: ByteArray? = null) {
        try {
            val currentUserId = SupabaseHolder.session()?.user?.id 
                ?: throw Exception("User not logged in")

            var finalImagePath = log.imagePath
            var finalHasImage = log.hasImage
            
            // Jika ada gambar baru, upload dan update path
            if (imageBytes != null) {
                // Hapus gambar lama jika ada (opsional, untuk kebersihan storage)
                deleteImage(log.imagePath)
                
                finalImagePath = uploadImage(currentUserId, imageBytes)
                finalHasImage = true
            }

            val updateData = DrugLogUpdate(
                drugName = log.drugName,
                dosage = log.dosage,
                time = log.time,
                status = log.status,
                description = log.description,
                imageCount = if (finalHasImage) 1 else 0,
                hasImage = finalHasImage,
                imagePath = finalImagePath
            )

            supabase.from("drug_logs").update(updateData) {
                filter { eq("id", log.id) }
            }
            Log.d("LogRepo", "Success update drug log")
        } catch (e: Exception) {
            Log.e("LogRepo", "Error update drug log: ${e.message}")
            throw e
        }
    }

    // DELETE
    suspend fun deleteDrugLog(id: Long) {
        try {
            // Ambil data dulu untuk dapat path gambar
            val log = getDrugLog(id)
            if (log != null) {
                // Hapus gambar dari storage
                deleteImage(log.imagePath)
            }

            supabase.from("drug_logs").delete {
                filter { eq("id", id) }
            }
            Log.d("LogRepo", "Success delete drug log")
        } catch (e: Exception) {
            Log.e("LogRepo", "Error delete drug log: ${e.message}")
            throw e
        }
    }
}
