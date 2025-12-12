package com.example.pa_pam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pa_pam.data.repositories.AuthRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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
        val userId = user.id

        viewModelScope.launch {
            repo.saveProfileChanges(
                firstname.value,
                lastname.value,
                username.value,
                imageBytes
            )
        }
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
