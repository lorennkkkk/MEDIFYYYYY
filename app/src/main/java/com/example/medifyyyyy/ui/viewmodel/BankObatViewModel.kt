package com.example.medifyyyyy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medifyyyyy.data.repositories.BankObatRepository
import com.example.medifyyyyy.data.remote.SupabaseHolder
import com.example.medifyyyyy.domain.model.BankObat
import com.example.medifyyyyy.ui.common.UiResult

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class BankObatViewModel(
    private val repo: BankObatRepository = BankObatRepository()
) : ViewModel() {

    private val _BankObat = MutableStateFlow<UiResult<List<BankObat>>>(UiResult.Loading)
    val BankObat: StateFlow<UiResult<List<BankObat>>> = _BankObat

    private val _adding = MutableStateFlow<UiResult<BankObat>?>(null)
    val adding: StateFlow<UiResult<BankObat>?> = _adding


    fun loadBankObat() {
        _BankObat.value = UiResult.Loading
        viewModelScope.launch {
            try {
                val list = repo.fetchBankObat()
                _BankObat.value = UiResult.Success(list)
            } catch (e: Exception) {
                _BankObat.value = UiResult.Error(e.message ?: "Gagal memuat")
            }
        }
    }

    fun addObat(title: String, description: String?, imageFile: File?) {
        _adding.value = UiResult.Loading
        viewModelScope.launch {
            try {
                val BankObat = repo.addObat(title, description, imageFile)
                _adding.value = UiResult.Success(BankObat)
                loadBankObat()
            } catch (e: Exception) {
                _adding.value = UiResult.Error(e.message ?: "Tambah gagal")
            }
        }
    }

    fun deleteObat(id: String) {
        viewModelScope.launch {
            try {
                repo.deleteObat(id)
                loadBankObat()
            } catch (_: Exception) { }
        }
    }

    suspend fun getObat(id: String): BankObat? = repo.getObat(id)
}