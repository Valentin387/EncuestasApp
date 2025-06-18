package com.kata3.encuestasapp.data.repositories

data class UserDto(
    val email: String,
    val firstname: String? = null,
    val lastname: String? = null,
    val password: String,
    val company: String? = null
)
