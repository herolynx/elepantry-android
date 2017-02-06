package com.herolynx.elepantry.auth

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.herolynx.elepantry.Intents
import com.herolynx.elepantry.MainActivity
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.log.error
import com.herolynx.elepantry.core.navigation.navigateTo
import com.herolynx.elepantry.core.view.WithProgressDialog
import com.herolynx.elepantry.core.view.toast
import com.herolynx.elepantry.ext.google.auth.GoogleAuth
import com.herolynx.elepantry.ext.google.firebase.FirebaseAuth
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class SignInActivity : AppCompatActivity(), WithProgressDialog {

    override var mProgressDialog: ProgressDialog? = null

    private fun initViewListeners() {
        findViewById(R.id.sign_in_button).setOnClickListener({ signIn() })
    }

    private fun redirectLoggedInUser() {
        FirebaseAuth.getCurrentUser()
                .onSuccess { u ->
                    navigateTo(MainActivity::class.java)
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        initViewListeners()
        redirectLoggedInUser()
    }

    override fun onRestart() {
        super.onRestart()
        redirectLoggedInUser()
    }

    override fun onResume() {
        super.onResume()
        redirectLoggedInUser()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Intents.GOOGLE_SIGN_IN) {
            GoogleAuth.onLogInResult(data)
                    .onFailure { ex ->
                        error("[Google] Couldn't log in user", ex)
                        toast(R.string.auth_failed, "Google Account")
                    }
                    .onSuccess { account ->
                        debug("[Firebase] Logging in - account id: %s", account.id)
                        showProgressDialog(this)
                        FirebaseAuth.logIn(account)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        { auth ->
                                            hideProgressDialog()
                                            navigateTo(MainActivity::class.java)
                                        },
                                        { ex ->
                                            hideProgressDialog()
                                            error("[Firebase] Couldn't log in user", ex)
                                            toast(R.string.auth_failed, "Firebase")
                                        }
                                )
                    }
        }
    }

    private fun signIn() {
        val signInIntent = GoogleAuth.logIn(this, { connectionResult ->
            error("[Google] Connection failed: %s", connectionResult)
            toast(R.string.auth_failed, "Google Account")
        })
        startActivityForResult(signInIntent, Intents.GOOGLE_SIGN_IN)
    }

}
