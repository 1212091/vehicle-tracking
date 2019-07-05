package com.example.android.myapplication.service

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import okhttp3.logging.HttpLoggingInterceptor


object ApiFactory {

    private const val googleApiBaseUrl = "https://maps.googleapis.com/"

    private fun createBearerTokenInterceptor(token: String?): Interceptor {
        return Interceptor { chain ->
            val newUrl = chain.request().url()
                .newBuilder()
                .build()

            val newRequest = chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .url(newUrl)
                .build()

            chain.proceed(newRequest)
        }
    }

    private fun createBasicInterceptor(): Interceptor {
        return Interceptor { chain ->
            val newUrl = chain.request().url()
                .newBuilder()
                .build()

            val newRequest = chain.request()
                .newBuilder()
                .addHeader("Content-Type", "application/json-patch+json")
                .addHeader("Accept", "text/plain")
                .url(newUrl)
                .build()

            chain.proceed(newRequest)
        }
    }

    fun createBearerService(token: String?): Retrofit {
        val basicInterceptor = createBasicInterceptor()
        val bearerTokenInterceptor = createBearerTokenInterceptor(token)

        val bearerClient = OkHttpClient().newBuilder()
            .addInterceptor(basicInterceptor)
            .addInterceptor(bearerTokenInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        return Retrofit.Builder()
            .client(bearerClient)
            .baseUrl("https://geo-tracking.herokuapp.com/api/")
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }


    fun createBasicService(): Retrofit {
        val basicInterceptor = createBasicInterceptor()
        val basicClient = OkHttpClient().newBuilder()
            .addInterceptor(basicInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        return Retrofit.Builder()
            .client(basicClient)
            .baseUrl("https://geo-tracking.herokuapp.com/api/")
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }

    fun createGoogleApiService(): Retrofit {
        val logClient = OkHttpClient().newBuilder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }).build()

        return Retrofit.Builder()
            .client(logClient)
            .baseUrl(googleApiBaseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    fun <T> createApiWithService(retrofit: Retrofit, apiType: Class<T>): T = retrofit.create(apiType)
}

