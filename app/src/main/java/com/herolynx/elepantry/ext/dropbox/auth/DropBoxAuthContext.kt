package com.herolynx.elepantry.ext.dropbox.auth

import com.herolynx.elepantry.auth.Token
import com.herolynx.elepantry.core.log.info

interface DropBoxAuthContext {

    var dropBoxSession: DropBoxSession?

    fun setDropBoxAccount(session:DropBoxSession) {
        info("[AppContext] DropBox account set - userId: ${session.uid}")
        dropBoxSession = session
    }

}