package com.herolynx.elepantry.core.generic

import android.os.Bundle

fun Bundle.put(name: String, value: String): Bundle {
    putString(name, value)
    return this
}

fun Bundle.put(name: String, value: Int): Bundle {
    putInt(name, value)
    return this
}

fun Bundle.put(name: String, value: Boolean): Bundle {
    putBoolean(name, value)
    return this
}