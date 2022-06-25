package com.mmgsoft.modules.libs.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.mmgsoft.modules.libs.R

class AlertMessageDialog(
    private val ctx: Context,
    private val msg: String
) : Dialog(ctx, R.style.Theme_Dialog) {
    private val tvDescription: TextView by lazy {
        findViewById(R.id.tvDescription)
    }

    private val btnClose: MaterialCardView by lazy {
        findViewById(R.id.btnClose)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.dialog_message_alert)
        tvDescription.text = msg
        btnClose.setOnClickListener {
            dismiss()
        }
    }
}