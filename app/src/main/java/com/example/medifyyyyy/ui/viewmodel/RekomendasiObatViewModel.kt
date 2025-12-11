package com.example.medifyyyyy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medifyyyyy.data.repository.RekomendasiObatRepository
import com.example.medifyyyyy.domain.model.RekomendasiObat
import com.example.medifyyyyy.ui.common.UiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class RekomendasiObatViewModel(
    private val repo: RekomendasiObatRepository = RekomendasiObatRepository()
) : ViewModel() {

    private val _RekomendasiObat = MutableStateFlow<UiResult<List<RekomendasiObat>>>(UiResult.Loading)
    val RekomendasiObat: StateFlow<UiResult<List<RekomendasiObat>>> = _RekomendasiObat

    private val _adding = MutableStateFlow<UiResult<RekomendasiObat>?>(null)
    val adding: StateFlow<UiResult<RekomendasiObat>?> = _adding


    fun loadRekomendasiObat() {
        _RekomendasiObat.value = UiResult.Loading
        viewModelScope.launch {
            try {
                val list = repo.fetchRekomendasiObat()
                _RekomendasiObat.value = UiResult.Success(list)
            } catch (e: Exception) {
                _RekomendasiObat.value = UiResult.Error(e.message ?: "Gagal memuat")
            }
        }
    }

    fun addObat(title: String, description: String?, imageFile: File?) {
        _adding.value = UiResult.Loading
        viewModelScope.launch {
            try {
                val RekomendasiObat = repo.addObat(title, description, imageFile)
                _adding.value = UiResult.Success(RekomendasiObat)
                loadRekomendasiObat()
            } catch (e: Exception) {
                _adding.value = UiResult.Error(e.message ?: "Tambah gagal")
            }
        }
    }

    fun deleteObat(id: String) {
        viewModelScope.launch {
            try {
                repo.deleteObat(id)
                loadRekomendasiObat()
            } catch (_: Exception) { }
        }
    }

    suspend fun getObat(id: String): RekomendasiObat? = repo.getObat(id)
}