package com.example.android.myapplication.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.example.android.myapplication.model.LoginResponse
import com.example.android.myapplication.repository.LoginRepository
import com.example.android.myapplication.service.ApiFactory
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class LoginViewModel : ViewModel() {
    private val clientId: String = "af875b1c-27f3-4de4-ad05-7adf4d9f1798"

    private val subscriptionKey: String = "this_is_subscription_key"

    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default

    private val scope = CoroutineScope(coroutineContext)

    private val repository: LoginRepository = LoginRepository(ApiFactory.authApi)


    val loginResponseLiveData = MutableLiveData<LoginResponse>()

    fun login() {
        scope.launch {
            val loginResponse = repository.login(clientId, subscriptionKey)
            loginResponseLiveData.postValue(loginResponse)
        }
    }


    fun cancelAllRequests() = coroutineContext.cancel()

}