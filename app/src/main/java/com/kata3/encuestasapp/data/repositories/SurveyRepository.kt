package com.kata3.encuestasapp.data.repositories

import com.kata3.encuestasapp.io.SurveyService
import com.kata3.encuestasapp.ui.response.models.ResponseDto
import com.kata3.encuestasapp.ui.response.models.SurveyDto
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SurveyRepository(private val surveyService: SurveyService) {

    suspend fun getSurvey(surveyId: String): SurveyDto? {
        val response = surveyService.getSurvey(surveyId)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun submitResponse(surveyId: String, response: ResponseDto): Boolean {
        val responseResult = surveyService.postResponse(surveyId, response)
        return responseResult.isSuccessful && responseResult.body()?.get("success") == true
    }

    suspend fun getMySurveys(token: String): List<SurveyDto>? {
        val response = surveyService.getMySurveys("Bearer $token")
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun getCompanySurveys(token: String, company: String): List<SurveyDto>? {
        val response = surveyService.getCompanySurveys("Bearer $token", company)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun createSurvey(token: String, survey: SurveyDto): Boolean {
        val response = surveyService.createSurvey("Bearer $token", survey)
        return response.isSuccessful && response.body()?.id != null
    }

    suspend fun getSurveyResponses(token: String, surveyId: String): List<ResponseDto>? {
        val response = surveyService.getSurveyResponses("Bearer $token", surveyId)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun deleteSurvey(token: String, surveyId: String): Boolean {
        val response = surveyService.deleteSurvey("Bearer $token", surveyId)
        return response.isSuccessful && response.body()?.get("success") == true
    }

    suspend fun deleteSurveyCascade(token: String, surveyId: String): Boolean {
        val response = surveyService.deleteSurveyCascade("Bearer $token", surveyId)
        return response.isSuccessful && response.body()?.get("success") == true
    }
}