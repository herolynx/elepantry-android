package com.herolynx.elepantry.resources.dynamic

import com.herolynx.elepantry.resources.ResourcePage
import com.herolynx.elepantry.resources.model.Resource
import org.funktionale.option.toOption
import org.funktionale.tries.Try
import rx.Observable

class DynamicResourcePage(
        private val tagNames: Set<String>,
        private val pages: List<ResourcePage>
) : ResourcePage {

    override fun resources(f: (Resource) -> Boolean) = Observable.merge(
            pages.map { p ->
                p.resources(f)
                        .filter { r -> r.containsAny(tagNames) }
            }
    )

    override fun next() = Try {
        DynamicResourcePage(
                tagNames,
                pages
                        .filter { p -> p.hasNext() }
                        .map { p -> p.next() }
                        .filter { next -> next.isSuccess() }
                        .map { next -> next.get() }
        )
    }

    override fun hasNext() = pages.find { p -> p.hasNext() }.toOption().isDefined()

}