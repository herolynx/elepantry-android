package com.herolynx.elepantry.drive

import android.app.Activity
import com.herolynx.elepantry.core.Result
import com.herolynx.elepantry.resources.core.model.Resource
import org.funktionale.tries.Try
import rx.Observable
import java.io.InputStream

interface CloudResource {

    fun thumbnail(): Observable<InputStream>

    fun metaInfo(): Resource

    fun preview(a:Activity) : Try<Result>

}