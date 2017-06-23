package com.herolynx.elepantry.ext.google.drive

import android.app.Activity
import android.net.Uri
import com.herolynx.elepantry.core.Result
import com.herolynx.elepantry.core.net.asInputStream
import com.herolynx.elepantry.core.ui.WebViewUtils
import com.herolynx.elepantry.drive.CloudResource
import com.herolynx.elepantry.resources.core.model.Resource
import org.funktionale.tries.Try
import rx.Observable
import java.io.InputStream

class GoogleDriveResource(private val metaInfo: Resource) : CloudResource {

    override fun preview(activity: Activity, beforeAction: () -> Unit, afterAction: () -> Unit): Try<Result> =
            WebViewUtils.openLink(activity, metaInfo.downloadLink)

    override fun thumbnail(): Observable<InputStream> = Uri.parse(metaInfo.thumbnailLink)
            .asInputStream()

    override fun metaInfo(): Resource = metaInfo
}