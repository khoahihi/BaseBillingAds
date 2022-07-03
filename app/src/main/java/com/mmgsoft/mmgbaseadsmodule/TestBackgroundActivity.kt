package com.mmgsoft.mmgbaseadsmodule

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mmgsoft.modules.libs.amzbiling.AmazonIapActivity
import com.mmgsoft.modules.libs.manager.BackgroundManager
import kotlinx.android.synthetic.main.activity_test_background.*

class TestBackgroundActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_background)
        btnNext.setOnClickListener {
            AmazonIapActivity.open(this)
        }
    }

    override fun onResume() {
        super.onResume()
        BackgroundManager.loadBackground(this)
    }
}