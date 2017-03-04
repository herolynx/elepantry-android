package com.herolynx.elepantry.core.conversion

import com.google.api.client.json.jackson2.JacksonFactory
import org.funktionale.tries.Try

private val json = JacksonFactory.getDefaultInstance()

fun <T> T.toJsonString() = Try { json.toString(this) }

fun <T> String.fromJsonString(t: Class<T>) = Try { json.fromString(this, t) }