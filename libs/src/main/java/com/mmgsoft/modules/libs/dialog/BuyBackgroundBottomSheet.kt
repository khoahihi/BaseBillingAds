package com.mmgsoft.modules.libs.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.card.MaterialCardView
import com.mmgsoft.modules.libs.R
import com.mmgsoft.modules.libs.manager.AssetManager
import com.mmgsoft.modules.libs.models.Background
import com.mmgsoft.modules.libs.utils.AdsComponentConfig

class BuyBackgroundBottomSheet(
    private val background: Background,
    private val onBuyBackground: (Background) -> Unit
) : BottomSheetDialogFragment() {
    private val tvPrice: TextView by lazy {
        requireView().findViewById(R.id.tvPrice)
    }

    private val tvDescription: TextView by lazy {
        requireView().findViewById(R.id.tvDescription)
    }

    private val imBackground: ImageView by lazy {
        requireView().findViewById(R.id.imBackground)
    }

    private val btnBuyNow: MaterialCardView by lazy {
        requireView().findViewById(R.id.btnBuyNow)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_bottom_sheet_buy_background, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        background.apply {
            tvPrice.text = "$price ${AdsComponentConfig.currency}"
            tvDescription.text = description
            AssetManager.loadBitmap(backgroundPath) {
                imBackground.setImageBitmap(it)
            }
        }

        btnBuyNow.setOnClickListener {
            onBuyBackground.invoke(background)
            dismiss()
        }

        btnBuyNow.visibility = if(background.isBuy) View.GONE else View.VISIBLE
    }
}