package com.herolynx.elepantry.core.rx

import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

fun <T> Observable<T>.subscribeOnDefault(): Observable<T> {
    return subscribeOn(Schedulers.io())
}

fun <T> Observable<T>.observeOnUi(): Observable<T> {
    return observeOn(AndroidSchedulers.mainThread())
}