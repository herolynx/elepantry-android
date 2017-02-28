package com.herolynx.elepantry

import android.app.Activity
import android.app.Application
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.herolynx.elepantry.ext.google.auth.GoogleAuthContext
import com.herolynx.elepantry.user.model.User
import org.funktionale.option.Option
import org.funktionale.option.toOption

class ElepantryApp : Application(), GoogleAuthContext {

    override var googleAccount: GoogleSignInAccount? = null
    override var user: User? = null

    override fun onCreate() {
        super.onCreate()
    }

}

fun Activity.getAuthContext(): Option<GoogleAuthContext> {
    return application.toOption()
            .map { a -> a as GoogleAuthContext }
}