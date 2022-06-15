package com.mmgsoft.modules.libs.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapterAutoViewHolder<T> : BaseAdapter<T, BaseAdapterAutoViewHolder<T>.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onHandleCreateViewHolder(itemView: View): ViewHolder {
        return ViewHolder(itemView)
    }
}