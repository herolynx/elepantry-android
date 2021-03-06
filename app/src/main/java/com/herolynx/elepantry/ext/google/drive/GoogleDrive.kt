package com.herolynx.elepantry.ext.google.drive

import android.app.Activity
import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.herolynx.elepantry.drive.CloudDrive
import com.herolynx.elepantry.drive.CloudResource
import com.herolynx.elepantry.drive.DriveType
import com.herolynx.elepantry.ext.google.GoogleConfig
import com.herolynx.elepantry.ext.google.sync.GoogleDriveMetaInfoSync
import com.herolynx.elepantry.getAuthContext
import com.herolynx.elepantry.resources.core.model.Resource
import org.funktionale.option.Option
import org.funktionale.option.toOption
import org.funktionale.tries.Try

class GoogleDrive(private val drive: Drive) : CloudDrive {

    override fun refresh(jobStatus: (Boolean) -> Unit): Option<() -> Unit> = Option.Some {
        val syncJob = GoogleDriveMetaInfoSync.create(driveView())
        syncJob.sync(jobStatus)
    }

    override fun driveView(): GoogleDriveView = GoogleDriveView(drive)

    override fun type(): DriveType = DriveType.GOOGLE_DRIVE

    override fun cloudResource(r: Resource): Try<CloudResource> = when (r.type) {

        DriveType.GOOGLE_DRIVE -> Try.Success(GoogleDriveResource(metaInfo = r, drive = drive))

        else -> Try.Failure(IllegalArgumentException("Not Google drive resource: $r"))

    }

    companion object {

        internal val APP_PACKAGE_NAME = "com.google.android.apps.docs"
        private val HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport()
        private val JSON_FACTORY = JacksonFactory.getDefaultInstance()

        fun create(account: GoogleSignInAccount, c: Context): GoogleDrive {
            val credential = GoogleAccountCredential.usingOAuth2(
                    c,
                    setOf(GoogleConfig.DRIVE_READONLY_API_URL, GoogleConfig.PHOTOS_READONLY_API_URL)
            )
            credential.setSelectedAccount(account.account)
            val drive = Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
            drive.applicationName = "elepantry"
            return GoogleDrive(drive.build())
        }

        fun create(activity: Activity): Option<GoogleDrive> {
            return activity.getAuthContext()
                    .flatMap { a -> a.googleAccount.toOption() }
                    .map { acc -> create(acc, activity) }
        }

    }

}