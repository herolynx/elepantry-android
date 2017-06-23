package com.herolynx.elepantry.core.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.herolynx.elepantry.core.log.debug
import org.funktionale.tries.Try


object WebViewUtils {

    fun openLink(a: Activity, url: String?): Try<Boolean> = Try {
        debug("[WebView] Opening link: $url")
        if (url != null) {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            a.startActivity(browserIntent)
            true
        } else {
            false
        }
    }

}