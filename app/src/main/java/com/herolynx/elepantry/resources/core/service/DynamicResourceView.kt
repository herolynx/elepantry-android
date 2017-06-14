package com.herolynx.elepantry.resources.core.service

import com.herolynx.elepantry.core.rx.DataEvent
import com.herolynx.elepantry.resources.core.model.Resource
import com.herolynx.elepantry.resources.core.model.View
import org.funktionale.tries.Try
import rx.Observable

class DynamicResourceView(
        private val v: View,
        private val resources: () -> Observable<DataEvent<Resource>>
) : ResourceView {

    override fun search(c: SearchCriteria) = Try {
        DynamicResourcePage(
                resources()
                        .filter { r -> r.data.containsAny(v.tags) }
                        .filter { r -> if (c.text != null) r.data.containsText(c.text) else true }
        )
    }
}

class DynamicResourcePage(
        private val resources: Observable<DataEvent<Resource>>
) : ResourcePage {

    override fun resources() = resources

    override fun next(): Try<ResourcePage> = Try.Failure(RuntimeException("No more data"))

    override fun hasNext() = false

}