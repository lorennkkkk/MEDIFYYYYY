package com.example.medifyyyyy.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medifyyyyy.data.repositories.AuthRepository
import io.github.jan.supabase.gotrue.SessionStatus
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {

    private val repo = AuthRepository()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

//    var username by mutableStateOf<String?>(null)
//    var firstName by mutableStateOf<String?>(null)
//    var lastName by mutableStateOf<String?>(null)


    val sessionStatus = repo.sessionStatus

    init {
        checkExistingSession()
        observeSessionStatus()
    }

    private fun checkExistingSession() {
        _isLoggedIn.value = repo.currentSession() != null
    }

    private fun observeSessionStatus() {
        viewModelScope.launch {
            sessionStatus.collect { status ->
                when (status) {
                    is SessionStatus.Authenticated -> {
                        _isLoggedIn.value = true
                    }
                    is SessionStatus.NotAuthenticated -> {
                        _isLoggedIn.value = false
                    }
                    else -> Unit
                }
            }
        }
    }

    fun register(firstName: String,
                 lastName: String,
                 username: String,
                 email: String,
                 password: String,
                 onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _loading.value = true
            _errorMessage.value = null


            try {
                repo.register(firstName, lastName, username, email, password)
                _isLoggedIn.value = true
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _loading.value = false
            }
        }
        }



    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _loading.value = true
            _errorMessage.value = null

            try {
                repo.login(email, password)
                _isLoggedIn.value = true
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }



    fun logout() {
        viewModelScope.launch {
            repo.logout()
            _isLoggedIn.value = false
        }
    }


}