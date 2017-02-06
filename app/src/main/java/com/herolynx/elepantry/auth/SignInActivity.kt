package com.herolynx.elepantry.auth

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.herolynx.elepantry.Intents
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.view.WithProgressDialog

class SignInActivity : AppCompatActivity(), WithProgressDialog {

    override var mProgressDialog: ProgressDialog? = null

    private fun initViewListeners() {
        findViewById(R.id.sign_in_button).setOnClickListener({ AuthUseCases.startLogIn(this) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in_view)
        initViewListeners()
        AuthUseCases.startLogIn(this)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Intents.GOOGLE_SIGN_IN) {
            AuthUseCases.logIn(this, this, data)
        }
    }

}
