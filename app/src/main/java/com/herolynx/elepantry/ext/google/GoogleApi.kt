package com.herolynx.elepantry.ext.google

import android.content.Context
import android.os.SystemClock
import android.support.v4.app.FragmentActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.log.error
import rx.Observable

object GoogleApi {

    fun build(
            fragmentActivity: FragmentActivity,
            onFailedHandler: (ConnectionResult) -> Unit = { cr -> error("[GoogleApi] Connection result error: $cr") }
    ): GoogleApiClient {
        return GoogleApiClient.Builder(fragmentActivity)
                .enableAutoManage(fragmentActivity, onFailedHandler)
                .addApi(Auth.GOOGLE_SIGN_IN_API, getSignInOptions(fragmentActivity))
                .build()
    }

    private fun getSignInOptions(c: Context): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(GoogleConfig.DRIVE_READONLY_API, GoogleConfig.PHOTOS_READONLY_API)
                .requestIdToken(c.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

    }

}

fun GoogleApiClient.asyncConnect(delayMs: Long = 100): Observable<GoogleApiClient> {
    return Observable.defer {
        connect()
        debug("[GoogleApiClient] Async connect - connected: $isConnected, connecting: $isConnecting")
        while (!isConnected && isConnecting) {
            SystemClock.sleep(delayMs)
        }
        Observable.just(this)
    }
}