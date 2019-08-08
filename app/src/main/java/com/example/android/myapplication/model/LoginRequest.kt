package com.example.android.myapplication.model

data class LoginRequest(
    val clientNumber: String,
    val secretKey: String,
    val deviceId: String
)