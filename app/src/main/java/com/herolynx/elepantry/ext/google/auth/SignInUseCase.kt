package com.herolynx.elepantry.ext.google.auth

import android.app.Activity
import android.content.Intent
import android.support.v4.app.FragmentActivity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.AuthResult
import com.herolynx.elepantry.Intents
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.func.toObservable
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.log.error
import com.herolynx.elepantry.core.ui.navigation.navigateTo
import com.herolynx.elepantry.core.ui.notification.WithProgressDialog
import com.herolynx.elepantry.core.ui.notification.toast
import com.herolynx.elepantry.ext.google.firebase.auth.FirebaseAuth
import com.herolynx.elepantry.getAuthContext
import com.herolynx.elepantry.resources.view.ResourcesActivity
import org.funktionale.tries.Try
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

object SignInUseCase {

    /**
     * Start Google OAuth procedure
     * @param activity current activity
     * @return non-nullable api client
     */
    fun startLogIn(activity: FragmentActivity): GoogleApiClient {
        val signIn = GoogleAuth.logIn(activity, { connectionResult ->
            error("[Google] Connection failed: %s", connectionResult)
            activity.toast(R.string.auth_failed, "Google Account")
        })
        activity.startActivityForResult(signIn.second, Intents.GOOGLE_SIGN_IN)
        return signIn.first
    }

    /**
     * Check if user's session/token is active.
     * If yes log in user automatically.
     * @param activity current activity
     * @param progress progress indicator
     */
    fun autoLogIn(activity: FragmentActivity, progress: WithProgressDialog) {
        if (FirebaseAuth.getCurrentUser().isSuccess()) {
            debug("[SignIn] User is auth - auto-login...")
            progress.showProgressDialog(activity)
            SignInUseCase.startLogIn(activity)
        }
    }

    /**
     * Log in user to the app
     * @param activity current activity
     * @param progress progress indicator
     * @param data data intent from google auth
     * @param googleAuth google OAuth response handler
     * @param firebaseAuth firebase OAuth handler
     */
    fun onLoginResult(
            activity: Activity,
            progress: WithProgressDialog,
            data: Intent,
            googleAuth: (Intent) -> Try<GoogleSignInAccount> = { data -> GoogleAuth.onLogInResult(data) },
            firebaseAuth: (GoogleSignInAccount) -> Observable<AuthResult> = { account -> FirebaseAuth.logIn(account) }
    ) {
        progress.showProgressDialog(activity)
        googleAuth(data)
                .onFailure { ex ->
                    error("[Google] Couldn't log in user", ex)
                    activity.toast(R.string.auth_failed, "Google Account")
                }
                .toObservable()
                .flatMap { account ->
                    debug("[Firebase] Logging in - account id: %s", account.id)
                    activity.getAuthContext().map { c -> c.setMainAccount(account) }
                    firebaseAuth(account)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { auth ->
                            debug("[Auth] User is logged in")
                            progress.hideProgressDialog()
                            activity.navigateTo(ResourcesActivity::class.java)
                        },
                        { ex ->
                            progress.hideProgressDialog()
                            error("[Firebase] Couldn't log in user", ex)
                            activity.toast(R.string.auth_failed, "Firebase")
                        }
                )
    }

    fun logOut(activity: Activity) {
        FirebaseAuth.logOut()
        activity.navigateTo(SignInActivity::class.java)
    }

}
