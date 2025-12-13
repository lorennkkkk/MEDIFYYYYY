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

    init {
        fetchData()
    }

    fun fetchData() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getAllergyLogs().collect { _allergyLogs.value = it }
        }
        viewModelScope.launch {
            repository.getDrugLogs().collect {
                _drugLogs.value = it
                _isLoading.value = false
            }
        }
    }

    fun addDrugLog(log: DrugLog, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.addDrugLog(log)
                fetchData() // Refresh data setelah berhasil menambah
                onSuccess()
            } catch (e: Exception) {
                Log.e("LogViewModel", "Gagal menyimpan log: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
