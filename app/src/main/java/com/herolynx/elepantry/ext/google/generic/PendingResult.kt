package com.herolynx.elepantry.ext.google.generic

import android.os.SystemClock
import com.google.android.gms.common.api.OptionalPendingResult
import com.google.android.gms.common.api.Result
import org.funktionale.option.Option
import org.funktionale.option.toOption
import rx.Observable


fun <T : Result> OptionalPendingResult<T>.toObservable(delayMs: Long = 100): Observable<Option<T>> {
    return Observable.defer {
        while (!isCanceled() || !isDone()) {
            SystemClock.sleep(delayMs)
        }
        val result = get().toOption()
        if (!isCanceled) Observable.just(result)
        else Observable.error(RuntimeException("Results not found - result has been cancelled"))
    }
}