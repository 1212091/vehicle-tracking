package com.example.android.myapplication.service

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiFactory {
    private val authInterceptor = Interceptor { chain ->
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

    //OkhttpClient for building http request url
    private val trackingClient = OkHttpClient().newBuilder()
        .addInterceptor(authInterceptor)
        .build()


    private fun retrofit(): Retrofit = Retrofit.Builder()
        .client(trackingClient)
        .baseUrl("https://geo-tracking.herokuapp.com/api/")
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()


    val authApi: AuthApi = retrofit().create(AuthApi::class.java)
}

