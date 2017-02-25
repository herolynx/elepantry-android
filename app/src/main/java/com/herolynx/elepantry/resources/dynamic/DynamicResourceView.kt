package com.herolynx.elepantry.resources.dynamic

import com.herolynx.elepantry.resources.ResourcePage
import com.herolynx.elepantry.resources.ResourceView
import com.herolynx.elepantry.resources.model.Resource
import com.herolynx.elepantry.resources.model.SearchCriteria
import com.herolynx.elepantry.resources.model.View
import org.funktionale.tries.Try
import rx.Observable
import rx.Subscriber

class DynamicResourceView(
        v: View,
        private val resources: () -> Observable<Resource>
) : ResourceView {

    private val tagNames = v.tags.map { t -> t.name }.toCollection(mutableSetOf()).toSet()
    private val viewResources: MutableList<Resource> = mutableListOf()
    private val subscribers: MutableList<Subscriber<in Resource>> = mutableListOf()

    init {
        resources()
                .filter { r -> r.containsAny(tagNames) }
                .subscribe { r -> viewResources.add(r) }
    }

    override fun search(c: SearchCriteria) = Try {
        DynamicResourcePage(
                resources()
                        .filter { r -> r.containsAny(tagNames) }
        )
    }
}

class DynamicResourcePage(
        private val resources: Observable<Resource>
) : ResourcePage {

    override fun resources() = resources //Observable.from(resources)

    override fun next(): Try<ResourcePage> = Try.Failure(RuntimeException("No more data"))

    override fun hasNext() = false

}