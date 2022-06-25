package com.mmgsoft.modules.libs.base

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlin.reflect.KProperty1

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

    protected fun<S> findIndex(item: T, compare: KProperty1<T, S>): Int {
        var pos = -1
        for(i in 0 until mData.size) {
            if(compare.get(mData[i]) == compare.get(item)) {
                pos = i
                break
            }
        }
        return pos
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        onBindView(holder.itemView, mData[position], position)
    }

    override fun onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>) {
        if(payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            onBindViewPayloadHolder(holder, position, payloads[0])
        }
    }

    open protected fun onBindViewPayloadHolder(holder: VH, position: Int, any: Any) {}

    abstract fun onHandleCreateViewHolder(itemView: View): VH

    abstract fun onBindView(itemView: View, item: T, position: Int)

    override fun getItemCount(): Int = mData.size
}