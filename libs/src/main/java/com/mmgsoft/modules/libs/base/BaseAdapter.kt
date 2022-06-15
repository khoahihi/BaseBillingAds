package com.mmgsoft.modules.libs.base

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T, VH: RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
    abstract val layoutResId: Int
    protected val mData = mutableListOf<T>()

    protected fun getItem(position: Int) = mData[position]

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newData: List<T>) {
        mData.clear()
        mData.addAll(newData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return onHandleCreateViewHolder(
            LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        onBindView(holder.itemView, mData[position], position)
    }

    abstract fun onHandleCreateViewHolder(itemView: View): VH

    abstract fun onBindView(itemView: View, item: T, position: Int)

    override fun getItemCount(): Int = mData.size
}