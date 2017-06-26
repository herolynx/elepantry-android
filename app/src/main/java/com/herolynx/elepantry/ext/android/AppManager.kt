package com.herolynx.elepantry.ext.android

import android.content.Context
import com.herolynx.elepantry.core.log.debug

object AppManager {

    fun isAppInstalled(c: Context, appPackageName: String): Boolean {
        val intent = c.packageManager.getLaunchIntentForPackage(appPackageName)
        debug("[AppManager] Is application installed - name: $appPackageName, installed: ${intent != null}")
        return intent != null
    }

}