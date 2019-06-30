package com.example.android.myapplication

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.android.myapplication.databinding.ActivityMainBinding
import com.example.android.myapplication.viewmodel.LoginViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class MainActivity : AppCompatActivity(), OnMapReadyCallback{

    private lateinit var binding: ActivityMainBinding
    private lateinit var mapFragment: SupportMapFragment
    private var googleMap: GoogleMap? = null
    private lateinit var loginViewModel: LoginViewModel

    override fun onMapReady(googleMap: GoogleMap?) {
        this.googleMap = googleMap
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setUpLogin()
    }

    private fun setUpLogin() {
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        loginViewModel.login()
        loginViewModel.loginResponseLiveData.observe(this, Observer {
            System.out.println("Token response: " + it?.token)
            setUpMap()
        })
    }

    private fun setUpMap() {
        mapFragment = SupportMapFragment.newInstance()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.mapLayout, mapFragment)
        fragmentTransaction.commit()
        mapFragment.getMapAsync(this)
    }
}
