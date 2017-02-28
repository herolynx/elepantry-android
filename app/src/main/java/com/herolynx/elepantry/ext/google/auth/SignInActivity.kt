package com.herolynx.elepantry.ext.google.auth

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.widget.Button
import com.herolynx.elepantry.Intents
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.ui.notification.WithProgressDialog

class SignInActivity : FragmentActivity(), WithProgressDialog {

    override var mProgressDialog: ProgressDialog? = null

    private fun initViewListeners() {
        findViewById(R.id.sign_in_button).setOnClickListener({ GoogleAuth.startLogIn(this) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in_view)
        initViewListeners()
        initActionHandlers()
        autoLogin()
    }

    override fun onResume() {
        super.onResume()
        autoLogin()
    }

    private fun initActionHandlers() {
        val signIn = findViewById(R.id.sign_in_button) as Button
        signIn.setOnClickListener { GoogleAuth.startLogIn(this) }
    }

    private fun autoLogin() {
        if (com.google.firebase.auth.FirebaseAuth.getInstance() != null) {
            debug("[SignIn] User is auth - auto-login...")
//            navigateTo(ResourcesActivity::class.java)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Intents.GOOGLE_SIGN_IN) {
            GoogleAuth.logIn(this, this, data)
        }
    }

}
