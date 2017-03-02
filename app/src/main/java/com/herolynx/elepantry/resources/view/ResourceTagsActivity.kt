package com.herolynx.elepantry.resources.view

import android.os.Bundle
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.ui.navigation.navigateTo
import com.herolynx.elepantry.resources.ResourceView
import com.herolynx.elepantry.resources.model.View
import com.herolynx.elepantry.resources.view.menu.UserViewsMenu

class ResourceTagsActivity : UserViewsMenu() {

    override val layoutId = R.layout.resource_tags

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(getString(R.string.add_view))
    }

    override fun onViewChange(v: View, rv: ResourceView): Boolean {
        navigateTo(ResourcesActivity::class.java)
        return true
    }
}