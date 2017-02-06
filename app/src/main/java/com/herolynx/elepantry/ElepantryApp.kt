package com.herolynx.elepantry

import android.app.Application
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class ElepantryApp : Application(), AppContext {

    override var googleAccount: GoogleSignInAccount? = null

    override fun onCreate() {
        super.onCreate()
    }

}