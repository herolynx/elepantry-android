package com.herolynx.elepantry.resources.dynamic

import com.herolynx.elepantry.resources.ResourceView
import com.herolynx.elepantry.resources.model.SearchCriteria
import com.herolynx.elepantry.resources.model.View
import org.funktionale.tries.Try

class DynamicResourceView(
        private val v: View,
        private val views: List<ResourceView>
) : ResourceView {

    private val tagNames = v.tags.map { t -> t.name }.toCollection(mutableSetOf()).toSet()

    override fun search(c: SearchCriteria) = Try {
        DynamicResourcePage(
                tagNames,
                views.map { rv -> rv.search(c) }
                        .filter { r -> r.isSuccess() }
                        .map { r -> r.get() }
        )
    }
}