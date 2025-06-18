package com.kata3.encuestasapp.io

import android.content.Context
import com.kata3.encuestasapp.BuildConfig
import com.kata3.encuestasapp.ui.response.models.ResponseDto
import com.kata3.encuestasapp.ui.response.models.SurveyDto
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SurveyService {

    @GET("api/surveys/{id}")
    suspend fun getSurvey(@Path("id") id: String): Response<SurveyDto>

    @POST("public/surveys/{id}/response")
    suspend fun postResponse(@Path("id") id: String, @Body response: ResponseDto): Response<Map<String, Boolean>>

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