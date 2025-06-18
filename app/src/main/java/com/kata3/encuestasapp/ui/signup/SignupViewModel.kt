package com.kata3.encuestasapp.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kata3.encuestasapp.data.repositories.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class SignupResult {
    data class Success(val token: String) : SignupResult()
    data class Error(val message: String) : SignupResult()
}

class SignupViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _signupResult = MutableStateFlow<SignupResult?>(null)
    val signupResult: StateFlow<SignupResult?> = _signupResult

    fun registerUser(email: String, firstname: String, lastname: String, password: String, company: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = authRepository.registerUser(email, firstname, lastname, password, company)
                if (response.isSuccessful && response.body()?.token != null) {
                    _signupResult.emit(SignupResult.Success(response.body()!!.token))
                } else {
                    _signupResult.emit(SignupResult.Error("Registration failed"))
                }
            } catch (e: Exception) {
                _signupResult.emit(SignupResult.Error("Error: ${e.message}"))
            }
        }
    }
}