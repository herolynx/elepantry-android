package com.herolynx.elepantry.ext.google.auth

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.widget.Button
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseUser
import com.herolynx.elepantry.Intents
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.log.error
import com.herolynx.elepantry.core.rx.observe
import com.herolynx.elepantry.core.rx.schedule
import com.herolynx.elepantry.core.ui.navigation.navigateTo
import com.herolynx.elepantry.core.ui.notification.WithProgressDialog
import com.herolynx.elepantry.core.ui.notification.toast
import com.herolynx.elepantry.ext.google.GoogleApi
import com.herolynx.elepantry.getAuthContext
import com.herolynx.elepantry.resources.view.ResourcesActivity
import rx.Observable

class SignInActivity : FragmentActivity(), WithProgressDialog {

    override var mProgressDialog: ProgressDialog? = null

    private var api: GoogleApiClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in_view)
        api = GoogleApi.build(this)
        initActionHandlers(api!!)
        handleAuthResults(SignInUseCase.autoLogIn(api!!))
    }

    private fun handleAuthResults(o: Observable<Pair<GoogleSignInAccount, FirebaseUser?>>) {
        o.schedule()
                .observe()
                .subscribe(
                        { auth -> onAuthResult(auth) },
                        { ex -> onAuthError(ex) }
                )
    }

    private fun initActionHandlers(api: GoogleApiClient) {
        val signIn = findViewById(R.id.sign_in_button) as Button
        signIn.setOnClickListener {
            showProgressDialog(this)
            SignInUseCase.startLogIn(this, api)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Intents.GOOGLE_SIGN_IN) {
            handleAuthResults(SignInUseCase.onLoginResult(data))
        }
    }

    private fun onAuthError(t: Throwable, showToast: Boolean = false) {
        hideProgressDialog()
        error("[Auth] Couldn't log in user", t)
        if (showToast) {
            toast(R.string.auth_failed, "Sign up failed")
        }
    }

    private fun onAuthResult(auth: Pair<GoogleSignInAccount, FirebaseUser?>) {
        debug("[Auth] User is logged in - auth: $auth, api connected: ${api?.isConnected}")
        hideProgressDialog()
        api?.disconnect()
        if (auth.second != null) {
            getAuthContext().map { c -> c.setMainAccount(auth.first) }
            navigateTo(ResourcesActivity::class.java)
        } else {
            toast(R.string.auth_failed, "Sign up failed")
        }
    }

}
