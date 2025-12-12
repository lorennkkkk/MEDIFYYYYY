package com.example.medifyyyyy.data.repositories

import com.example.medifyyyyy.data.remote.SupabaseHolder // Asumsi file ini ada
import com.example.medifyyyyy.domain.mapper.SertifikatVaksinMapper
import com.example.medifyyyyy.domain.model.SertifikatVaksinDto
import com.example.medifyyyyy.ui.pages.SertifikatVaksin
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class SertifikatVaksinRepository {

    // Akses Supabase Client
    private val postgrest get() = SupabaseHolder.client.postgrest
    // Ganti dengan nama bucket storage Anda yang sebenarnya
    private val storage get() = SupabaseHolder.client.storage.from("vaksin-certificates")

    private fun resolveImageUrl(path: String?): String? {
        if (path == null) return null
        return storage.publicUrl(path)
    }

    /** Mengambil semua sertifikat vaksin milik user yang sedang login. */
    suspend fun fetchSertifikatVaksin(): List<SertifikatVaksin> {
        // Mendapatkan ID user yang sedang login
        val userId = SupabaseHolder.session()?.user?.id ?: return emptyList()

        val response = postgrest["sertifikat_vaksin"].select {
            filter {
                eq("user_id", userId)
            }
            order("tanggal_vaksinasi", Order.DESCENDING)
        }
        val list = response.decodeList<SertifikatVaksinDto>()
        return list.map { SertifikatVaksinMapper.map(it, ::resolveImageUrl) }
    }

    /** Menambahkan data sertifikat baru, termasuk upload gambar. */
    suspend fun addSertifikat(
        namaLengkap: String,
        jenisVaksin: String,
        dosis: String,
        tanggalVaksinasi: String, // String ISO 8601
        tempatVaksinasi: String,
        imageFile: File?
    ): SertifikatVaksin {
        val userId = SupabaseHolder.session()?.user?.id
            ?: throw IllegalStateException("User not logged in")

        var imagePath: String? = null
        if (imageFile != null) {
            imagePath = uploadImage(imageFile, userId)
        }

        val insert = postgrest["sertifikat_vaksin"].insert(
            mapOf(
                "user_id" to userId,
                "nama_lengkap" to namaLengkap,
                "jenis_vaksin" to jenisVaksin,
                "dosis" to dosis,
                "tanggal_vaksinasi" to tanggalVaksinasi,
                "tempat_vaksinasi" to tempatVaksinasi,
                "image_path" to imagePath
            )
        ) { select() }

        val dto = insert.decodeSingle<SertifikatVaksinDto>()
        return SertifikatVaksinMapper.map(dto, ::resolveImageUrl)
    }

    private suspend fun uploadImage(file: File, uid: String): String = withContext(Dispatchers.IO) {
        // Simpan file di folder milik user: [uid]/[UUID]_[filename]
        val objectName = "$uid/${UUID.randomUUID()}_${file.name}"
        storage.upload(objectName, file.readBytes())
        objectName // path yang akan disimpan di kolom image_path DB
    }
}