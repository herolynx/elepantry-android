package com.herolynx.elepantry.core.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.herolynx.elepantry.core.log.debug


object WebViewUtils {

    fun openLink(a: Activity, url: String?) {
        debug("[WebView] Opening link: $url")
        if (url != null) {
            val browserIntent = Intent("android.intent.action.VIEW", Uri.parse(url))
            a.startActivity(browserIntent)
        }
    }

}