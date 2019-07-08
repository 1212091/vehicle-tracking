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
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.LatLngBounds




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
        fragmentTransaction.add(R.id.mapLayout, mapFragment, null)
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
        loginViewModel.getAllTrackingPoints(token).observe(this, Observer {
            val latLngList = ArrayList<LatLng>()
            val bounds = LatLngBounds.Builder()
            for (point in it!!) {
                val latLng = LatLng(point.latitude, point.longitude)
                bounds.include(latLng)
                latLngList.add(latLng)
            }
            map?.addPolyline(PolylineOptions().addAll(latLngList).color(Color.BLUE))
            map?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 0))
        })
        binding.stopButton.isEnabled = false
        binding.startButton.isEnabled = true
    }

    private fun registerLocationListener() {
        try {
            val locationRequest = LocationRequest.create()?.apply {
                interval = 5000
                fastestInterval = 5000
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
                        map?.addMarker(MarkerOptions().position(currentLocation).flat(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car)))
                        map?.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
                        map?.moveCamera(
                            CameraUpdateFactory.newCameraPosition(
                                CameraPosition.Builder()
                                    .target(map?.cameraPosition?.target)
                                    .zoom(17f)
                                    .bearing(30f)
                                    .tilt(45f)
                                    .build()
                            )
                        )
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
}
