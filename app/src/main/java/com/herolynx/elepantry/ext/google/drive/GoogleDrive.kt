package com.herolynx.elepantry.ext.google.drive

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.herolynx.elepantry.core.log.debug
import java.util.*


class GoogleDrive(private val service: Drive) {

    fun search(text: String = "") {
        debug("[GDrive] Searching: %s", text)

        service.files().list().execute().files.forEach { f ->
            debug("[GoogleDrive] File: " + f.name)
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
