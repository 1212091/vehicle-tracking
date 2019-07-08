package com.example.android.myapplication.service

import com.example.android.myapplication.model.GoogleMapResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IGoogleApi {
    @GET("v1/snapToRoads")
    fun getRoadApiValue(
        @Query("path") path: String,
        @Query("interpolate") interpolate: Boolean,
        @Query("key") apiKey: String
    ): Deferred<Response<GoogleMapResponse>>
}