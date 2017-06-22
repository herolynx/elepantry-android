package com.herolynx.elepantry.ext.dropbox.drive

import com.dropbox.core.v2.files.DbxUserFilesRequests
import com.dropbox.core.v2.files.SearchResult
import com.herolynx.elepantry.core.log.warn
import com.herolynx.elepantry.core.rx.DataEvent
import com.herolynx.elepantry.resources.core.model.Resource
import com.herolynx.elepantry.resources.core.service.ResourcePage

internal class DropBoxSearchPage(
        private val searchResult: SearchResult,
        private val files: DbxUserFilesRequests,
        private val nextPage: (Long) -> SearchResult
) : ResourcePage {

    override fun resources(): rx.Observable<DataEvent<Resource>> = rx.Observable.from(searchResult.matches)
            .map { m -> m.toResource() }
            .filter { m -> m.isDefined() }
            .map { r -> com.herolynx.elepantry.core.rx.DataEvent(r.get(), deleted = false) }

    override fun next(): org.funktionale.tries.Try<out ResourcePage> = org.funktionale.tries.Try {
        DropBoxSearchPage(
                nextPage(searchResult.start),
                files,
                nextPage
        )
    }
            .onFailure { ex -> warn("[DropBox] Getting search page data error", ex) }

    override fun hasNext(): Boolean = searchResult.more
}