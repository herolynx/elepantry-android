package com.herolynx.elepantry.resources.view.tags

import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.ui.recyclerview.ListAdapter
import com.herolynx.elepantry.resources.core.model.Tag

internal object TagsList {

    fun adapter(deleteHandler: (Tag) -> Unit): ListAdapter<Tag, TagItemView> =
            ListAdapter<Tag, TagItemView>(
                    { ctx -> TagItemView(ctx) },
                    { r, h -> display(r, h, deleteHandler) }
            )

    fun display(tag: Tag?, h: ListAdapter.ViewHolder<TagItemView>, deleteHandler: (Tag) -> Unit) {
        h.view.name.text = tag?.name
        h.view.buttonDelete.setOnClickListener {
            deleteHandler(tag!!)
        }
    }

}

internal class TagItemView(ctx: Context) : LinearLayout(ctx) {

    init {
        LayoutInflater.from(context).inflate(R.layout.resources_tag_item, this)
    }

    val name = findViewById(R.id.tag_name) as TextView
    val buttonDelete = findViewById(R.id.button_delete_tag) as Button

}

