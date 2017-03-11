package com.herolynx.elepantry.resources.dynamic

import com.herolynx.elepantry.core.rx.DataEvent
import com.herolynx.elepantry.resources.ResourcePage
import com.herolynx.elepantry.resources.ResourceView
import com.herolynx.elepantry.resources.model.Resource
import com.herolynx.elepantry.resources.model.SearchCriteria
import com.herolynx.elepantry.resources.model.View
import org.funktionale.tries.Try
import rx.Observable

class DynamicResourceView(
        v: View,
        private val resources: () -> Observable<DataEvent<Resource>>
) : ResourceView {

    private val tagNames = v.tags.map { t -> t.name }.toCollection(mutableSetOf()).toSet()

    override fun search(c: SearchCriteria) = Try {
        DynamicResourcePage(
                resources()
                        .filter { r -> r.data.containsAny(tagNames) }
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