package com.herolynx.elepantry.core.conversion

import com.google.gson.Gson
import org.funktionale.tries.Try

private val json = Gson()

fun <T> T.toJsonString() = Try { json.toJson(this) }

fun <T> String.fromJsonString(t: Class<T>) = Try { json.fromJson(this, t) }