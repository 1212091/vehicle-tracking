package com.example.android.myapplication

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.telephony.TelephonyManager
import com.example.android.myapplication.databinding.ActivityLoginBinding
import com.example.android.myapplication.viewmodel.LoginViewModel
import android.content.Intent
import android.view.View
import com.example.android.myapplication.AppConstants.TOKEN


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private lateinit var loginViewModel: LoginViewModel

    private val PERMISSION_CODE: Int = 504

    private var token: String? = null

    private var deviceId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        //setUpView()
        requestPermission()
        addListener()
    }

    private fun setUpView() {
        binding.clientId.setText(getString(R.string.client_id_value))
        binding.subscriptionKey.setText(getString(R.string.secret_key_value))
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), PERMISSION_CODE)
    }

    private fun addListener() {
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        loginViewModel.getLoginLiveData().observe(this, Observer {
            binding.loadingView.visibility = View.INVISIBLE
            if (it != null) {
                token = it.token
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra(TOKEN, token)
                }
                startActivity(intent)
            } else {
                binding.wrongPassword.visibility = View.VISIBLE
            }
        })
        binding.login.setOnClickListener {
            binding.loadingView.visibility = View.VISIBLE
            binding.wrongPassword.visibility = View.INVISIBLE
            loginViewModel.login(binding.clientId.text.toString(), binding.subscriptionKey.text.toString(), deviceId!!)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    val telephonyManager = baseContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                    deviceId = telephonyManager.imei
                }
                return
            }
        }
    }
}