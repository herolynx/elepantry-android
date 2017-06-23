package com.herolynx.elepantry.ext.dropbox.drive

import android.app.Activity
import com.dropbox.core.android.DbxOfficialAppConnector
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.ThumbnailSize
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.Result
import com.herolynx.elepantry.core.func.Retry
import com.herolynx.elepantry.core.log.warn
import com.herolynx.elepantry.drive.CloudResource
import com.herolynx.elepantry.ext.dropbox.auth.DropBoxSession
import com.herolynx.elepantry.resources.core.model.Resource
import org.funktionale.tries.Try
import rx.Observable
import java.io.InputStream

class DropBoxResource(
        private val metaInfo: Resource,
        private val client: DbxClientV2,
        private val session: DropBoxSession
) : CloudResource {

    override fun preview(a: Activity): Try<Result> = Try {
        val dropBox = DbxOfficialAppConnector(session.uid)
        val openIntent = dropBox.getPreviewFileIntent(a, metaInfo.downloadLink, metaInfo.version)
        if (openIntent != null) {
            a.startActivity(openIntent)
            Result(true)
        } else {
            Result(success = false, errMsg = a.getString(R.string.error_dropbox_no_app))
        }
    }

    override fun thumbnail(): rx.Observable<InputStream> = Observable.defer {
        if (!metaInfo.isImageType()) {
            Observable.empty()
        } else {
            Retry.executeWithRetries(logic = {
                Try {
                    Observable.just(client.files()
                            .getThumbnailBuilder(metaInfo.thumbnailLink)
                            .withSize(ThumbnailSize.W640H480)
                            .start()
                            .inputStream
                    )
                }
            })
                    .onFailure { ex -> warn("[DropBox][Thumbnail] Couldn't get image - resource: $metaInfo", ex) }
                    .getOrElse { Observable.empty() }
        }
    }

    override fun metaInfo(): Resource = metaInfo
}