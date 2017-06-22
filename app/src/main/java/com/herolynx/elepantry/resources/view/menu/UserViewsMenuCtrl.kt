package com.herolynx.elepantry.resources.view.menu

import com.herolynx.elepantry.config.Config
import com.herolynx.elepantry.core.rx.observeOnDefault
import com.herolynx.elepantry.core.rx.subscribeOnDefault
import com.herolynx.elepantry.drive.DriveType
import com.herolynx.elepantry.drive.Drives
import com.herolynx.elepantry.repository.Repository
import com.herolynx.elepantry.resources.core.model.Resource
import com.herolynx.elepantry.resources.core.model.View
import com.herolynx.elepantry.resources.core.model.ViewType
import com.herolynx.elepantry.resources.core.service.DynamicResourceView
import com.herolynx.elepantry.resources.core.service.ResourceView

internal class UserViewsMenuCtrl(
        private val view: UserViewsMenu,
        private val userViews: Repository<View> = Config.repository.userViews(),
        private val resourceView: Repository<Resource> = Config.repository.userResources()
) {

    fun getResourceView(v: View): ResourceView = when (v.type) {
        ViewType.DYNAMIC -> DynamicResourceView(v, { resourceView.asObservable() })

        ViewType.GOOGLE -> Drives.drive(view, DriveType.GOOGLE_DRIVE).driveView()

        ViewType.DROP_BOX -> Drives.drive(view, DriveType.DROP_BOX).driveView()

        else -> throw UnsupportedOperationException("Unknown view type - view: $view")
    }

    fun getUserViews() = userViews
            .asObservable()
            .subscribeOnDefault()
            .observeOnDefault()


}
