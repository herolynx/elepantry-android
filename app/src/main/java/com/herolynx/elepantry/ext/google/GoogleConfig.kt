package com.herolynx.elepantry.ext.google

import com.google.android.gms.common.api.Scope

object GoogleConfig {

    val DRIVE_READONLY_API_URL = "https://www.googleapis.com/auth/drive.readonly"
    val DRIVE_READONLY_API = Scope(DRIVE_READONLY_API_URL)

}