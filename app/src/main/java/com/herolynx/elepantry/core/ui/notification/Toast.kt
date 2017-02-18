package com.herolynx.elepantry.core.ui.notification

import android.app.Activity
import android.support.annotation.IdRes
import android.widget.Toast

fun <A : Activity> A.toast(message: String, vararg args: Any?) {
    Toast.makeText(
            this,
            message.format(args.map { o -> o.toString() }),
            Toast.LENGTH_SHORT
    )
            .show()
}

fun <A : Activity> A.toast(@IdRes msgId: Int, vararg args: Any) {
    toast(this.getString(msgId), args)
}
