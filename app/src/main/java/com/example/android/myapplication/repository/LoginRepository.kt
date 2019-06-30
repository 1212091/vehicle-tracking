package com.example.android.myapplication.repository

import com.example.android.myapplication.model.LoginRequest
import com.example.android.myapplication.model.LoginResponse
import com.example.android.myapplication.service.AuthApi

class LoginRepository(private val api : AuthApi) : BaseRepository() {

    suspend fun login(clientId: String, subscriptionKey: String) : LoginResponse {
        return safeApiCall(
            call = {api.login(LoginRequest(clientId, subscriptionKey)).await()},
            errorMessage = "Cannot login"
        )!!
    }

}