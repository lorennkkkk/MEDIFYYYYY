package com.example.medifyyyyy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medifyyyyy.data.repositories.AuthRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class SaveProfileState {
    object Idle : SaveProfileState()
    object Loading : SaveProfileState()
    object Success : SaveProfileState()
    data class Error(val message: String) : SaveProfileState()
}

class ProfileViewModel : ViewModel() {

    private val repo = AuthRepository()

    // STATE PROFILE
    private val _firstname = MutableStateFlow("")
    val firstname: StateFlow<String> = _firstname

    private val _lastname = MutableStateFlow("")
    val lastname: StateFlow<String> = _lastname

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _imageUrl = MutableStateFlow<String?>(null)
    val imageUrl: StateFlow<String?> = _imageUrl

    private val _saveState = MutableStateFlow<SaveProfileState>(SaveProfileState.Idle)
    val saveState: StateFlow<SaveProfileState> = _saveState


    fun loadProfile() {
        val user = repo.currentSession()?.user ?: return
        val userId = user.id

        viewModelScope.launch {
            val data = repo.getProfile(userId) ?: return@launch

            _firstname.value = data.first_name
            _lastname.value = data.last_name
            _username.value = data.username
            _email.value = data.email
            _imageUrl.value = data.profile_image_url

        }
    }

    fun saveProfileChanges(imageBytes: ByteArray?) {
        val user = repo.currentSession()?.user ?: return
//        val userId = user.id

        viewModelScope.launch {
            try {
                _saveState.value = SaveProfileState.Loading

                repo.saveProfileChanges(
                    firstname.value,
                    lastname.value,
                    username.value,
                    imageBytes
                )

                _saveState.value = SaveProfileState.Success
            } catch (e: Exception) {
                _saveState.value = SaveProfileState.Error("Gagal menyimpan perubahan")
            }
        }
    }


    fun resetSaveState() {
        _saveState.value = SaveProfileState.Idle
    }


    fun logout() {
        viewModelScope.launch {
            repo.logout()
        }
    }

    fun updateFirstname(v: String) { _firstname.value = v }
    fun updateLastname(v: String) { _lastname.value = v }
    fun updateUsername(v: String) { _username.value = v }
}