package com.example.android.myapplication.repository

import com.example.android.myapplication.model.GoogleMapResponse
import com.example.android.myapplication.model.LoginRequest
import com.example.android.myapplication.model.LoginResponse
import com.example.android.myapplication.service.ApiFactory
import com.example.android.myapplication.service.AuthApi
import com.example.android.myapplication.service.IGoogleApi

class LoginRepository : BaseRepository() {

    private val basicAuthApi by lazy {
        ApiFactory.createApiWithService(ApiFactory.createBasicService(), AuthApi::class.java)
    }

    suspend fun login(clientId: String, subscriptionKey: String): LoginResponse {
        return safeApiCall(
            call = { basicAuthApi.login(LoginRequest(clientId, subscriptionKey)).await() },
            errorMessage = "Cannot login"
        )!!
    }

    suspend fun refreshToken(token: String?): LoginResponse {
        val bearerApi = ApiFactory.createApiWithService(ApiFactory.createBearerService(token), AuthApi::class.java)
        return safeApiCall(
            call = {
                bearerApi.refreshToken().await()
            },
            errorMessage = "Token cannot be refreshed"
        )!!
    }

    suspend fun getGoogleApiData(mode: String, routingPreference: String, origin: String, destination: String, apiKey: String): GoogleMapResponse {
        val googleApi = ApiFactory.createApiWithService(ApiFactory.createGoogleApiService(), IGoogleApi::class.java)
        return safeApiCall(
            call = {
                googleApi.getDirections(mode, routingPreference, origin, destination, apiKey).await()
            },
            errorMessage = "Cannot get Google API data"
        )!!
    }
}