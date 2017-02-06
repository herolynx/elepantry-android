package com.herolynx.elepantry.core.func

import org.funktionale.tries.Try
import rx.Observable

fun <T> Try<T>.toObservable(): Observable<T> {
    return map { v -> Observable.just(v) }
            .rescue { ex -> Try.Success(Observable.error<T>(ex)) }
            .get()
}