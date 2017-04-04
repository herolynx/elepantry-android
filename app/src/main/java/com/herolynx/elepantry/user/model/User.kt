package com.herolynx.elepantry.user.model

import android.net.Uri
import com.herolynx.elepantry.core.generic.fromISO8601
import org.joda.time.DateTime

data class UserId(val uid: String) {
    constructor() : this("")
}

data class User(val id: UserId, val displayName: String, val photoUrl: Uri)

data class UserMetaInf(val id: String, val lastSyncTime: String = "") {

    constructor() : this("")

}

fun UserMetaInf.getLastSyncTimeDate() = lastSyncTime.fromISO8601().getOrElse { DateTime.now().minusDays(1).toDate() }