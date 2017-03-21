package com.herolynx.elepantry.user.view.menu

import android.app.Activity
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.ui.image.download
import com.herolynx.elepantry.getAuthContext
import com.herolynx.elepantry.resources.view.tags.ResourceTagsActivity
import com.herolynx.elepantry.user.model.User
import org.funktionale.option.Option

class UserBadge(
        val layout: LinearLayout,
        val activity: Activity
) {

    val userName = layout.findViewById(R.id.menu_user_name) as TextView
    val userImage = layout.findViewById(R.id.menu_user_picture) as ImageView
    val newViewButton = layout.findViewById(R.id.add_new_view) as Button

    fun initAddNewViewAction(preAction: () -> Unit = {}) {
        newViewButton.setOnClickListener {
            preAction()
            ResourceTagsActivity.navigateNewView(activity)
        }
    }

    fun display() {
        activity.getAuthContext().map { c -> display(c.user!!) }
    }

    fun display(user: User) {
        debug("[UserBadge] Displaying user info: %s", user)
        userName.text = user.displayName
        userImage.download(uri = user.photoUrl, parentId = Option.Some(user.id), parentIdGetter = { Option.Some(user.id) })
    }

    companion object {

        fun create(a: Activity, vg: ViewGroup): UserBadge {
            val ll = a.layoutInflater.inflate(R.layout.menu_user_badge, vg, false) as LinearLayout
            return UserBadge(ll, a)
        }

    }

}

