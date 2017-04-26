package com.herolynx.elepantry.core.log

import android.util.Log
import com.google.firebase.crash.FirebaseCrash

private fun <T> tag(t: T): String {
    val o = t as Any
    return o.javaClass.simpleName
}

fun <T> T.debug(msg: String) {
    Log.d(tag(this), msg)
    FirebaseCrash.log(msg)
}

fun <T> T.debug(msg: String, t: Throwable) {
    Log.d(tag(this), msg, t)
    FirebaseCrash.log(msg)
    FirebaseCrash.log("Debug error message: ${t.message}")
}

fun <T> T.info(msg: String) {
    Log.i(tag(this), msg)
    FirebaseCrash.log(msg)
}

fun <T, E : Throwable> T.exception(msg: String, t: E?) {
    Log.e(tag(this), msg, t)
    FirebaseCrash.log(msg)
    FirebaseCrash.report(t)
}

fun <T, E : Throwable> T.error(msg: String, t: E?) {
    Log.e(tag(this), msg, t)
    FirebaseCrash.log(msg)
    FirebaseCrash.log("Error message: ${t?.message}")
}

fun <T> T.error(msg: String) {
    Log.e(tag(this), msg)
}

fun <T, E : Throwable> T.warn(msg: String, t: E?) {
    Log.w(tag(this), msg, t)
    FirebaseCrash.log(msg)
    FirebaseCrash.log("Warn error message: ${t?.message}")
}
