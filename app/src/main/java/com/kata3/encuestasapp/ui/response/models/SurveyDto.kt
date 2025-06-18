package com.kata3.encuestasapp.ui.response.models

data class SurveyDto(
    val id: String,
    val company: String,
    val title: String,
    val description: String,
    val questions: List<Question>
)