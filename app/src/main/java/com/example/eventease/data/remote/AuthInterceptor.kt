package com.example.eventease.data.remote

import android.content.Context
import com.example.eventease.data.datastore.UserPreferencesManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context) : Interceptor {

    private val userPreferencesManager = UserPreferencesManager(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            userPreferencesManager.userToken.first()
        }

        val request = chain.request()
        val builder = request.newBuilder()

        token?.let {
            builder.addHeader("Authorization", "Bearer $it")
        }

        return chain.proceed(builder.build())
    }
}