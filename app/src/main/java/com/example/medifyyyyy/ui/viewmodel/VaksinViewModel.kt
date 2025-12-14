package com.example.medifyyyyy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medifyyyyy.data.repositories.SertifikatVaksinRepository
import com.example.medifyyyyy.domain.model.SertifikatVaksin
import com.example.medifyyyyy.ui.pages.UIResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VaksinViewModel(
    private val repository: SertifikatVaksinRepository = SertifikatVaksinRepository()
) : ViewModel() {

    // State untuk daftar sertifikat vaksin (menggunakan UIResult)
    private val _sertifikatListState = MutableStateFlow<UIResult<List<SertifikatVaksin>>>(UIResult.Loading)
    val sertifikatListState: StateFlow<UIResult<List<SertifikatVaksin>>> = _sertifikatListState.asStateFlow()

    // State untuk operasi penambahan sertifikat (upload/simpan)
    private val _addSertifikatState = MutableStateFlow<UIResult<SertifikatVaksin>>(UIResult.Idle)
    val addSertifikatState: StateFlow<UIResult<SertifikatVaksin>> = _addSertifikatState.asStateFlow()

    init {
        fetchSertifikat()
    }

    fun fetchSertifikat() {
        viewModelScope.launch {
            _sertifikatListState.value = UIResult.Loading
            try {
                val list = repository.fetchSertifikatVaksin()
                _sertifikatListState.value = UIResult.Success(list)
            } catch (e: Exception) {
                _sertifikatListState.value = UIResult.Error(e)
            }
        }
    }

    fun addSertifikat(
        namaLengkap: String,
        jenisVaksin: String,
        dosis: String,
        tanggal: Date,
        tempatVaksinasi: String,
        imageFile: File?
    ) {
        viewModelScope.launch {
            _addSertifikatState.value = UIResult.Loading
            try {
                // PERBAIKAN: Konversi Date ke String ISO 8601 untuk Supabase.
                // Format "yyyy-MM-dd'T'HH:mm:ss.SSSZ" lebih akurat untuk Supabase Timestamp.
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
                val tanggalVaksinasiString = dateFormat.format(tanggal)

                val newSertifikat = repository.addSertifikat(
                    namaLengkap = namaLengkap,
                    jenisVaksin = jenisVaksin,
                    dosis = dosis,
                    tanggalVaksinasi = tanggalVaksinasiString,
                    tempatVaksinasi = tempatVaksinasi,
                    imageFile = imageFile
                )

                _addSertifikatState.value = UIResult.Success(newSertifikat)

                // Setelah berhasil, muat ulang daftar agar tampilan otomatis terupdate
                fetchSertifikat()

            } catch (e: Exception) {
                // Error akan dikirim ke AddVaksinScreen untuk di-log dan ditangani
                _addSertifikatState.value = UIResult.Error(e)
            }
        }
    }

    fun resetAddSertifikatState() {
        _addSertifikatState.value = UIResult.Idle
    }
}