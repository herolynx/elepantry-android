package com.herolynx.elepantry.core.net

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import rx.Observable
import java.net.URL

fun Uri.download(): Observable<Bitmap> {
    return Observable.defer {
        try {
            val bitMap = BitmapFactory.decodeStream(URL(toString()).openConnection().getInputStream())
            Observable.just(bitMap)
        } catch (t: Throwable) {
            Observable.error<Bitmap>(t)
        }
    }
}