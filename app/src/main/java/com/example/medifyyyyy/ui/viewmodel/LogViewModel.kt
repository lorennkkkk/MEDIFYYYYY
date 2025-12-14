package com.example.medifyyyyy.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medifyyyyy.data.repository.LogRepository
import com.example.medifyyyyy.domain.model.AllergyLog
import com.example.medifyyyyy.domain.model.DrugLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LogViewModel : ViewModel() {
    private val repository = LogRepository()

    private val _allergyLogs = MutableStateFlow<List<AllergyLog>>(emptyList())
    val allergyLogs = _allergyLogs.asStateFlow()

    private val _drugLogs = MutableStateFlow<List<DrugLog>>(emptyList())
    val drugLogs = _drugLogs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        fetchData()
    }

    fun fetchData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Ambil data alergi
                repository.getAllergyLogs().collect { _allergyLogs.value = it }

                // Ambil data obat (menggunakan suspend function baru)
                val drugs = repository.getDrugLogs()
                _drugLogs.value = drugs

            } catch (e: Exception) {
                Log.e("LogViewModel", "Fetch failed: ${e.message}")
                _error.value = "Gagal memuat data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // CREATE
    // Tambahkan parameter imageBytes (nullable)
    fun addDrugLog(log: DrugLog, imageBytes: ByteArray? = null, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Teruskan imageBytes ke repository
                repository.addDrugLog(log, imageBytes)
                fetchData()
                onSuccess()
            } catch (e: Exception) {
                Log.e("LogViewModel", "Add failed: ${e.message}")
                _error.value = "Gagal menyimpan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // UPDATE
    // Tambahkan parameter imageBytes (nullable)
    fun updateDrugLog(log: DrugLog, imageBytes: ByteArray? = null, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Teruskan imageBytes ke repository
                repository.updateDrugLog(log, imageBytes)
                fetchData()
                onSuccess()
            } catch (e: Exception) {
                Log.e("LogViewModel", "Update failed: ${e.message}")
                _error.value = "Gagal mengupdate: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // DELETE
    fun deleteDrugLog(id: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.deleteDrugLog(id)
                fetchData()
                onSuccess()
            } catch (e: Exception) {
                Log.e("LogViewModel", "Delete failed: ${e.message}")
                _error.value = "Gagal menghapus: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // GET SINGLE
    suspend fun getDrugLog(id: Long): DrugLog? {
        return try {
            repository.getDrugLog(id)
        } catch (e: Exception) {
            Log.e("LogViewModel", "Get Detail failed: ${e.message}")
            _error.value = "Gagal mengambil detail: ${e.message}"
            null
        }
    }

    fun clearError() {
        _error.value = null
    }
}
