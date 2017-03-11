package com.herolynx.elepantry.core.func

import org.funktionale.option.getOrElse
import org.funktionale.option.toOption
import org.funktionale.tries.Try

object Retry {

    fun <T> executeWithRetries(
            logic: () -> Try<T>,
            retryPolicy: (Throwable) -> Boolean = { true },
            tryNr: Int = 0,
            maxTries: Int = 2,
            lastEx: Throwable? = null
    ): Try<T> {
        if (tryNr >= maxTries) {
            return Try.Failure(lastEx.toOption().getOrElse { RuntimeException("Unknown error") })
        }
        return logic()
                .rescue { ex ->
                    if (retryPolicy(ex)) executeWithRetries(logic, retryPolicy, tryNr + 1, maxTries, ex)
                    else Try.Failure(ex)
                }
    }

}