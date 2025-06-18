package com.kata3.encuestasapp.ui.response.models

data class Question(
    val questionId: String,
    val questionText: String,
    val type: String,
    val required: Boolean
)