package com.herolynx.elepantry.resources.view

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.ui.recyclerview.ListAdapter
import com.herolynx.elepantry.resources.Resource

object ResourceList {

    fun adapter(): ListAdapter<Resource, ResourceItemView> =
            ListAdapter<Resource, ResourceItemView>(
                    { ctx -> ResourceItemView(ctx) },
                    { r, h -> display(r, h) }
            )

    fun display(r: Resource?, h: ListAdapter.ViewHolder<ResourceItemView>) {
        h.view.name.text = r?.name
    }

}

class ResourceItemView(ctx: Context) : LinearLayout(ctx) {

    init {
        LayoutInflater.from(context).inflate(R.layout.resource_list_item, this)
    }

    val name = findViewById(R.id.resource_item_name) as TextView

}

