package com.herolynx.elepantry.ext.google.auth

import android.content.Context
import android.content.Intent
import android.support.v4.app.FragmentActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Scope
import com.herolynx.elepantry.R
import com.herolynx.elepantry.ext.google.generic.toObservable
import org.funktionale.option.Option
import org.funktionale.tries.Try
import rx.Observable

object GoogleAuth {

    fun connect(fragmentActivity: FragmentActivity, handler: (ConnectionResult) -> Unit): GoogleApiClient {
        val api = build(fragmentActivity, handler)
        api.connect()
        return api
    }

    fun onLogInResult(data: Intent): Try<GoogleSignInAccount> {
        val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
        if (result.isSuccess) {
            return Try.Success(result.signInAccount!!)
        } else {
            return Try.Failure(RuntimeException("Google sign in error - status: " + result.status))
        }
    }

    fun silentLogIn(fragmentActivity: FragmentActivity, handler: (ConnectionResult) -> Unit): Observable<Option<GoogleSignInAccount>> {
        val api = build(fragmentActivity, handler)
        return Auth.GoogleSignInApi.silentSignIn(api).
                toObservable()
                .map { result ->
                    result.filter { r -> r.isSuccess }
                            .map { r -> r.signInAccount!! }
                }
    }

    fun logIn(fragmentActivity: FragmentActivity, handler: (ConnectionResult) -> Unit): Intent {
        val api = build(fragmentActivity, handler)
        return Auth.GoogleSignInApi.getSignInIntent(api)
    }

    fun logout(api: GoogleApiClient) {
        Auth.GoogleSignInApi
                .signOut(api)
                .setResultCallback { }
    }

    fun build(fragmentActivity: FragmentActivity, onFailedHandler: (ConnectionResult) -> Unit): GoogleApiClient {
        return GoogleApiClient.Builder(fragmentActivity)
                .enableAutoManage(fragmentActivity, onFailedHandler)
                .addApi(Auth.GOOGLE_SIGN_IN_API, getSignInOptions(fragmentActivity))
//                .addApi(Drive.API)
                .build()
    }

    private fun getSignInOptions(c: Context): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Scope(Scopes.DRIVE_FILE))
                .requestScopes(Scope(Scopes.DRIVE_APPFOLDER))
                .requestIdToken(c.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

    }

}