package com.herolynx.elepantry.ext.dropbox

import com.dropbox.core.v2.files.DbxUserFilesRequests
import com.dropbox.core.v2.files.SearchResult
import com.herolynx.elepantry.core.log.warn
import com.herolynx.elepantry.core.rx.DataEvent
import com.herolynx.elepantry.resources.core.model.Resource
import com.herolynx.elepantry.resources.core.service.ResourcePage
import org.funktionale.tries.Try
import rx.Observable

internal class DropBoxSearchPage(
        private val searchResult: SearchResult,
        private val files: DbxUserFilesRequests,
        private val nextPage: (Long) -> SearchResult
) : ResourcePage {

    override fun resources(): Observable<DataEvent<Resource>> = Observable.from(searchResult.matches)
            .map { m -> m.toResource() }
            .filter { m -> m.isDefined() }
            .map { r -> DataEvent(r.get(), deleted = false) }

    override fun next(): Try<out ResourcePage> = Try {
        DropBoxSearchPage(
                nextPage(searchResult.start),
                files,
                nextPage
        )
    }
            .onFailure { ex -> warn("[DropBox] Getting search page data error", ex) }

    override fun hasNext(): Boolean = searchResult.more
}