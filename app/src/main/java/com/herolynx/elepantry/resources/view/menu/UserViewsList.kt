package com.herolynx.elepantry.resources.view.menu

import com.herolynx.elepantry.core.ui.recyclerview.ListAdapter
import com.herolynx.elepantry.resources.model.Resource
import com.herolynx.elepantry.resources.view.ui.ResourceItemView

object UserViewAdapter {

    fun adapter(): ListAdapter<Resource, ResourceItemView> =
            ListAdapter<Resource, ResourceItemView>(
                    { ctx -> ResourceItemView(ctx) },
                    { r, h -> display(r, h) }
            )

    fun display(r: Resource?, h: ListAdapter.ViewHolder<ResourceItemView>) {
        h.view.name.text = r?.name
    }

}

class UserViewItem(ctx: Context) : LinearLayout(ctx) {

    init {
        LayoutInflater.from(context).inflate(R.layout.resources_list_item, this)
    }

    val name = findViewById(R.id.resource_item_name) as TextView

}
