package com.herolynx.elepantry.user.model

import android.net.Uri

typealias UserId = String

data class User(val id: UserId, val displayName: String, val photoUrl: Uri)