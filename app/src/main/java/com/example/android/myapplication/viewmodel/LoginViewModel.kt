package com.example.android.myapplication.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.example.android.myapplication.model.GoogleMapResponse
import com.example.android.myapplication.model.LoginResponse
import com.example.android.myapplication.repository.LoginRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class LoginViewModel : ViewModel() {
    private val clientId: String = "af875b1c-27f3-4de4-ad05-7adf4d9f1798"

    private val subscriptionKey: String = "this_is_subscription_key"

    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default

    private val scope = CoroutineScope(coroutineContext)

    private val repository: LoginRepository = LoginRepository()


    private val loginResponseLiveData = MutableLiveData<LoginResponse>()

    private val refreshLoginLiveData = MutableLiveData<LoginResponse>()

    private val googleApiLiveData = MutableLiveData<GoogleMapResponse>()

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

    fun login(): MutableLiveData<LoginResponse> {
        scope.launch {
            val loginResponse = repository.login(clientId, subscriptionKey)
            loginResponseLiveData.postValue(loginResponse)
        }
        return loginResponseLiveData
    }

    fun refreshToken(token: String?): MutableLiveData<LoginResponse> {
        scope.launch {
            val loginResponse = repository.refreshToken(token)
            refreshLoginLiveData.postValue(loginResponse)
        }
        return refreshLoginLiveData
    }

    fun getGoogleApiData(mode: String, routingPreference: String, origin: String,
                         destination: String, apiKey: String): MutableLiveData<GoogleMapResponse> {
        scope.launch {
            val googleData = repository.getGoogleApiData(mode, routingPreference, origin, destination, apiKey)
            googleApiLiveData.postValue(googleData)
        }
        return googleApiLiveData
    }

}