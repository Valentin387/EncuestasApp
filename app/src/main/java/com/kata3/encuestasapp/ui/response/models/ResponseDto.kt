package com.kata3.encuestasapp.ui.response.models

data class ResponseDto(
    val respondent: Respondent,
    val answers: List<Answer>
) {
    data class Respondent(
        val email: String,
        val firstname: String,
        val lastname: String
    )

    data class Answer(
        val questionId: String,
        val answer: Any // Int for age/rating, String for text
    )
}