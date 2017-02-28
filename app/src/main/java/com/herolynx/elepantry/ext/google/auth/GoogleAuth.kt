package com.herolynx.elepantry.ext.google.auth

import android.content.Intent
import android.support.v4.app.FragmentActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.herolynx.elepantry.core.log.error
import com.herolynx.elepantry.ext.google.GoogleApi
import org.funktionale.tries.Try

object GoogleAuth {

    fun onLogInResult(data: Intent): Try<GoogleSignInAccount> {
        val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
        if (result.isSuccess) {
            return Try.Success(result.signInAccount!!)
        } else {
            return Try.Failure(RuntimeException("Google sign in error - status: " + result.status))
        }
    }

    fun logIn(
            fragmentActivity: FragmentActivity,
            handler: (ConnectionResult) -> Unit = { cr -> error("[GoogleAuth] Connection failed: %s", cr) },
            api: GoogleApiClient = GoogleApi.build(fragmentActivity, handler)
    ): Pair<GoogleApiClient, Intent> {
        return Pair(api, Auth.GoogleSignInApi.getSignInIntent(api))
    }

}