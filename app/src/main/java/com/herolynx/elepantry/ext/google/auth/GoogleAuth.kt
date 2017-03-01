package com.herolynx.elepantry.ext.google.auth

import android.content.Intent
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.herolynx.elepantry.ext.google.generic.toObservable
import org.funktionale.tries.Try
import rx.Observable

internal object GoogleAuth {

    fun onLogInResult(data: Intent): Try<GoogleSignInAccount> {
        val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
        if (result.isSuccess) {
            return Try.Success(result.signInAccount!!)
        } else {
            return Try.Failure(RuntimeException("Google sign in error - status: " + result.status))
        }
    }

    fun silentLogIn(api: GoogleApiClient): Observable<GoogleSignInAccount> {
        return Auth.GoogleSignInApi.silentSignIn(api)
                .toObservable()
                .filter { result -> result.isSuccess }
                .map { result -> result.signInAccount }
    }

    fun logIn(api: GoogleApiClient): Intent {
        return Auth.GoogleSignInApi.getSignInIntent(api)
    }

    fun logOut(api: GoogleApiClient): Observable<Status> {
        return Auth.GoogleSignInApi.signOut(api)
                .toObservable()
    }

}