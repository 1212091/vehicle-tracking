package com.example.android.myapplication.model

data class LoginResponse(
    val token: String,
    val issuedAtUtc: String,
    val expires: Long
)