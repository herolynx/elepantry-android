package com.herolynx.elepantry.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

class ListAdapter<T, TU : View>(
        val viewFactory: (Context) -> TU,
        val display: (T?, ListAdapter.ViewHolder<TU>) -> Unit,
        val mItems: MutableList<T> = mutableListOf()
)
    : RecyclerView.Adapter<ListAdapter.ViewHolder<TU>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<TU> {
//        val layoutInflater = LayoutInflater.from(parent.context)
//        return ViewHolder(layoutInflater.inflate(layoutId, parent, false) as TU)
        return ViewHolder(viewFactory(parent.context))
    }

    override fun onBindViewHolder(holder: ViewHolder<TU>, position: Int) {
        val item = mItems[position]
        display(item, holder)
    }

    override fun getItemCount(): Int = mItems.size

    class ViewHolder<out TU : View>(val view: TU) : RecyclerView.ViewHolder(view)

    fun add(t: T) {
        mItems.add(t)
    }

}