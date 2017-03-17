package com.herolynx.elepantry.core.ui.recyclerview

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.rx.DataEvent
import org.funktionale.option.Option

/**
 * Adapter for UI list
 * @param viewFactory factory provide UI list items for view holder
 * @param display decorator for displaying data on UI list item
 * @param items current items in adapter (must be mutable due to android recycler view)
 */
class ListAdapter<T, TU : View>(
        val viewFactory: (Context) -> TU,
        val display: (T?, ViewHolder<TU>) -> Unit,
        val sortBy: Option<(T) -> String> = Option.None,
        val items: MutableList<T> = mutableListOf(),
        val selectedItems: MutableList<T> = mutableListOf(),
        val selectedUIItems: MutableList<View> = mutableListOf()
)
    : RecyclerView.Adapter<ListAdapter.ViewHolder<TU>>() {

    private var selectedItemsChange: (List<T>) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<TU> = ViewHolder(viewFactory(parent.context))

    override fun onBindViewHolder(holder: ViewHolder<TU>, position: Int) {
        val i = items[position]
        holder.view.setOnLongClickListener { v ->
            v.isSelected = select(i, v)
            true
        }
        display(i, holder)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder<out TU : View>(val view: TU) : RecyclerView.ViewHolder(view)

    fun add(t: DataEvent<T>) {
        if (t.deleted) {
            items.remove(t.data)
        } else if (items.contains(t.data)) {
            items.remove(t.data)
            items.add(t.data)
        } else {
            items.add(t.data)
        }
    }

    fun add(t: T) {
        items.add(t)
    }

    fun size() = items.size

    fun sort() {
        sortBy.map { l -> items.sortBy(l) }
    }

    fun clear() {
        items.clear()
        clearSelected()
    }

    fun clearSelected() {
        selectedUIItems.map { v -> v.isSelected = false }
        selectedUIItems.clear()
        selectedItems.clear()
    }

    private fun select(t: T, v: View): Boolean {
        var isSelected: Boolean
        if (selectedItems.contains(t)) {
            debug("[ListAdapter] Item deselected: $t")
            selectedItems.remove(t)
            selectedUIItems.remove(v)
            isSelected = false
        } else {
            debug("[ListAdapter] Item selected: $t")
            selectedItems.add(t)
            selectedUIItems.add(v)
            isSelected = true
        }
        selectedItemsChange(selectedItems.toList())
        return isSelected
    }

    fun onSelectedItemsChange(selectedItemsChange: (List<T>) -> Unit) {
        this.selectedItemsChange = selectedItemsChange
    }
}

