package com.herolynx.elepantry.ext.dropbox.drive

import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.http.OkHttp3Requestor
import com.dropbox.core.v2.DbxClientV2
import com.herolynx.elepantry.drive.CloudDrive
import com.herolynx.elepantry.drive.CloudResource
import com.herolynx.elepantry.drive.DriveType
import com.herolynx.elepantry.ext.dropbox.auth.DropBoxSession
import com.herolynx.elepantry.getAuthContext
import com.herolynx.elepantry.resources.core.model.Resource
import org.funktionale.option.Option
import org.funktionale.option.toOption
import org.funktionale.tries.Try
import rx.Observable

class DropBoxDrive(private val client: DbxClientV2, private val session: DropBoxSession) : CloudDrive {

    override fun driveView(): DropBoxView = DropBoxView(client)

    override fun type(): DriveType = DriveType.DROP_BOX

    override fun cloudResource(r: Resource): Try<CloudResource> = when (r.type) {

        DriveType.DROP_BOX -> org.funktionale.tries.Try.Success(DropBoxResource(client = client, metaInfo = r, session = session))

        else -> Try.Failure(IllegalArgumentException("Not DropBox resource: $r"))

    }

    companion object {

        fun create(session: DropBoxSession): com.herolynx.elepantry.ext.dropbox.drive.DropBoxDrive {
            val requestConfig = DbxRequestConfig.newBuilder(com.herolynx.elepantry.config.Config.elepantryUrl)
                    .withHttpRequestor(OkHttp3Requestor(com.dropbox.core.http.OkHttp3Requestor.defaultOkHttpClient()))
                    .build()
            return DropBoxDrive(DbxClientV2(requestConfig, session.token), session)
        }

        fun create(a: android.app.Activity): Option<rx.Observable<DropBoxDrive>> = a.getAuthContext()
                .flatMap { c -> c.dropBoxSession.toOption() }
                .map { s -> Observable.just(DropBoxDrive.create(s)) }

    }

}