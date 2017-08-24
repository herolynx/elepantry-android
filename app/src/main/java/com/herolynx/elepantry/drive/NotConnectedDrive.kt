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

internal object NotConnectedDrive : CloudDrive {

    override fun driveView(): ResourceView = NotConnectedResourceView

    override fun type(): DriveType = DriveType.NOT_CONNECTED

    override fun cloudResource(r: Resource): Try<CloudResource> = Try { NotConnectedResource }
}

private object NotConnectedResource : CloudResource {

    override fun thumbnail(): Observable<InputStream> = Observable.empty()

    override fun metaInfo(): Resource = Resource()

    override fun preview(activity: Activity, beforeAction: () -> Unit, afterAction: () -> Unit): Try<Result> =
            Try { Result(false, activity.getString(R.string.error_drive_not_connected)) }

    override fun download(activity: Activity, beforeAction: () -> Unit, afterAction: () -> Unit): Try<Result> =
            Try { Result(false, activity.getString(R.string.error_drive_not_connected)) }

}

private object NotConnectedResourceView : ResourceView {

    override fun search(c: SearchCriteria): Try<out ResourcePage> = Try.Failure(RuntimeException("Drive is not connected"))

}