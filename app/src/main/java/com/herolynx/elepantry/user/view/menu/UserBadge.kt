package com.herolynx.elepantry.user.view.menu

import android.app.Activity
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.net.download
import com.herolynx.elepantry.core.rx.observe
import com.herolynx.elepantry.core.rx.schedule
import com.herolynx.elepantry.getAuthContext
import com.herolynx.elepantry.resources.view.ResourceTagsActivity
import com.herolynx.elepantry.user.model.User

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
        user.photoUrl
                .download()
                .schedule()
                .observe()
                .subscribe { bitmap ->
                    userImage.setImageBitmap(bitmap)
                }

    }

    companion object {

        fun create(a: Activity, vg: ViewGroup): UserBadge {
            val ll = a.layoutInflater.inflate(R.layout.menu_user_badge, vg, false) as LinearLayout
            return UserBadge(ll, a)
        }

    }

}

