package com.herolynx.elepantry.core.log

import android.util.Log
import com.google.firebase.crash.FirebaseCrash

private fun <T> tag(t: T): String {
    val o = t as Any
    return o.javaClass.simpleName
}

fun <T> T.debug(msg: String, vararg args: Any?) {
    Log.d(tag(this), msg.format(args.map { o -> o.toString() }))
}

fun <T> T.debug(msg: String, t: Throwable) {
    Log.d(tag(this), msg, t)
}

fun <T> T.info(msg: String, vararg args: Any?) {
    val infoMsg = msg.format(args.map { o -> o.toString() })
    Log.i(tag(this), infoMsg)
    FirebaseCrash.log(infoMsg)
}

fun <T, E : Throwable> T.error(msg: String, t: E?) {
    Log.e(tag(this), msg, t)
    FirebaseCrash.log(msg)
    FirebaseCrash.report(t)
}

fun <T> T.error(msg: String, vararg args: Any?) {
    val errMsg = msg.format(args.map { o -> o.toString() })
    Log.e(tag(this), errMsg)
    FirebaseCrash.report(RuntimeException(errMsg))
}

fun <T, E : Throwable> T.error(t: E, msg: String, vararg args: Any?) {
    val errMsg = msg.format(args.map { o -> o.toString() })
    Log.e(tag(this), errMsg, t)
    FirebaseCrash.log(errMsg)
    FirebaseCrash.report(t)
}

fun <T, E : Throwable> T.warn(msg: String, t: E?) {
    Log.w(tag(this), msg, t)
    FirebaseCrash.log(msg)
}
