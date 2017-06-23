package com.herolynx.elepantry.auth

import com.herolynx.elepantry.ext.dropbox.auth.DropBoxAuthContext
import com.herolynx.elepantry.ext.google.auth.GoogleAuthContext

interface AuthContext :
        GoogleAuthContext,
        DropBoxAuthContext