package com.mmgsoft.mmgbaseadsmodule

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mmgsoft.mmgbaseadsmodule.BaseModuleApplication
import com.mmgsoft.mmgbaseadsmodule.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
//        BaseModuleApplication.instance.adsManager.forceShowInterstitial(this, getString(R.string.splash_id)) {
//            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
//        }
    }
}