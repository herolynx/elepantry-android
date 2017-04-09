package com.herolynx.elepantry.auth.view

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.widget.Button
import android.widget.TextView
import com.google.firebase.analytics.FirebaseAnalytics
import com.herolynx.elepantry.BuildConfig
import com.herolynx.elepantry.R
import com.herolynx.elepantry.config.Config
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.log.viewVisit
import com.herolynx.elepantry.core.ui.WebViewUtils
import com.herolynx.elepantry.core.ui.notification.WithProgressDialog

class SignInActivity : FragmentActivity(), WithProgressDialog {

    override var mProgressDialog: ProgressDialog? = null
    private var ctrl: SignInCtrl? = null
    private var analytics: FirebaseAnalytics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analytics = FirebaseAnalytics.getInstance(this)
        analytics?.viewVisit(this)
        setContentView(R.layout.sign_in_view)
        val appIdLabel = findViewById(R.id.app_id) as TextView
        appIdLabel.text = BuildConfig.VERSION_NAME
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
        val buttonRegulations = findViewById(R.id.regulation) as Button
        buttonRegulations.setOnClickListener {
            debug("[SignIn] Going to regulations and terms")
            WebViewUtils.openLink(this, Config.licenseUrl)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        ctrl?.handleLoginResult(requestCode, data)
    }
}
