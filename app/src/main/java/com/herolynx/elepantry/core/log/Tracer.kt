package com.herolynx.elepantry.core.log

import android.util.Log

private fun <T> tag(t: T): String {
    val o = t as Any
    return o.javaClass.simpleName
}

fun <T> T.debug(msg: String, vararg args: Any?) {
    Log.d(tag(this), msg.format(args.map { o -> o.toString() }))
}

fun <T> T.info(msg: String, vararg args: Any?) {
    Log.i(tag(this), msg.format(args.map { o -> o.toString() }))
}

fun <T, E : Throwable> T.error(msg: String, t: E?) {
    Log.e(tag(this), msg, t)
}

fun <T> T.error(msg: String, vararg args: Any?) {
    Log.e(tag(this), msg.format(args.map { o -> o.toString() }))
}

fun <T, E : Throwable> T.error(t: E, msg: String, vararg args: Any?) {
    Log.e(tag(this), msg.format(args.map { o -> o.toString() }), t)
}

fun <T, E : Throwable> T.warn(msg: String, t: E?) {
    Log.w(tag(this), msg, t)
}
