package com.herolynx.elepantry.ext.google.drive

import android.app.Activity
import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.herolynx.elepantry.ext.google.GoogleConfig
import com.herolynx.elepantry.getAppContext
import org.funktionale.option.Option
import org.funktionale.option.toOption
import java.util.*

class GoogleDrive(private val service: Drive) {

    fun search(text: String = "", pageSize: Int = 30) = GoogleDriveSearch({ nextPageToken ->
        service.files()
                .list()
                .setQ(String.format(QUERY_BY_NAME, text))
                .setPageSize(pageSize)
                .setPageToken(nextPageToken)
    }, true)

    companion object Factory {

        private val QUERY_BY_NAME = "name contains '%s'"
        private val HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport()
        private val JSON_FACTORY = JacksonFactory.getDefaultInstance()

        fun create(account: GoogleSignInAccount, c: Context): GoogleDrive {
            val credential = GoogleAccountCredential.usingOAuth2(
                    c,
                    Collections.singleton(GoogleConfig.DRIVE_READONLY_API_URL)
            )
            credential.setSelectedAccount(account.account)
            return GoogleDrive(Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).build())
        }

        fun create(activity: Activity): Option<GoogleDrive> {
            return activity.getAppContext()
                    .flatMap { a -> a.googleAccount.toOption() }
                    .map { acc -> create(acc, activity) }
        }

    }

}
