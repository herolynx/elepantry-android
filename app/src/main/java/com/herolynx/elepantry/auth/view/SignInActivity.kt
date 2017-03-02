package com.herolynx.elepantry.auth.view

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.widget.Button
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.ui.notification.WithProgressDialog

class SignInActivity : FragmentActivity(), WithProgressDialog {

    override var mProgressDialog: ProgressDialog? = null
    private var ctrl: SignInCtrl? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in_view)
        ctrl = SignInCtrl(this)
        initActionHandlers(ctrl!!)
        ctrl?.autoLogIn()
    }

    override fun onResume() {
        super.onResume()
        ctrl?.autoLogIn()
    }

    private fun initActionHandlers(ctrl: SignInCtrl) {
        val buttonSignIn = findViewById(R.id.sign_in_button) as Button
        buttonSignIn.setOnClickListener { ctrl.logIn() }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        ctrl?.handleLoginResult(requestCode, data)
    }
}
