package com.kata3.encuestasapp.data.repositories

import android.util.Log
import com.kata3.encuestasapp.io.AuthService
import retrofit2.Response

class AuthRepository(private val authService: AuthService) {

    suspend fun registerUser(email: String, firstname: String, lastname: String, password: String, company: String): Response<SignUpResponse> {
        return try {
            val response = authService.postRegister(UserDto(email, firstname, lastname, password, company))
            if (response.isSuccessful) {
                Log.d("AuthRepository", "Registration successful")
            } else {
                Log.e("AuthRepository", "Registration failed. Code: ${response.code()}")
            }
            response
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error registering user", e)
            throw e
        }
    }

    suspend fun loginUser(email: String, password: String): Response<SignUpResponse> {
        return try {
            val response = authService.postLogin(UserDto(email, password = password))
            if (response.isSuccessful) {
                Log.d("AuthRepository", "Login successful")
            } else {
                Log.e("AuthRepository", "Login failed. Code: ${response.code()}")
            }
            response
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error logging in user", e)
            throw e
        }
    }
}