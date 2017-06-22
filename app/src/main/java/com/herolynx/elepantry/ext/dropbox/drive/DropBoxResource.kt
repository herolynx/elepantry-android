package com.herolynx.elepantry.ext.dropbox.drive

import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.ThumbnailSize
import com.herolynx.elepantry.drive.CloudResource
import com.herolynx.elepantry.resources.core.model.Resource
import rx.Observable
import java.io.InputStream

class DropBoxResource(
        private val metaInfo: Resource,
        private val client: DbxClientV2
) : CloudResource {

    override fun thumbnail(): rx.Observable<InputStream> = Observable.defer {
        Observable.just(client.files()
                .getThumbnailBuilder(metaInfo.thumbnailLink)
                .withSize(ThumbnailSize.W640H480)
                .start()
                .inputStream
        )
    }

    override fun metaInfo(): Resource = metaInfo
}