package com.kata3.encuestasapp.io

import android.content.Context
import com.kata3.encuestasapp.BuildConfig
import com.kata3.encuestasapp.data.repositories.SignUpResponse
import com.kata3.encuestasapp.data.repositories.UserDto
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("auth/register")
    suspend fun postRegister(@Body request: UserDto): Response<SignUpResponse>

    @POST("auth/login")
    suspend fun postLogin(@Body request: UserDto): Response<SignUpResponse>

    companion object Factory {
        private const val BASE_URL = BuildConfig.BASE_URL

        fun create(context: Context): AuthService {
            val client = OkHttpClient.Builder().build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AuthService::class.java)
        }
    }
}