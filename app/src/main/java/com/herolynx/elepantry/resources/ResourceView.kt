package com.herolynx.elepantry.resources

import com.herolynx.elepantry.resources.model.Resource
import com.herolynx.elepantry.resources.model.SearchCriteria
import org.funktionale.tries.Try
import rx.Observable

interface ResourceView {

    fun search(c: SearchCriteria = SearchCriteria()): Try<out ResourcePage>

}

interface ResourcePage {

    fun resources(): Observable<Resource>

    fun next(): Try<out ResourcePage>

    fun hasNext(): Boolean

}
