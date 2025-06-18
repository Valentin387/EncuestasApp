package com.kata3.encuestasapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kata3.encuestasapp.data.repositories.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginResult {
    data class Success(val token: String) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _loginResult = MutableStateFlow<LoginResult?>(null)
    val loginResult: StateFlow<LoginResult?> = _loginResult

    fun loginUser(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = authRepository.loginUser(email, password)
                if (response.isSuccessful && response.body()?.token != null) {
                    _loginResult.emit(LoginResult.Success(response.body()!!.token))
                } else {
                    _loginResult.emit(LoginResult.Error("Invalid credentials"))
                }
            } catch (e: Exception) {
                _loginResult.emit(LoginResult.Error("Error: ${e.message}"))
            }
        }
    }
}