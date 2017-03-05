package com.herolynx.elepantry.auth

import android.content.Intent
import android.support.v4.app.FragmentActivity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.herolynx.elepantry.config.Intents
import com.herolynx.elepantry.core.func.toObservable
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.log.error
import com.herolynx.elepantry.ext.google.auth.GoogleAuth
import com.herolynx.elepantry.ext.google.firebase.auth.FirebaseAuth
import org.funktionale.tries.Try
import rx.Observable

internal object SignInUseCase {

    /**
     * Start Google OAuth procedure
     * @param activity current activity
     * @param api api client
     * @return non-nullable api client
     */
    fun startLogIn(activity: FragmentActivity, api: GoogleApiClient) {
        val signIn = GoogleAuth.logIn(api)
        activity.startActivityForResult(signIn, Intents.GOOGLE_SIGN_IN)
    }

    /**
     * Check if user's session/token is active.
     * If yes log in user automatically.
     * @param api api client
     * @param googleAuth Google OAuth silent logging in
     */
    fun autoLogIn(
            api: GoogleApiClient,
            googleAuth: (GoogleApiClient) -> Observable<GoogleSignInAccount> = { a -> GoogleAuth.silentLogIn(a) }
    ): Observable<Pair<GoogleSignInAccount, FirebaseUser?>> {
        return googleAuth(api)
                .filter { acc -> FirebaseAuth.getCurrentUser().isSuccess() }
                .map { acc ->
                    debug("[AutoLogIn] Success")
                    Pair(acc, FirebaseAuth.getCurrentUser().get())
                }
    }

    /**
     * Log in user to the app
     * @param activity current activity
     * @param progress progress indicator
     * @param data data intent from google auth
     * @param googleAuth Google OAuth response handler
     * @param firebaseAuth firebase OAuth handler
     */
    fun onLoginResult(
            data: Intent,
            googleAuth: (Intent) -> Try<GoogleSignInAccount> = { data -> GoogleAuth.onLogInResult(data) },
            firebaseAuth: (GoogleSignInAccount) -> Observable<Pair<GoogleSignInAccount, AuthResult>> = { account -> FirebaseAuth.logIn(account) }
    ): Observable<Pair<GoogleSignInAccount, FirebaseUser?>> {
        return googleAuth(data)
                .onFailure { ex ->
                    error("[onLoginResult][GoogleAuth] Couldn't log in user", ex)
                }
                .toObservable()
                .flatMap { account ->
                    debug("[onLoginResult][FirebaseAuth] Logging in - account id: %s", account.id)
                    firebaseAuth(account)
                }
                .map { r -> Pair(r.first, r.second.user) }
    }

    /**
     * Log out user
     * @param api api client
     * @return non-nullable results
     */
    fun logOut(api: GoogleApiClient): Observable<Status> {
        debug("[LogOut] Logging out...")
        return GoogleAuth.logOut(api)
                .map { status ->
                    debug("[LogOut] Google log out: %s", status)
                    status
                }
                .filter { status -> status.isSuccess }
                .map { s ->
                    FirebaseAuth.logOut()
                    debug("[LogOut] Success")
                    s
                }
    }

}
