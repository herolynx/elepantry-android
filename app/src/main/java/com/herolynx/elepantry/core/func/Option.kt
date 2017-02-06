package com.herolynx.elepantry.core.func

import org.funktionale.option.Option
import org.funktionale.option.getOrElse
import rx.Observable

fun <T> Option<T>.toObservable(): Observable<T> {
    return map { v -> Observable.just(v) }
            .getOrElse { Observable.empty() }
}