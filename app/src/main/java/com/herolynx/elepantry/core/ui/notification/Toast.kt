package com.herolynx.elepantry.core.ui.notification

import android.app.Activity
import android.support.annotation.IdRes
import android.widget.Toast

fun <A : Activity> A.toast(@IdRes msgId: Int, vararg args: Any) {
    toast(getString(msgId).format(args.map { o -> o.toString() }.joinToString(",")))
}

fun <A : Activity> A.toast(msg: String) {
    Toast.makeText(
            this,
            msg,
            Toast.LENGTH_SHORT
    )
            .show()
}
