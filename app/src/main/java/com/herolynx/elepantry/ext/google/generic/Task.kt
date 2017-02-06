package com.herolynx.elepantry.ext.google.generic

import android.os.SystemClock
import com.google.android.gms.tasks.Task
import rx.Observable

fun <T> Task<T>.toObservable(delayMs: Long = 100): Observable<T> {
    return Observable.defer {
        while (!isComplete) {
            SystemClock.sleep(delayMs)
        }
        if (isSuccessful) Observable.just(result!!)
        else Observable.error(exception!!)
    }
}
