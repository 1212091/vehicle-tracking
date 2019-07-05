package com.example.android.myapplication.model

import okhttp3.Route
import com.google.gson.annotations.SerializedName


data class GoogleMapResponse(
    @SerializedName("geocoded_waypoints")
    var geocodedWaypoints: List<GeocodedWaypoint>?,
    @SerializedName("routes")
    var routes: List<Route>?,
    @SerializedName("status")
    var status: String?
)