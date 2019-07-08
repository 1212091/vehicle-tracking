package com.example.android.myapplication.service

import com.example.android.myapplication.model.Location
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface GeoApi{
    @POST("Tracking")
    fun saveCurrentLocation(@Body location: Location): Deferred<Response<Location>>

    @GET("Tracking")
    fun getAllTrackingPoints(): Deferred<Response<List<Location>>>
}