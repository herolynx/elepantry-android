package com.herolynx.elepantry

import android.app.Application
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.herolynx.elepantry.user.model.User

class ElepantryApp : Application(), AppContext {

    override var googleAccount: GoogleSignInAccount? = null
    override var user: User? = null

    override fun onCreate() {
        super.onCreate()
    }

}