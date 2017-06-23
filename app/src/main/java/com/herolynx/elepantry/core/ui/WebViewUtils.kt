package com.herolynx.elepantry.core.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.log.warn
import com.herolynx.elepantry.core.ui.notification.toast


object WebViewUtils {

    fun openLink(a: Activity, url: String?) {
        try {
            debug("[WebView] Opening link: $url")
            if (url != null) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                a.startActivity(browserIntent)
            }
        } catch(ex: Exception) {
            warn("[WebView] Couldn't open link: $url", ex)
            a.toast(R.string.error_web_view_open_link)
        }
    }

}