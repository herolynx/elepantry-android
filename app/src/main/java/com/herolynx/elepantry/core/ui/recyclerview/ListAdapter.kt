package com.herolynx.elepantry.core.ui.recyclerview

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

/**
 * Adapter for UI list
 * @param viewFactory factory provide UI list items for view holder
 * @param display decorator for displaying data on UI list item
 * @param items current items in adapter (must be mutable due to android recycler view)
 */
class ListAdapter<T, TU : View>(
        val viewFactory: (Context) -> TU,
        val display: (T?, ViewHolder<TU>) -> Unit,
        val items: MutableList<T> = mutableListOf()
)
    : RecyclerView.Adapter<ListAdapter.ViewHolder<TU>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<TU> = ViewHolder(viewFactory(parent.context))

    override fun onBindViewHolder(holder: ViewHolder<TU>, position: Int) = display(items[position], holder)

    override fun getItemCount(): Int = items.size

    class ViewHolder<out TU : View>(val view: TU) : RecyclerView.ViewHolder(view)

    fun add(t: T) {
        items.add(t)
    }

}