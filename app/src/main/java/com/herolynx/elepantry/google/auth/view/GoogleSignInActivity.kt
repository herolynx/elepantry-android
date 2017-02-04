package com.herolynx.elepantry.google.auth.view

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.herolynx.elepantry.Intents
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.log.error
import com.herolynx.elepantry.core.view.WithProgressDialog
import com.herolynx.elepantry.core.view.toast
import com.herolynx.elepantry.google.auth.GoogleAuth
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class GoogleSignInActivity : AppCompatActivity(), WithProgressDialog {

    override var mProgressDialog: ProgressDialog? = null

    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null

    private var mGoogleApiClient: GoogleApiClient? = null
    private var mStatusTextView: TextView? = null
    private var mDetailTextView: TextView? = null

    private fun bindView() {
        mStatusTextView = findViewById(R.id.status) as TextView
        mDetailTextView = findViewById(R.id.detail) as TextView
    }

    private fun initViewListeners() {
        findViewById(R.id.sign_in_button).setOnClickListener({ signIn() })
        findViewById(R.id.sign_out_button).setOnClickListener({ signOut() })
        findViewById(R.id.disconnect_button).setOnClickListener({ revokeAccess() })
    }

    private fun initFirebase() {
        mAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                // User is signed in
                debug("[Firebase] Logged in user - id: %s", user.uid)
            } else {
                // User is signed out
                debug("[Firebase] Logged out")
            }
            updateUI(user)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google)
        bindView()
        initViewListeners()
        initFirebase()

        mGoogleApiClient = GoogleAuth.build(this, { connectionResult ->
            error("[Google] Connection failed: %s", connectionResult)
            toast(R.string.auth_failed, "Google Account")
        })

    }

    public override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(mAuthListener!!)
    }

    override fun onStop() {
        super.onStop()
        hideProgressDialog()
        if (mAuthListener != null) {
            mAuth!!.removeAuthStateListener(mAuthListener!!)
        }
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
                        com.herolynx.elepantry.google.firebase.FirebaseAuth.logIn(account)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe { auth ->
                                    hideProgressDialog()
                                    updateUI(auth.user)
                                }

                    }
        }
    }

    private fun signIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, Intents.GOOGLE_SIGN_IN)
    }

    private fun signOut() {
        // Firebase sign out
        mAuth!!.signOut()
        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback { updateUI(null) }
    }

    private fun revokeAccess() {
        // Firebase sign out
        mAuth!!.signOut()

        // Google revoke access
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback { updateUI(null) }
    }

    private fun updateUI(user: FirebaseUser?) {
        hideProgressDialog()
        if (user != null) {
            mStatusTextView!!.text = getString(R.string.google_status_fmt, user.email)
            mDetailTextView!!.text = getString(R.string.firebase_status_fmt, user.uid)

            findViewById(R.id.sign_in_button).visibility = View.GONE
            findViewById(R.id.sign_out_and_disconnect).visibility = View.VISIBLE
        } else {
            mStatusTextView!!.setText(R.string.signed_out)
            mDetailTextView!!.text = null

            findViewById(R.id.sign_in_button).visibility = View.VISIBLE
            findViewById(R.id.sign_out_and_disconnect).visibility = View.GONE
        }
    }

}
