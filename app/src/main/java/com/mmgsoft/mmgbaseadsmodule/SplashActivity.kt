package com.mmgsoft.mmgbaseadsmodule

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ExampleAdsComponentsApplication.instance.adsComponents.adsManager.forceShowInterstitial(this, getString(R.string.splash_id)) {
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }
}