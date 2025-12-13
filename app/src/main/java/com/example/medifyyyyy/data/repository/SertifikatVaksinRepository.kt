package com.example.medifyyyyy.data.repositories

import com.example.medifyyyyy.data.remote.SupabaseHolder
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
import android.util.Log // Import untuk debugging Logcat

class SertifikatVaksinRepository {

    // Akses Supabase Client
    private val postgrest get() = SupabaseHolder.client.postgrest

    // Pastikan nama bucket ini 100% cocok dengan nama bucket di Supabase Storage Anda!
    private val storage get() = SupabaseHolder.client.storage.from("sertifikat-images")

    private fun resolveImageUrl(path: String?): String? {
        if (path == null) return null
        return storage.publicUrl(path)
    }

    /** Mengambil semua sertifikat vaksin milik user yang sedang login. */
    suspend fun fetchSertifikatVaksin(): List<SertifikatVaksin> = withContext(Dispatchers.IO) {
        // Mendapatkan ID user yang sedang login
        val userId = SupabaseHolder.session()?.user?.id ?: return@withContext emptyList()

        try {
            val response = postgrest["sertifikat_vaksin"].select {
                filter {
                    eq("user_id", userId)
                }
                order("tanggal_vaksinasi", Order.DESCENDING)
            }
            val list = response.decodeList<SertifikatVaksinDto>()
            return@withContext list.map { SertifikatVaksinMapper.map(it, ::resolveImageUrl) }
        } catch (e: Exception) {
            Log.e("SertifRepo", "Gagal memuat sertifikat: ${e.message}")
            throw e
        }
    }

    /** Menambahkan data sertifikat baru, termasuk upload gambar. */
    suspend fun addSertifikat(
        namaLengkap: String,
        jenisVaksin: String,
        dosis: String,
        tanggalVaksinasi: String, // String ISO 8601
        tempatVaksinasi: String,
        imageFile: File?
    ): SertifikatVaksin = withContext(Dispatchers.IO) {

        // 1. Validasi Otentikasi
        val userId = SupabaseHolder.session()?.user?.id
            ?: throw IllegalStateException("User not logged in")

        var imagePath: String? = null

        // KRUSIAL: Tambahkan blok try-finally untuk memastikan file sementara dihapus
        try {
            if (imageFile != null) {
                // 2. Upload Gambar
                imagePath = uploadImage(imageFile, userId)
                Log.d("SertifRepo", "Gambar berhasil di-upload ke path: $imagePath")
            }

            // 3. Insert Data ke Postgrest
            val insert = postgrest["sertifikat_vaksin"].insert(
                mapOf(
                    "user_id" to userId,
                    "nama_lengkap" to namaLengkap,
                    "jenis_vaksin" to jenisVaksin,
                    "dosis" to dosis,
                    "tanggal_vaksinasi" to tanggalVaksinasi,
                    "tempat_vaksinasi" to tempatVaksinasi,
                    "image_url" to imagePath
                )
            ) { select() }

            val dto = insert.decodeSingle<SertifikatVaksinDto>()
            return@withContext SertifikatVaksinMapper.map(dto, ::resolveImageUrl)

        } catch (e: Exception) {
            // Lemparkan lagi agar ditangkap di ViewModel dan di-log di Screen
            Log.e("SertifRepo", "Gagal operasi Add Sertifikat: ${e.message}")
            throw e
        } finally {
            // PERBAIKAN: Pastikan file sementara dihapus
            imageFile?.let {
                if (it.exists()) {
                    it.delete()
                    Log.d("SertifRepo", "File sementara dihapus.")
                }
            }
        }
    }

    private suspend fun uploadImage(file: File, uid: String): String = withContext(Dispatchers.IO) {
        try {
            // Simpan file di folder milik user: [uid]/[UUID]_[filename]
            val objectName = "$uid/${UUID.randomUUID()}_${file.name}"
            storage.upload(objectName, file.readBytes())
            return@withContext objectName // path yang akan disimpan di kolom image_path DB
        } catch (e: Exception) {
            Log.e("SertifRepo", "Gagal upload gambar: ${e.message}")
            throw e // Lemparkan error upload
        }
    }
}