package com.example.android.myapplication.repository

import com.example.android.myapplication.model.GoogleMapResponse
import com.example.android.myapplication.model.Location
import com.example.android.myapplication.model.LoginRequest
import com.example.android.myapplication.model.LoginResponse
import com.example.android.myapplication.service.ApiFactory
import com.example.android.myapplication.service.AuthApi
import com.example.android.myapplication.service.GeoApi
import com.example.android.myapplication.service.IGoogleApi

class LoginRepository : BaseRepository() {

    private val basicAuthApi by lazy {
        ApiFactory.createApiWithService(ApiFactory.createBasicService(), AuthApi::class.java)
    }

    suspend fun login(clientNumber: String, secretKey: String, deviceId: String): LoginResponse? {
        return safeApiCall(
            call = { basicAuthApi.login(LoginRequest(clientNumber, secretKey, deviceId)).await() },
            errorMessage = "Cannot login"
        )
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

    suspend fun getGoogleApiData(path: String, interpolate: Boolean, apiKey: String): GoogleMapResponse {
        val googleApi = ApiFactory.createApiWithService(ApiFactory.createGoogleApiService(), IGoogleApi::class.java)
        return safeApiCall(
            call = {
                googleApi.getRoadApiValue(path, interpolate, apiKey).await()
            },
            errorMessage = "Cannot get Google API data"
        )!!
    }

    suspend fun saveCurrentLocation(location: Location, token: String?): Location {
        val bearerApi = ApiFactory.createApiWithService(ApiFactory.createBearerService(token), GeoApi::class.java)
        return safeApiCall(
            call = {
                bearerApi.saveCurrentLocation(location).await()
            },
            errorMessage = "Cannot save location"
        )!!
    }

    suspend fun getAllTrackingPoints(token: String?): List<Location> {
        val bearerApi = ApiFactory.createApiWithService(ApiFactory.createBearerService(token), GeoApi::class.java)
        return safeApiCall(
            call = {
                bearerApi.getAllTrackingPoints().await()
            },
            errorMessage = "Cannot save location"
        )!!
    }
}