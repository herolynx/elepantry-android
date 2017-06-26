package com.herolynx.elepantry.ext.google.drive

import android.app.Activity
import android.net.Uri
import com.google.api.services.drive.Drive
import com.herolynx.elepantry.core.Result
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.net.asInputStream
import com.herolynx.elepantry.core.ui.WebViewUtils
import com.herolynx.elepantry.drive.CloudResource
import com.herolynx.elepantry.ext.android.AppManager
import com.herolynx.elepantry.ext.android.Storage
import com.herolynx.elepantry.resources.core.model.Resource
import org.funktionale.tries.Try
import rx.Observable
import java.io.InputStream

class GoogleDriveResource(
        private val metaInfo: Resource,
        private val drive: Drive
) : CloudResource {

    override fun preview(activity: Activity, beforeAction: () -> Unit, afterAction: () -> Unit): Try<Result> {
        if (AppManager.isAppInstalled(activity, GoogleDrive.APP_PACKAGE_NAME)) {
            return WebViewUtils.openLink(activity, metaInfo.downloadLink)
        } else {
            Storage.downloadAndOpen(
                    activity = activity,
                    fileName = metaInfo.uuid(),
                    download = { outputStream ->
                        debug("[GoogleDrive][Download] Downloading - resource: $metaInfo")
                        drive.files().get(metaInfo.id).executeMediaAndDownloadTo(outputStream)
                        -1L
                    },
                    beforeAction = beforeAction,
                    afterAction = afterAction
            )
            return Try.Success(Result(true))
        }
    }

    override fun thumbnail(): Observable<InputStream> = Uri.parse(metaInfo.thumbnailLink)
            .asInputStream()

    override fun metaInfo(): Resource = metaInfo
}