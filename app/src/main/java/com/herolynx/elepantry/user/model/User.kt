package com.herolynx.elepantry.user.model

import android.net.Uri

data class UserId(val uid: String) {
    constructor() : this("")
}

data class User(val id: UserId, val displayName: String, val photoUrl: Uri)
