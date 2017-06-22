package com.herolynx.elepantry.core.net

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import rx.Observable
import java.io.InputStream
import java.net.URL

fun Uri.asInputStream(): Observable<InputStream> = Observable.defer { Observable.just(URL(toString()).openConnection().getInputStream()) }

fun InputStream.download(): Observable<Bitmap> = Observable.defer {
    try {
        val bitMap = BitmapFactory.decodeStream(this)
        Observable.just(bitMap)
    } catch (t: Throwable) {
        Observable.error<Bitmap>(t)
    }
}
