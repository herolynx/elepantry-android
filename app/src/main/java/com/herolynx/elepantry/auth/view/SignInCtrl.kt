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
            handleAuthResults(SignInUseCase.onLoginResult(data), showErrorToast = true)
        }
    }

    fun autoLogIn() {
        view.showProgressDialog(view)
        handleAuthResults(SignInUseCase.autoLogIn(api), showErrorToast = false)
    }

    fun logIn() {
        view.showProgressDialog(view)
        SignInUseCase.startLogIn(view, api)
    }

    private fun handleAuthResults(o: Observable<Pair<GoogleSignInAccount?, FirebaseUser?>>, showErrorToast: Boolean) {
        o.schedule()
                .observe()
                .subscribe(
                        { auth -> onAuthResult(auth, showErrorToast) },
                        { ex -> onAuthError(ex, showErrorToast) }
                )
    }

    private fun onAuthError(t: Throwable, showToast: Boolean = false) {
        view.hideProgressDialog()
        error("[Auth] Couldn't log in user", t)
        if (showToast) {
            view.toast(R.string.auth_failed)
        }
    }

    private fun onAuthResult(auth: Pair<GoogleSignInAccount?, FirebaseUser?>, showErrorToast: Boolean) {
        debug("[Auth] User is logged in - auth: ${auth.first?.id}, api connected: ${api.isConnected}")
        view.hideProgressDialog()
        api.disconnect()
        if (auth.first != null && auth.second != null) {
            view.getAuthContext().map { c -> c.setMainAccount(auth.first) }
            ResourcesActivity.navigate(view)
        } else if (showErrorToast) {
            view.toast(R.string.auth_failed)
        }
    }
}