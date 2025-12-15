package com.example.medifyyyyy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medifyyyyy.data.repositories.AllergyFoodRepository
import com.example.medifyyyyy.domain.model.AllergyFood
import com.example.medifyyyyy.ui.common.UiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AllergyFoodViewModel(
    private val repo: AllergyFoodRepository = AllergyFoodRepository()
) : ViewModel() {

    private val _allergyListState = MutableStateFlow<UiResult<List<AllergyFood>>>(UiResult.Loading)
    val allergyListState: StateFlow<UiResult<List<AllergyFood>>> = _allergyListState

    private val _adding = MutableStateFlow<UiResult<Boolean>?>(null)
    val adding: StateFlow<UiResult<Boolean>?> = _adding

    fun loadAllergies() {
        _allergyListState.value = UiResult.Loading
        viewModelScope.launch {
            try {
                val list = repo.getAllergyFoods()
                _allergyListState.value = UiResult.Success(list)
            } catch (e: Exception) {
                _allergyListState.value = UiResult.Error(e.message ?: "Gagal memuat data")
            }
        }
    }

    fun addAllergy(name: String, description: String, imageBytes: ByteArray?) {
        _adding.value = UiResult.Loading
        viewModelScope.launch {
            try {
                repo.addAllergyFood(name, description, imageBytes)
                _adding.value = UiResult.Success(true)
                loadAllergies() // Refresh otomatis
            } catch (e: Exception) {
                _adding.value = UiResult.Error(e.message ?: "Gagal menyimpan")
            }
        }
    }

    // Fungsi Hapus
    fun deleteAllergy(id: String) {
        viewModelScope.launch {
            try {
                repo.deleteAllergyFood(id)
                loadAllergies()
            } catch (e: Exception) { }
        }
    }

    fun resetAddState() {
        _adding.value = null
    }

    suspend fun getAllergy(id: String): AllergyFood? {
        return repo.getAllergyFood(id)
    }

    // var state 1 data detail
    private val _detailState = MutableStateFlow<UiResult<AllergyFood?>>(UiResult.Loading)
    val detailState = _detailState.asStateFlow()

    // get allergy
    fun getDetailAllergy(id: String) {
        viewModelScope.launch {
            _detailState.value = UiResult.Loading
            try {
                val result = getAllergy(id)
                if (result != null) {
                    _detailState.value = UiResult.Success(result)
                } else {
                    _detailState.value = UiResult.Error("Data kosong")
                }
            } catch (e: Exception) {
                _detailState.value = UiResult.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }
}