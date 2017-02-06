package com.herolynx.elepantry.ext.google.drive

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.herolynx.elepantry.core.log.debug
import rx.Observable
import java.util.*

class GoogleDrive(private val service: Drive) {

    fun search(text: String = ""): Observable<File> {
        debug("[GoogleDrive] Searching: %s", text)
        return Observable.defer {
            Observable.from(service.files().list().execute().files.asIterable())
        }
    }

    companion object Factory {

        private val HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport()
        private val JSON_FACTORY = JacksonFactory.getDefaultInstance()

        fun create(account: GoogleSignInAccount, c: Context): GoogleDrive {
            val credential = GoogleAccountCredential.usingOAuth2(
                    c,
                    Collections.singleton("https://www.googleapis.com/auth/drive.readonly")
            )
            credential.setSelectedAccount(account.account)
            return GoogleDrive(Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).build())
        }

    }

}
