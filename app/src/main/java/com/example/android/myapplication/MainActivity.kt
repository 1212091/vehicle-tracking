package com.example.android.myapplication

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.android.myapplication.databinding.ActivityMainBinding
import com.example.android.myapplication.viewmodel.LoginViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import android.content.pm.PackageManager
import android.graphics.Color
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.View
import com.example.android.myapplication.model.Location
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.JointType.ROUND
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.SquareCap
import com.google.android.gms.maps.model.LatLng
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.fixedRateTimer
import kotlin.concurrent.timer


class MainActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mapFragment: SupportMapFragment
    private var map: GoogleMap? = null
    private lateinit var loginViewModel: LoginViewModel
    private var token: String? = null
    private var polyLineList: ArrayList<LatLng>? = null
    private val PERMISSION_CODE: Int = 503
    private lateinit var locationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var isLocationPermissionAllow: Boolean = false
    private var previousLocation: LatLng? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        polyLineList = ArrayList()
        setUpLogin()
    }

    override fun onResume() {
        super.onResume()
        if (isLocationPermissionAllow) {
            binding.startButton.setOnClickListener(this)
            binding.stopButton.setOnClickListener(this)
        }
    }


    private fun setUpLogin() {
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        loginViewModel.login().observe(this, Observer {
            token = it?.token
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_CODE)
            setUpMap()
            refreshToken()
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    isLocationPermissionAllow = true
                }
                return
            }
        }
    }


    private fun setUpMap() {
        mapFragment = SupportMapFragment.newInstance()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.mapView, mapFragment, null)
        fragmentTransaction.commit()
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        this.map = googleMap
        map?.mapType = GoogleMap.MAP_TYPE_NORMAL
        map?.isTrafficEnabled = false
        map?.isIndoorEnabled = false
        map?.isBuildingsEnabled = false
        map?.uiSettings?.isZoomControlsEnabled = true
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        try {
            locationProviderClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val longitude = location.longitude
                        val latitude = location.latitude
                        val currentLocation = LatLng(latitude, longitude)
                        map?.addMarker(
                            MarkerOptions().position(currentLocation)
                                .flat(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car))
                        )
                        map?.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
                        map?.moveCamera(
                            CameraUpdateFactory.newCameraPosition(
                                CameraPosition.Builder()
                                    .target(map?.cameraPosition?.target)
                                    .zoom(17f)
                                    .bearing(30f)
                                    .tilt(30f)
                                    .build()
                            )
                        )
                    }
                }.addOnFailureListener { e ->
                    Log.d("Map Activity", "Error trying to get last GPS location")
                }
        } catch (e: SecurityException) {
            Log.v("Main Activity", "Permission denied: " + e.message)
        }

    }

    override fun onClick(view: View?) {
        when (view) {
            binding.startButton -> registerLocationListener()
            binding.stopButton -> unregisterLocationListener()
        }
    }

    private fun unregisterLocationListener() {
        locationProviderClient.removeLocationUpdates(locationCallback)
        map?.clear()
        binding.loadingView.visibility = View.VISIBLE
        binding.mapView.visibility = View.INVISIBLE
        loginViewModel.getAllTrackingPoints(token).observe(this, Observer {
            binding.loadingView.visibility = View.INVISIBLE
            binding.mapView.visibility = View.VISIBLE
            val latLngList = ArrayList<LatLng>()
            val bounds = LatLngBounds.Builder()
            for (point in it!!) {
                val latLng = LatLng(point.latitude, point.longitude)
                bounds.include(latLng)
                latLngList.add(latLng)
            }
            val polyLineOptions = PolylineOptions().apply {
                color(Color.BLACK)
                width(6f)
                startCap(SquareCap())
                endCap(SquareCap())
                jointType(ROUND)
            }
            map?.addPolyline(polyLineOptions.addAll(latLngList))
            map?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 0))
            if (latLngList.isNotEmpty()) {
                map?.addMarker(
                    MarkerOptions().position(latLngList.lastOrNull() ?: LatLng(0.0, 0.0))
                        .flat(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car))
                )
            }
        })
        binding.stopButton.isEnabled = false
        binding.startButton.isEnabled = true
    }

    private fun registerLocationListener() {
        try {
            val locationRequest = LocationRequest.create()?.apply {
                interval = 3000
                fastestInterval = 3000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    val longitude = locationResult?.lastLocation?.longitude
                    val latitude = locationResult?.lastLocation?.latitude
                    if (longitude != null && latitude != null) {
                        val currentLocation = LatLng(latitude, longitude)
                        Log.v("Main Activity", "Latitude: $latitude / $longitude")
                        polyLineList?.add(currentLocation)
                        map?.clear()
                        loginViewModel.saveCurrentLocation(Location(latitude, longitude), token)
                        val marker = map?.addMarker(
                            MarkerOptions().position(currentLocation)
                                .flat(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car))
                        )
                        if (previousLocation != null) {
                            marker?.rotation = getBearing(previousLocation!!, currentLocation)
                        }
                        map?.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
                        map?.moveCamera(
                            CameraUpdateFactory.newCameraPosition(
                                CameraPosition.Builder()
                                    .target(map?.cameraPosition?.target)
                                    .zoom(17f)
                                    .bearing(30f)
                                    .tilt(30f)
                                    .build()
                            )
                        )
                        previousLocation = currentLocation
                    }
                }
            }
            locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
        } catch (e: SecurityException) {
            Log.v("Main Activity", "Permission denied: " + e.message)
        }
        binding.startButton.isEnabled = false
        binding.stopButton.isEnabled = true
    }

    private fun getBearing(begin: LatLng, end: LatLng): Float {
        val lat = Math.abs(begin.latitude - end.latitude)
        val lng = Math.abs(begin.longitude - end.longitude)

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return Math.toDegrees(Math.atan(lng / lat)).toFloat()
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (90 - Math.toDegrees(Math.atan(lng / lat)) + 90).toFloat()
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (Math.toDegrees(Math.atan(lng / lat)) + 180).toFloat()
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (90 - Math.toDegrees(Math.atan(lng / lat)) + 270).toFloat()
        return -1f
    }

    private fun refreshToken() {
        val self = this
        fixedRateTimer("default", false, initialDelay = 5000, period = 60000) {
            Log.v("Map Activity", "Refresh data")
            loginViewModel.refreshToken(token).observe(self, Observer {
                token = it?.token
            })
        }
        //
    }
}
