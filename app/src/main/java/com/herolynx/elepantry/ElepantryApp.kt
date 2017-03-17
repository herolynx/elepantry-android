package com.herolynx.elepantry

import android.app.Activity
import android.app.Application
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.herolynx.elepantry.auth.AuthContext
import com.herolynx.elepantry.core.log.error
import com.herolynx.elepantry.user.model.User
import org.funktionale.option.Option
import org.funktionale.option.toOption


class ElepantryApp : Application(), AuthContext {

    override var googleAccount: GoogleSignInAccount? = null
    override var user: User? = null

    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler { thread, ex -> error("UncaughtExceptionHandler", ex) }
    }

}

fun Activity.getAuthContext(): Option<AuthContext> {
    return application.toOption()
            .map { a -> a as AuthContext }
}