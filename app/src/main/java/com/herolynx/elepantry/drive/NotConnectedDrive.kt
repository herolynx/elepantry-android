package com.herolynx.elepantry.drive

import android.app.Activity
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.Result
import com.herolynx.elepantry.resources.core.model.Resource
import com.herolynx.elepantry.resources.core.service.ResourcePage
import com.herolynx.elepantry.resources.core.service.ResourceView
import com.herolynx.elepantry.resources.core.service.SearchCriteria
import org.funktionale.tries.Try
import rx.Observable
import java.io.InputStream

internal class NotConnectedDrive(private val driveType: DriveType) : CloudDrive {

    override fun driveView(): ResourceView = NotConnectedResourceView

    override fun type(): DriveType = driveType

    override fun cloudResource(r: Resource): Try<CloudResource> = Try { NotConnectedResource(r) }
}

private class NotConnectedResource(private val r: Resource) : CloudResource {

    override fun thumbnail(): Observable<InputStream> = Observable.empty()

    override fun metaInfo(): Resource = r

    private fun notSupportedOperation(activity: Activity): Try<Result> =
            Try { Result(false, activity.getString(R.string.error_drive_not_connected)) }

    override fun preview(activity: Activity, beforeAction: () -> Unit, afterAction: () -> Unit) = notSupportedOperation(activity)

    override fun download(activity: Activity, beforeAction: () -> Unit, afterAction: () -> Unit) = notSupportedOperation(activity)

}

private object NotConnectedResourceView : ResourceView {

    override fun search(c: SearchCriteria): Try<out ResourcePage> = Try.Failure(RuntimeException("Drive is not connected"))

}