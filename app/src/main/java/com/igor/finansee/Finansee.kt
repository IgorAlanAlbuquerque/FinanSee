package com.igor.finansee

import android.app.Application
import com.google.firebase.FirebaseApp

class FinanseeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}