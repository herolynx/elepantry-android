package com.herolynx.elepantry.ext.google.drive

import android.app.Activity
import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.ext.google.GoogleConfig
import com.herolynx.elepantry.getAuthContext
import com.herolynx.elepantry.resources.core.service.ResourcePage
import com.herolynx.elepantry.resources.core.service.ResourceView
import com.herolynx.elepantry.resources.core.service.SearchCriteria
import org.funktionale.option.Option
import org.funktionale.option.toOption
import org.funktionale.tries.Try

class GoogleDriveView(private val service: Drive) : ResourceView {

    override fun search(c: SearchCriteria): Try<out ResourcePage> = GoogleDrivePage.create { nextPageToken ->
        debug("[GoogleDriveView] Search - criteria: $c")
        service.files()
                .list()
                .setFields(DOWNLOAD_FIELDS)
                .setQ(String.format("$QUERY_BY_NAME and $QUERY_NOT_DIRECTORY and $QUERY_NOT_TRASHED", c.text ?: ""))
                .setPageSize(c.pageSize)
                .setSpaces("$SPACE_DRIVE,$SPACE_PHOTOS")
                .setPageToken(nextPageToken)
    }

    companion object Factory {

        private val DOWNLOAD_FIELDS = "nextPageToken, files(id,name,mimeType,createdTime,modifiedTime,webContentLink,webViewLink,thumbnailLink,iconLink)"
        private val QUERY_BY_NAME = "name contains '%s'"
        private val QUERY_NOT_DIRECTORY = "mimeType != 'application/vnd.google-apps.folder'"
        private val QUERY_NOT_TRASHED = "trashed=false"

        private val SPACE_DRIVE = "drive"
        private val SPACE_PHOTOS = "photos"

        private val HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport()
        private val JSON_FACTORY = JacksonFactory.getDefaultInstance()

        fun create(account: GoogleSignInAccount, c: Context): GoogleDriveView {
            val credential = GoogleAccountCredential.usingOAuth2(
                    c,
                    setOf(GoogleConfig.DRIVE_READONLY_API_URL, GoogleConfig.PHOTOS_READONLY_API_URL)
            )
            credential.setSelectedAccount(account.account)
            return GoogleDriveView(Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).build())
        }

        fun create(activity: Activity): Option<GoogleDriveView> {
            return activity.getAuthContext()
                    .flatMap { a -> a.googleAccount.toOption() }
                    .map { acc -> create(acc, activity) }
        }

    }

}
