package com.example.eventease.di

import android.content.Context
import com.example.eventease.data.datastore.UserPreferencesManager
import com.example.eventease.data.remote.ApiService
import com.example.eventease.data.remote.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Injection {

    private const val BASE_URL = "https://eventease-api-weld.vercel.app/"

    fun provideApiService(context: Context): ApiService {
        val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        val authInterceptor = AuthInterceptor(context)

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiService::class.java)
    }
    fun provideUserPreferencesManager(context: Context): UserPreferencesManager {
        return UserPreferencesManager(context.applicationContext)
    }
}