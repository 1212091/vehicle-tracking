package com.example.android.myapplication

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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.CameraPosition


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mapFragment: SupportMapFragment
    private var map: GoogleMap? = null
    private lateinit var loginViewModel: LoginViewModel
    private var token: String? = null
    private var polyLineList: List<LatLng>? = null
    private lateinit var destination: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        polyLineList = ArrayList()
        destination = "101+nguyen+van+linh+da+nang"
        setUpLogin()
    }

    private fun setUpLogin() {
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        loginViewModel.login().observe(this, Observer {
            setUpMap()
            token = it?.token
            loginViewModel.refreshToken(token).observe(this, Observer {
            })
        })
    }

    private fun setUpMap() {
        mapFragment = SupportMapFragment.newInstance()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.mapLayout, mapFragment, null)
        fragmentTransaction.commit()
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        val latitude = 28.671246
        val longitude = 77.317654
        this.map = googleMap
        map?.mapType = GoogleMap.MAP_TYPE_NORMAL
        map?.isTrafficEnabled = false
        map?.isIndoorEnabled = false
        map?.isBuildingsEnabled = false
        map?.uiSettings?.isZoomControlsEnabled = true


        val sydney = LatLng(latitude, longitude)
        map?.addMarker(MarkerOptions().position(sydney).title("Marker in Home"))
        map?.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        map?.moveCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder()
                    .target(googleMap?.cameraPosition?.target)
                    .zoom(17f)
                    .bearing(30f)
                    .tilt(45f)
                    .build()
            )
        )

//        loginViewModel.getGoogleApiData("driving", "less_walking", "$latitude+$longitude"
//            , "28.671246+77.317600", resources.getString(R.string.google_directions_key))
//            .observe(this, Observer {
//        })
    }
}
