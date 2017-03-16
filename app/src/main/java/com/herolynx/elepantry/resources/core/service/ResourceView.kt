package com.herolynx.elepantry.resources.core.service

import com.herolynx.elepantry.core.rx.DataEvent
import com.herolynx.elepantry.resources.core.model.Resource
import org.funktionale.tries.Try
import rx.Observable

interface ResourceView {

    fun search(c: SearchCriteria = SearchCriteria()): Try<out ResourcePage>

}

interface ResourcePage {

    fun resources(): Observable<DataEvent<Resource>>

    fun next(): Try<out ResourcePage>

    fun hasNext(): Boolean

}
