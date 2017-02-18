package com.herolynx.elepantry.menu

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.net.download
import com.herolynx.elepantry.core.rx.observe
import com.herolynx.elepantry.core.rx.schedule
import com.herolynx.elepantry.user.model.User

class UserBadge(v: ViewGroup) {

    val userName = v.findViewById(R.id.menu_user_name) as TextView
    val userImage = v.findViewById(R.id.menu_user_picture) as ImageView

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
}

