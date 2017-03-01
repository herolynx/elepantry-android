package com.herolynx.elepantry.resources.view

import android.os.Bundle
import com.herolynx.elepantry.R
import com.herolynx.elepantry.resources.ResourceView
import com.herolynx.elepantry.resources.model.View
import com.herolynx.elepantry.resources.view.menu.UserViewsMenu

class ResourceTagsActivity : UserViewsMenu() {

    override val layoutWithMenuId = R.layout.resource_tags_view

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle("Add view")
    }

    override fun onViewChange(v: View, rv: ResourceView): Boolean {
        //TODO
        return false
    }
}