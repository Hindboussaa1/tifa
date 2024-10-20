package com.restaurant.healthirestaurant.application

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.FirebaseApp
import com.restaurant.healthirestaurant.utils.SharedPreferencesManager


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initValues()
    }

    private fun initValues() {
        FirebaseApp.initializeApp(this)
        SharedPreferencesManager.initialize(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}