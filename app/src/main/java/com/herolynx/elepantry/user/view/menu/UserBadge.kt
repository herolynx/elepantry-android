package com.herolynx.elepantry.user.view.menu

import android.support.v4.app.FragmentActivity
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.common.api.GoogleApiClient
import com.herolynx.elepantry.R
import com.herolynx.elepantry.auth.SignInUseCase
import com.herolynx.elepantry.auth.view.SignInActivity
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.rx.observe
import com.herolynx.elepantry.core.rx.schedule
import com.herolynx.elepantry.core.ui.image.download
import com.herolynx.elepantry.core.ui.navigation.navigateTo
import com.herolynx.elepantry.ext.google.GoogleApi
import com.herolynx.elepantry.ext.google.asyncConnect
import com.herolynx.elepantry.getAuthContext
import com.herolynx.elepantry.resources.view.tags.ResourceTagsActivity
import com.herolynx.elepantry.user.model.User
import org.funktionale.option.Option

class UserBadge(
        val layout: LinearLayout,
        val activity: FragmentActivity
) {

    val userName = layout.findViewById(R.id.menu_user_name) as TextView
    val userImage = layout.findViewById(R.id.menu_user_picture) as ImageView
    val newViewButton = layout.findViewById(R.id.add_new_view) as Button
    val signOutButton = layout.findViewById(R.id.sign_out) as Button

    fun initAddNewViewAction(preAction: () -> Unit = {}) {
        newViewButton.setOnClickListener {
            preAction()
            ResourceTagsActivity.navigateNewView(activity)
        }
    }

    fun initSignOutAction(api: GoogleApiClient = GoogleApi.build(activity)) {
        signOutButton.setOnClickListener {
            api.asyncConnect()
                    .flatMap { api -> SignInUseCase.logOut(api) }
                    .schedule()
                    .observe()
                    .subscribe { s ->
                        api.disconnect()
                        activity.navigateTo(SignInActivity::class.java)
                    }
        }
    }

    fun display() {
        activity.getAuthContext().map { c -> display(c.user!!) }
    }

    fun display(user: User) {
        debug("[UserBadge] Displaying user info: $user")
        userName.text = user.displayName
        userImage.download(uri = user.photoUrl, parentId = Option.Some(user.id), parentIdGetter = { Option.Some(user.id) })
    }

    companion object {

        fun create(a: FragmentActivity, vg: ViewGroup): UserBadge {
            val ll = a.layoutInflater.inflate(R.layout.menu_user_badge, vg, false) as LinearLayout
            return UserBadge(ll, a)
        }

    }

}

