package com.example.pa_pam.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pa_pam.data.repositories.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repo = AuthRepository()
    var loading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var username by mutableStateOf<String?>(null)
    var firstName by mutableStateOf<String?>(null)
    var lastName by mutableStateOf<String?>(null)


    val sessionStatus = repo.sessionStatus

    fun register(firstName: String,
                 lastName: String,
                 username: String,
                 email: String,
                 password: String,
                 onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            loading = true
            errorMessage = null

            try {
                repo.register(firstName, lastName, username, email, password)
                onSuccess()
            } catch (e: Exception) {
                errorMessage = e.message
            }

            loading = false
        }
    }


    fun login(email: String, password: String) =
        viewModelScope.launch {
            repo.login(email, password)
        }

    fun loginByUsername(
        username: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            loading = true
            errorMessage = null

            try {
                val email = repo.getEmailByUsername(username)
                    ?: throw Exception("Username tidak ditemukan")

                repo.login(email, password)

                onSuccess()

            } catch (e: Exception) {
                errorMessage = e.message
            }

            loading = false
        }
    }

    fun loadCurrentUserProfile() {
        viewModelScope.launch {
            val session = repo.currentSession() ?: return@launch
            val userId = session.user!!.id

            val data = repo.getProfile(userId) ?: return@launch

            username = data.username
            firstName = data.first_name
            lastName = data.last_name
        }
    }

    fun logout() = viewModelScope.launch {
        repo.logout()
    }

    fun saveProfile(first: String, last: String, username: String, image: ByteArray?) =
        viewModelScope.launch {
            repo.saveProfileChanges(first, last, username, image)
        }

    fun currentUser() = repo.currentSession()
}
