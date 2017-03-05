package com.herolynx.elepantry.resources.view.menu

import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.ui.recyclerview.ListAdapter
import com.herolynx.elepantry.resources.model.View

object UserViewsList {

    fun adapter(
            clickHandler: (View) -> Unit,
            editHandler: (View) -> Unit
    ): ListAdapter<View, UserViewItem> =
            ListAdapter(
                    { ctx -> UserViewItem(ctx) },
                    { r, h -> display(r, h, clickHandler, editHandler) }
            )

    fun display(
            v: View?,
            h: ListAdapter.ViewHolder<UserViewItem>,
            clickHandler: (View) -> Unit,
            editHandler: (View) -> Unit
    ) {
        h.view.viewButton.text = v?.name
        h.view.viewButton.setOnClickListener { if (v != null) clickHandler(v) }
        h.view.editButton.setOnClickListener { if (v != null) editHandler(v) }
    }

}

class UserViewItem(ctx: Context) : LinearLayout(ctx) {

    init {
        LayoutInflater.from(context).inflate(R.layout.menu_user_views_item, this)
    }

    val viewButton = findViewById(R.id.user_view_button) as Button
    val editButton = findViewById(R.id.user_view_edit_button) as Button

}
