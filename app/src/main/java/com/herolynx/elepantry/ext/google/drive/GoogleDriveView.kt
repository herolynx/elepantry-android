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
import com.herolynx.elepantry.resources.ResourceView
import com.herolynx.elepantry.resources.model.SearchCriteria
import org.funktionale.option.Option
import org.funktionale.option.toOption
import java.util.*

class GoogleDriveView(private val service: Drive) : ResourceView {

    override fun search(c: SearchCriteria) = GoogleDrivePage.create { nextPageToken ->
        service.files()
                .list()
                .setQ(String.format(QUERY_BY_NAME, c.text))
                .setPageSize(c.pageSize)
                .setPageToken(nextPageToken)
    }

    companion object Factory {

        private val QUERY_BY_NAME = "name contains '%s'"
        private val HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport()
        private val JSON_FACTORY = JacksonFactory.getDefaultInstance()

        fun create(account: GoogleSignInAccount, c: Context): GoogleDriveView {
            val credential = GoogleAccountCredential.usingOAuth2(
                    c,
                    Collections.singleton(GoogleConfig.DRIVE_READONLY_API_URL)
            )
            credential.setSelectedAccount(account.account)
            return GoogleDriveView(Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).build())
        }

        fun create(activity: Activity): Option<GoogleDriveView> {
            return activity.getAppContext()
                    .flatMap { a -> a.googleAccount.toOption() }
                    .map { acc -> create(acc, activity) }
        }

    }

}
