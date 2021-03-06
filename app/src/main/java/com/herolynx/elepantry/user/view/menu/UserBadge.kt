package com.herolynx.elepantry.user.view.menu

import android.support.v4.app.FragmentActivity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.net.asInputStream
import com.herolynx.elepantry.core.ui.image.download
import com.herolynx.elepantry.getAuthContext
import com.herolynx.elepantry.user.model.User
import org.funktionale.option.Option
import org.funktionale.option.toOption

class UserBadge(
        val layout: LinearLayout,
        val activity: FragmentActivity
) {

    val userName = layout.findViewById(R.id.menu_user_name) as TextView
    val userImage = layout.findViewById(R.id.menu_user_picture) as ImageView

    fun display() {
        activity.getAuthContext().map { c -> c.user.toOption().map { u -> display(u) } }
    }

    fun display(user: User) {
        debug("[UserBadge] Displaying user info: $user")
        userName.text = user.displayName
        userImage.download(
                inStream = user.photoUrl.asInputStream(),
                parentId = Option.Some(user.id),
                parentIdGetter = { Option.Some(user.id) }
        )
    }

    companion object {

        fun create(a: FragmentActivity, vg: ViewGroup): UserBadge {
            val ll = a.layoutInflater.inflate(R.layout.menu_user_badge, vg, false) as LinearLayout
            return UserBadge(ll, a)
        }

    }

}

