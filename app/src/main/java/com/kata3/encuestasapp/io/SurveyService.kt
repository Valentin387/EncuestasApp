package com.kata3.encuestasapp.io

import android.content.Context
import com.kata3.encuestasapp.BuildConfig
import com.kata3.encuestasapp.ui.response.models.ResponseDto
import com.kata3.encuestasapp.ui.response.models.SurveyDto
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface SurveyService {

    @GET("public/surveys/{id}")
    suspend fun getSurvey(@Path("id") id: String): Response<SurveyDto>

    @POST("public/surveys/{id}/response")
    suspend fun postResponse(@Path("id") id: String, @Body response: ResponseDto): Response<Map<String, Boolean>>

    @GET("api/surveys")
    suspend fun getMySurveys(@Header("Authorization") token: String): Response<List<SurveyDto>>

    @GET("api/surveys")
    suspend fun getCompanySurveys(@Header("Authorization") token: String, @Query("company") company: String): Response<List<SurveyDto>>

    @POST("api/surveys")
    suspend fun createSurvey(@Header("Authorization") token: String, @Body survey: SurveyDto): Response<SurveyDto>

    @GET("api/surveys/{id}/responses")
    suspend fun getSurveyResponses(@Header("Authorization") token: String, @Path("id") surveyId: String): Response<List<ResponseDto>>

    @DELETE("api/surveys/{id}")
    suspend fun deleteSurvey(@Header("Authorization") token: String, @Path("id") surveyId: String): Response<Map<String, Boolean>>

    @DELETE("api/surveys/{id}/cascade")
    suspend fun deleteSurveyCascade(@Header("Authorization") token: String, @Path("id") surveyId: String): Response<Map<String, Boolean>>

    companion object Factory {
        private const val BASE_URL = BuildConfig.BASE_URL

        fun create(context: Context): SurveyService {
            val client = OkHttpClient.Builder().build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SurveyService::class.java)
        }
    }
}