package com.example.android.myapplication.service

import com.example.android.myapplication.model.LoginRequest
import com.example.android.myapplication.model.LoginResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi{
    @POST("Authentication/DeviceAuthentication")
    fun login(@Body loginRequest: LoginRequest): Deferred<Response<LoginResponse>>

    @POST("Authentication/RefreshToken")
    fun refreshToken(): Deferred<Response<LoginResponse>>
}