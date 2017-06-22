package com.herolynx.elepantry.ext.dropbox.drive

import com.dropbox.core.v2.DbxClientV2
import com.herolynx.elepantry.auth.Token
import com.herolynx.elepantry.drive.CloudDrive
import com.herolynx.elepantry.drive.CloudResource
import com.herolynx.elepantry.drive.DriveType
import com.herolynx.elepantry.resources.core.model.Resource
import com.herolynx.elepantry.resources.core.service.ResourceView
import org.funktionale.tries.Try

class DropBoxDrive(private val client: DbxClientV2) : CloudDrive {

    override fun driveView(): ResourceView = DropBoxView(client)

    override fun type(): DriveType = DriveType.DROP_BOX

    override fun cloudResource(r: Resource): Try<CloudResource> = when (r.type) {

        DriveType.DROP_BOX -> org.funktionale.tries.Try.Success(DropBoxResource(client = client, metaInfo = r))

        else -> Try.Failure(IllegalArgumentException("Not DropBox resource: $r"))

    }

    companion object {

        fun create(token: Token): com.herolynx.elepantry.ext.dropbox.drive.DropBoxDrive {
            val requestConfig = com.dropbox.core.DbxRequestConfig.newBuilder(com.herolynx.elepantry.config.Config.elepantryUrl)
                    .withHttpRequestor(com.dropbox.core.http.OkHttp3Requestor(com.dropbox.core.http.OkHttp3Requestor.defaultOkHttpClient()))
                    .build()
            return com.herolynx.elepantry.ext.dropbox.drive.DropBoxDrive(com.dropbox.core.v2.DbxClientV2(requestConfig, token))
        }

        fun create(a: android.app.Activity): rx.Observable<DropBoxDrive> = com.herolynx.elepantry.ext.dropbox.auth.DropBoxAuth.getToken(a)
                .map { token ->
                    com.herolynx.elepantry.ext.dropbox.drive.DropBoxDrive.Companion.create(token)
                }


    }

}