package com.herolynx.elepantry.ext.dropbox.drive

import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.ThumbnailSize
import com.herolynx.elepantry.core.func.Retry
import com.herolynx.elepantry.core.log.warn
import com.herolynx.elepantry.drive.CloudResource
import com.herolynx.elepantry.resources.core.model.Resource
import org.funktionale.tries.Try
import rx.Observable
import java.io.InputStream

class DropBoxResource(
        private val metaInfo: Resource,
        private val client: DbxClientV2
) : CloudResource {

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