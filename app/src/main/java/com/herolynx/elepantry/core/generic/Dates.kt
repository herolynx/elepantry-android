package com.herolynx.elepantry.core.generic

import com.google.gson.internal.bind.util.ISO8601Utils
import org.funktionale.tries.Try
import java.text.ParsePosition
import java.util.*

fun Date.toISO8601(): Try<String> = Try { ISO8601Utils.format(this) }

fun String.fromISO8601(): Try<Date> = Try { ISO8601Utils.parse(this, ParsePosition(0)) }
