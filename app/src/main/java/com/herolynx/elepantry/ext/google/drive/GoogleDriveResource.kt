package com.herolynx.elepantry.ext.google.drive

import android.net.Uri
import com.herolynx.elepantry.core.net.asInputStream
import com.herolynx.elepantry.drive.CloudResource
import com.herolynx.elepantry.resources.core.model.Resource
import rx.Observable
import java.io.InputStream

class GoogleDriveResource(private val metaInfo: Resource) : CloudResource {

    override fun thumbnail(): Observable<InputStream> = Uri.parse(metaInfo.thumbnailLink)
            .asInputStream()

    override fun metaInfo(): Resource = metaInfo
}