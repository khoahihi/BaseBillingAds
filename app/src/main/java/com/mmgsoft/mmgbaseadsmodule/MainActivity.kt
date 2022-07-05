package com.mmgsoft.mmgbaseadsmodule

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mmgsoft.modules.libs.activity.ChangeBackgroundActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnBuyItem1.setOnClickListener {
            startActivity(Intent(this, TestBackgroundActivity::class.java))
        }

        btnChangeBackground.setOnClickListener {
            ChangeBackgroundActivity.open(this)
        }
    }
}