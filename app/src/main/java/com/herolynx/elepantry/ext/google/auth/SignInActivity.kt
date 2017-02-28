package com.herolynx.elepantry.ext.google.auth

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.widget.Button
import com.herolynx.elepantry.Intents
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.ui.notification.WithProgressDialog

class SignInActivity : FragmentActivity(), WithProgressDialog {

    override var mProgressDialog: ProgressDialog? = null

    private fun initViewListeners() {
        findViewById(R.id.sign_in_button).setOnClickListener({ SignInUseCase.startLogIn(this) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in_view)
        initViewListeners()
        initActionHandlers()
        SignInUseCase.autoLogIn(this, this)
    }

    private fun initActionHandlers() {
        val signIn = findViewById(R.id.sign_in_button) as Button
        signIn.setOnClickListener { SignInUseCase.startLogIn(this) }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Intents.GOOGLE_SIGN_IN) {
            SignInUseCase.onLoginResult(this, this, data)
        }
    }

}
