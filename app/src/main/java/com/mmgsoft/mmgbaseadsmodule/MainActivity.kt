package com.mmgsoft.mmgbaseadsmodule

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.mmgsoft.modules.libs.activity.ChangeBackgroundActivity
import com.mmgsoft.modules.libs.manager.AssetManager
import com.mmgsoft.modules.libs.manager.BackgroundManager
import com.mmgsoft.modules.libs.manager.MoneyManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val imBackground: ImageView by lazy {
        findViewById(R.id.imBackground)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        BackgroundManager.getBackground {
            AssetManager.loadBitmap(it.backgroundPath) {
                imBackground.setImageBitmap(it)
            }
        }

        btnBuyItem1.setOnClickListener {
            MoneyManager.buyBilling("100$", {}, {})
        }

        btnChangeBackground.setOnClickListener {
            startActivityForResult(Intent(this, ChangeBackgroundActivity::class.java), 111)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 111) {
            BackgroundManager.getBackground {
                AssetManager.loadBitmap(it.backgroundPath) {
                    imBackground.setImageBitmap(it)
                }
            }
        }
    }
}