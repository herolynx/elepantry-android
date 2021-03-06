package com.herolynx.elepantry

import android.app.Activity
import android.app.Application
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.herolynx.elepantry.auth.AuthContext
import com.herolynx.elepantry.core.log.exception
import com.herolynx.elepantry.ext.dropbox.auth.DropBoxSession
import com.herolynx.elepantry.user.model.User
import org.funktionale.option.Option
import org.funktionale.option.toOption


class ElepantryApp : Application(), AuthContext {

    override var user: User? = null
    override var googleAccount: GoogleSignInAccount? = null
    override var dropBoxSession: DropBoxSession? = null

    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler { _, ex -> exception("UncaughtExceptionHandler", ex) }
    }

}

fun Activity.getAuthContext(): Option<AuthContext> {
    return application.toOption()
            .map { a -> a as AuthContext }
}