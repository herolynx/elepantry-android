package com.herolynx.elepantry.resources.view.menu

import com.google.android.gms.common.api.GoogleApiClient
import com.herolynx.elepantry.auth.SignInUseCase
import com.herolynx.elepantry.auth.view.SignInActivity
import com.herolynx.elepantry.config.Config
import com.herolynx.elepantry.core.repository.Repository
import com.herolynx.elepantry.core.rx.observe
import com.herolynx.elepantry.core.rx.schedule
import com.herolynx.elepantry.core.ui.navigation.navigateTo
import com.herolynx.elepantry.ext.google.GoogleApi
import com.herolynx.elepantry.ext.google.asyncConnect
import com.herolynx.elepantry.ext.google.drive.GoogleDriveView
import com.herolynx.elepantry.resources.ResourceView
import com.herolynx.elepantry.resources.dynamic.DynamicResourceView
import com.herolynx.elepantry.resources.model.Resource
import com.herolynx.elepantry.resources.model.View

internal class UserViewsMenuCtrl(
        private val view: UserViewsMenu,
        private val userViews: Repository<View> = Config.repository.userViews(),
        private val resourceView: Repository<Resource> = Config.repository.userResources()
) {

    fun getGoogleDriveView() = GoogleDriveView.create(view).get()

    fun getView(v: View): ResourceView = DynamicResourceView(v, { resourceView.asObservable() })

    fun logOut(api: GoogleApiClient = GoogleApi.build(view)) {
        api.asyncConnect()
                .flatMap { api -> SignInUseCase.logOut(api) }
                .schedule()
                .observe()
                .subscribe { s ->
                    api.disconnect()
                    view.navigateTo(SignInActivity::class.java)
                }
    }

    fun getUserViews() = userViews
            .asObservable()
            .schedule()
            .observe()


}
