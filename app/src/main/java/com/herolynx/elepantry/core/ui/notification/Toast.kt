package com.herolynx.elepantry.core.ui.notification

import android.app.Activity
import android.support.annotation.IdRes
import android.widget.Toast

fun <A : Activity> A.toast(@IdRes msgId: Int, vararg args: Any) {
    Toast.makeText(
            this,
            getString(msgId).format(args.map { o -> o.toString() }.joinToString(",")),
            Toast.LENGTH_SHORT
    )
            .show()
}
