package com.herolynx.elepantry

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.herolynx.elepantry.core.log.info
import org.funktionale.option.Option
import org.funktionale.option.toOption

interface AppContext {

    var googleAccount: GoogleSignInAccount?

    fun setMainAccount(account: GoogleSignInAccount?) {
        info("[AppContext] Google account set")
        this.googleAccount = account
    }

    fun getMainAccount(): GoogleSignInAccount? {
        return googleAccount
    }

}

fun Activity.getAppContext(): Option<AppContext> {
    return application.toOption()
            .map { a -> a as AppContext }
}