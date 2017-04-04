package com.herolynx.elepantry.ext.google.firebase.db

import com.google.firebase.database.DatabaseError
import org.funktionale.option.Option

fun DatabaseError.checkError(): Option<Throwable> {
    if (toException().message?.contains("This client does not have permission to perform this operation") ?: false) {
        return Option.None
    } else if (toException().message?.contains("Permission denied") ?: false) {
        return Option.None
    }
    return Option.Some(toException())
}