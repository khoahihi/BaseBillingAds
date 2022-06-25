package com.mmgsoft.modules.libs.base

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mmgsoft.modules.libs.dialog.AlertMessageDialog

abstract class BaseActivity : AppCompatActivity() {
    abstract val layoutResId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResId)
        initViews()
    }

    abstract fun initViews()

    protected fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    protected fun showAlertMessage(msg: String) {
        AlertMessageDialog(this, msg).show()
    }
}