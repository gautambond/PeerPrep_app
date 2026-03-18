package com.bond.peerprep

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cloudinary.android.MediaManager
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
          FirebaseApp.initializeApp(this)

        // ✅ Initialize Cloudinary
        val config = mapOf(
            "cloud_name" to "*******",
            "api_key"    to "************",
            "api_secret" to "********************"
        )
        MediaManager.init(this, config)

        setContentView(R.layout.activity_main)
    }
}
