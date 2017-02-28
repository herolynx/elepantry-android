package com.herolynx.elepantry.ext.google

import android.content.Context
import android.support.v4.app.FragmentActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.log.error
import java.util.*

object GoogleApi {

    private val RAN = Random()

    fun build(
            fragmentActivity: FragmentActivity,
            onFailedHandler: (ConnectionResult) -> Unit = { cr -> error("[GoogleApi] Connection result error: %s", cr) }
    ): GoogleApiClient {
        return GoogleApiClient.Builder(fragmentActivity)
                .enableAutoManage(fragmentActivity, RAN.nextInt(1000), onFailedHandler)
                .addApi(Auth.GOOGLE_SIGN_IN_API, getSignInOptions(fragmentActivity))
                .build()
    }

    private fun getSignInOptions(c: Context): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(GoogleConfig.DRIVE_READONLY_API)
                .requestIdToken(c.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

    }

}