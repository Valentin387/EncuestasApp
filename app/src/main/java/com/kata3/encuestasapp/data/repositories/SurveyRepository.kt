package com.kata3.encuestasapp.data.repositories

import com.kata3.encuestasapp.io.SurveyService
import com.kata3.encuestasapp.ui.response.models.ResponseDto
import com.kata3.encuestasapp.ui.response.models.SurveyDto

class SurveyRepository(private val surveyService: SurveyService) {

    suspend fun getSurvey(surveyId: String): SurveyDto? {
        val response = surveyService.getSurvey(surveyId)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun submitResponse(surveyId: String, response: ResponseDto): Boolean {
        val responseResult = surveyService.postResponse(surveyId, response)
        return responseResult.isSuccessful && responseResult.body()?.get("success") == true
    }
}