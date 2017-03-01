package com.herolynx.elepantry.ext.google.generic

import android.os.SystemClock
import com.google.android.gms.common.api.OptionalPendingResult
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Result
import rx.Observable

fun <T : Result> PendingResult<T>.toObservable(): Observable<T> {
    return Observable.defer {
        val result = await()
        Observable.just(result)
    }
}

fun <T : Result> OptionalPendingResult<T>.toObservable(delayMs: Long = 100): Observable<T> {
    return Observable.defer {
        while (!isDone && !isCanceled) {
            SystemClock.sleep(delayMs)
        }
        Observable.just(get())
    }
}
