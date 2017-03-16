package com.herolynx.elepantry.auth.view

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseUser
import com.herolynx.elepantry.R
import com.herolynx.elepantry.auth.SignInUseCase
import com.herolynx.elepantry.config.Intents
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.log.error
import com.herolynx.elepantry.core.rx.observe
import com.herolynx.elepantry.core.rx.schedule
import com.herolynx.elepantry.core.ui.notification.toast
import com.herolynx.elepantry.ext.google.GoogleApi
import com.herolynx.elepantry.getAuthContext
import com.herolynx.elepantry.resources.view.list.ResourcesActivity
import rx.Observable

internal class SignInCtrl(
        private val view: SignInActivity,
        private var api: GoogleApiClient = GoogleApi.build(view)
) {

    fun handleLoginResult(requestCode: Int, data: Intent) {
        if (requestCode == Intents.GOOGLE_SIGN_IN) {
            handleAuthResults(SignInUseCase.onLoginResult(data))
        }
    }

    fun autoLogIn() {
        handleAuthResults(SignInUseCase.autoLogIn(api))
    }

    fun logIn() {
        view.showProgressDialog(view)
        SignInUseCase.startLogIn(view, api)
    }

    private fun handleAuthResults(o: Observable<Pair<GoogleSignInAccount, FirebaseUser?>>) {
        o.schedule()
                .observe()
                .subscribe(
                        { auth -> onAuthResult(auth) },
                        { ex -> onAuthError(ex) }
                )
    }

    private fun onAuthError(t: Throwable, showToast: Boolean = false) {
        view.hideProgressDialog()
        error("[Auth] Couldn't log in user", t)
        if (showToast) {
            view.toast(R.string.auth_failed, "Sign up failed")
        }
    }

    private fun onAuthResult(auth: Pair<GoogleSignInAccount, FirebaseUser?>) {
        debug("[Auth] User is logged in - auth: $auth, api connected: ${api.isConnected}")
        view.hideProgressDialog()
        api.disconnect()
        if (auth.second != null) {
            view.getAuthContext().map { c -> c.setMainAccount(auth.first) }
            ResourcesActivity.navigate(view)
        } else {
            view.toast(R.string.auth_failed, "Sign up failed")
        }
    }
}