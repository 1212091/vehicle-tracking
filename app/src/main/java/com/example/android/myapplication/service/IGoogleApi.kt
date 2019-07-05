package com.example.android.myapplication.service

import com.example.android.myapplication.model.GoogleMapResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IGoogleApi {

    @GET("maps/api/directions/json")
    fun getDirections(
        @Query("mode") mode: String,
        @Query("transit_routing_preference") routingPreference: String,
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") apiKey: String
    ): Deferred<Response<GoogleMapResponse>>
}