package com.herolynx.elepantry.drive

import com.herolynx.elepantry.resources.core.model.Resource
import rx.Observable
import java.io.InputStream

interface CloudResource {

    fun thumbnail(): Observable<InputStream>

    fun metaInfo(): Resource

}