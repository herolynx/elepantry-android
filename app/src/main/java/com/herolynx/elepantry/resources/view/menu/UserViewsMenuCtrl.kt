package com.herolynx.elepantry.resources.view.menu

import com.google.android.gms.common.api.GoogleApiClient
import com.herolynx.elepantry.auth.SignInUseCase
import com.herolynx.elepantry.auth.view.SignInActivity
import com.herolynx.elepantry.config.Config
import com.herolynx.elepantry.core.rx.observeOnUi
import com.herolynx.elepantry.core.rx.subscribeOnDefault
import com.herolynx.elepantry.core.ui.navigation.navigateTo
import com.herolynx.elepantry.drive.Drives
import com.herolynx.elepantry.ext.google.GoogleApi
import com.herolynx.elepantry.ext.google.asyncConnect
import com.herolynx.elepantry.repository.Repository
import com.herolynx.elepantry.resources.core.model.Resource
import com.herolynx.elepantry.resources.core.model.View
import com.herolynx.elepantry.resources.core.model.ViewType
import com.herolynx.elepantry.resources.core.service.DynamicResourceView
import com.herolynx.elepantry.resources.core.service.ResourceView
import com.herolynx.elepantry.resources.view.tags.ResourceTagsActivity

internal class UserViewsMenuCtrl(
        private val view: UserViewsMenu,
        private val userViews: Repository<View> = Config.repository.userViews(),
        private val resourceView: Repository<Resource> = Config.repository.userResources()
) {

    fun getResourceView(v: View): ResourceView = when (v.type) {
        ViewType.DYNAMIC -> DynamicResourceView(v, { resourceView.asObservable() })

        else -> Drives.drive(view, v.type.driveType()).driveView()
    }

    fun getUserViews() = userViews
            .asObservable()
            .subscribeOnDefault()
            .observeOnUi()

    fun handleAddNewView() {
        ResourceTagsActivity.navigateNewView(view)
    }

    fun handleSignOutAction(api: GoogleApiClient = GoogleApi.build(view)) {
        api.asyncConnect()
                .flatMap { api -> SignInUseCase.logOut(api) }
                .subscribeOnDefault()
                .observeOnUi()
                .subscribe { _ ->
                    api.disconnect()
                    view.navigateTo(SignInActivity::class.java)
                }
    }

}
