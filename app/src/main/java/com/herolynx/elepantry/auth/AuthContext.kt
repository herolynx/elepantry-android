package com.herolynx.elepantry.auth

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.herolynx.elepantry.core.log.info
import com.herolynx.elepantry.user.model.User
import com.herolynx.elepantry.user.model.UserId

interface AuthContext {

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
