package com.example.android.myapplication.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.example.android.myapplication.model.GoogleMapResponse
import com.example.android.myapplication.model.Location
import com.example.android.myapplication.model.LoginResponse
import com.example.android.myapplication.repository.LoginRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class LoginViewModel : ViewModel() {
    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default

    private val scope = CoroutineScope(coroutineContext)

    private val repository: LoginRepository = LoginRepository()


    private val loginResponseLiveData = MutableLiveData<LoginResponse>()

    private val refreshLoginLiveData = MutableLiveData<LoginResponse>()

    private val locationLiveData = MutableLiveData<Location>()

    private val trackingLocationList = MutableLiveData<List<Location>>()

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

    fun login(clientNumber: String, secretKey: String, deviceId: String) {
        scope.launch {
            val loginResponse = repository.login(clientNumber, secretKey, deviceId)
            loginResponseLiveData.postValue(loginResponse)
        }
    }

    fun getLoginLiveData() : MutableLiveData<LoginResponse> {
        return loginResponseLiveData
    }

    fun refreshToken(token: String?): MutableLiveData<LoginResponse> {
        scope.launch {
            val loginResponse = repository.refreshToken(token)
            refreshLoginLiveData.postValue(loginResponse)
        }
        return refreshLoginLiveData
    }

    fun saveCurrentLocation(location: Location, token: String?) : MutableLiveData<Location> {
        scope.launch {
            val locationResponse = repository.saveCurrentLocation(location, token)
            locationLiveData.postValue(locationResponse)
        }
        return locationLiveData
    }

    fun getAllTrackingPoints(token: String?) : MutableLiveData<List<Location>> {
        scope.launch {
            val locationListResponse = repository.getAllTrackingPoints(token)
            trackingLocationList.postValue(locationListResponse)
        }
        return trackingLocationList
    }

}