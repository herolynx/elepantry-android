package com.herolynx.elepantry

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.herolynx.elepantry.core.log.info
import com.herolynx.elepantry.user.model.User
import com.herolynx.elepantry.user.model.UserId
import org.funktionale.option.Option
import org.funktionale.option.toOption

interface AppContext {

    var googleAccount: GoogleSignInAccount?
    var user: User?

    fun setMainAccount(account: GoogleSignInAccount?) {
        info("[AppContext] Google account set")
        this.googleAccount = account
        user = User(
                UserId(googleAccount?.id!!),
                googleAccount?.displayName!!,
                googleAccount?.photoUrl!!
        )
    }

}

fun Activity.getAppContext(): Option<AppContext> {
    return application.toOption()
            .map { a -> a as AppContext }
}