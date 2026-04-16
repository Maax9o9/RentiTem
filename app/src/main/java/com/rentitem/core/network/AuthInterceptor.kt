package com.rentitem.core.network

import com.rentitem.core.storage.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenManager.getToken()
        val request = chain.request().newBuilder()

        if (!token.isNullOrBlank()) {
            android.util.Log.d("AUTH_DEBUG", "Sending Token: ${token.take(10)}...")
            request.addHeader("Authorization", "Bearer $token")
        } else {
            android.util.Log.d("AUTH_DEBUG", "No token found in TokenManager")
        }

        return chain.proceed(request.build())
    }
}