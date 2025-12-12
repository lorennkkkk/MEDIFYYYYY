package com.example.medifyyyyy.data.repository

import android.util.Log
import com.example.medifyyyyy.data.remote.SupabaseHolder
import com.example.medifyyyyy.domain.mapper.AllergyLogMapper
import com.example.medifyyyyy.domain.model.AllergyLog
import com.example.medifyyyyy.domain.model.AllergyLogDto
import com.example.medifyyyyy.domain.model.DrugLog
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class LogRepository {
    // Mengambil client dari SupabaseHolder yang baru direvisi
    private val supabase = SupabaseHolder.client

    // --- ALERGI ---
    fun getAllergyLogs(): Flow<List<AllergyLog>> = flow {
        try {
            // Mengambil data mentah (DTO) dari tabel 'allergy_logs'
            val dtos = supabase.from("allergy_logs").select().decodeList<AllergyLogDto>()

            // Ubah DTO menjadi Domain Model menggunakan Mapper
            val domainList = dtos.map { AllergyLogMapper.map(it) }
            emit(domainList)
        } catch (e: Exception) {
            Log.e("LogRepo", "Error fetching allergy: ${e.message}")
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)

    suspend fun addAllergyLog(log: AllergyLog) {
        try {
            // Ubah Domain Model menjadi DTO sebelum dikirim ke database
            val dto = AllergyLogMapper.mapToDto(log)
            supabase.from("allergy_logs").insert(dto)
            Log.d("LogRepo", "Success insert allergy")
        } catch (e: Exception) {
            Log.e("LogRepo", "Error insert allergy: ${e.message}")
            throw e // Lempar error agar UI tahu
        }
    }

    // --- OBAT ---
    fun getDrugLogs(): Flow<List<DrugLog>> = flow {
        try {
            // Langsung decode ke DrugLog (jika tidak pakai DTO terpisah)
            val logs = supabase.from("drug_logs").select().decodeList<DrugLog>()
            emit(logs)
        } catch (e: Exception) {
            Log.e("LogRepo", "Error fetching drugs: ${e.message}")
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)
}