package com.mmgsoft.modules.libs.adapters

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.graphics.BitmapCompat
import androidx.core.view.doOnLayout
import androidx.lifecycle.LifecycleCoroutineScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.card.MaterialCardView
import com.mmgsoft.modules.libs.R
import com.mmgsoft.modules.libs.base.BaseAdapterAutoViewHolder
import com.mmgsoft.modules.libs.etx.afterMeasured
import com.mmgsoft.modules.libs.manager.AssetManager
import com.mmgsoft.modules.libs.models.Background
import kotlinx.android.synthetic.main.item_background.view.*
import kotlinx.coroutines.*
import java.util.concurrent.Executors

const val UPDATE_WAS_PAID = "UPDATE_WAS_PAID"

class BackgroundAdapter(
    private val lifecycleScope: LifecycleCoroutineScope,
    private val onItemClicked: (Background) -> Unit
) : BaseAdapterAutoViewHolder<Background>() {
    override val layoutResId: Int
        get() = R.layout.item_background

    fun updateBilling(background: Background) {
        val index = findIndex(background, Background::productId)
        if (index != -1) {
            getItem(index).isBuy = true
            notifyItemChanged(index)
        }
    }

    fun updateSelected(background: Background) {
        mData.find { it.isSelected }?.let {
            updateSelected(it, false)
        }
        updateSelected(background, true)
    }

    fun updateSelected(background: Background, isPaid: Boolean) {
        val index = findIndex(background, Background::productId)
        if (index != -1) {
            getItem(index).isSelected = isPaid
            notifyItemChanged(index, UPDATE_WAS_PAID)
        }
    }

    override fun onBindViewPayloadHolder(holder: ViewHolder, position: Int, any: Any) {
        when (any) {
            UPDATE_WAS_PAID -> updateItemWasPaid(holder.itemView, getItem(position))
        }
    }

    override fun onBindView(itemView: View, item: Background, position: Int) {
        itemView.apply {

            item.bm?.let {
                visibleAndLoadImage(this, it)
            } ?: run {
                progress.visibility = View.VISIBLE
                imBackground.visibility = View.INVISIBLE
                if (item.isTriggerLoadBitmap) {
                    return@run
                }
                item.isTriggerLoadBitmap = true
                imBackground.afterMeasured {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val bitmap = AssetManager.loadBitmap(
                            item.backgroundPath,
                            imBackground.width,
                            imBackground.height
                        )
                        if (bitmap != null) {
                            item.bm = bitmap
                            withContext(Dispatchers.Main) {
                                visibleAndLoadImage(itemView, bitmap)
                            }
                        }
                    }
                }
            }
            findViewById<MaterialCardView>(R.id.cardImage).setOnClickListener {
                onItemClicked.invoke(item)
            }
            val tvBuyStatus = findViewById<TextView>(R.id.tvSelect)
            tvBuyStatus.text =
                if (item.isBuy) context.getString(R.string.select) else context.getString(R.string.buy)

            updateItemWasPaid(this, item)
        }
    }

    private fun visibleAndLoadImage(itemView: View, bm: Bitmap) {
        with(itemView) {
            val imBackground = findViewById<ImageView>(R.id.imBackground)
            val progress = findViewById<ProgressBar>(R.id.progress)
            progress.visibility = View.GONE
            imBackground.visibility = View.VISIBLE
            imBackground.setImageBitmap(bm)
        }
    }

    private fun updateItemWasPaid(itemView: View, item: Background) {
        itemView.apply {
            val tvBuyStatus = findViewById<TextView>(R.id.tvSelect)
            val imSelect = findViewById<ImageView>(R.id.imTick)

            if (item.isSelected) {
                imSelect.visibility = View.VISIBLE
                tvBuyStatus.visibility = View.GONE
            } else {
                imSelect.visibility = View.GONE
                tvBuyStatus.visibility = View.VISIBLE
            }
        }
    }
}