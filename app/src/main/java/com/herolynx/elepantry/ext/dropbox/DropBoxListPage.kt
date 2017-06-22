package com.herolynx.elepantry.ext.dropbox

import com.dropbox.core.v2.files.DbxUserFilesRequests
import com.dropbox.core.v2.files.ListFolderResult
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.log.warn
import com.herolynx.elepantry.core.rx.DataEvent
import com.herolynx.elepantry.resources.core.model.Resource
import com.herolynx.elepantry.resources.core.service.ResourcePage
import org.funktionale.tries.Try
import rx.Observable

internal class DropBoxListPage(
        private val folder: ListFolderResult,
        private val files: DbxUserFilesRequests
) : ResourcePage {

    override fun resources(): Observable<DataEvent<Resource>> {
        debug("[DropBox] List page - size: ${folder.entries.size}")
        return Observable.from(folder.entries)
                .map { m -> m.toResource() }
                .filter { m -> m.isDefined() }
                .map { r -> DataEvent(r.get(), deleted = false) }
    }

    override fun next(): Try<out ResourcePage> = Try {
        DropBoxListPage(files.listFolderContinue(folder.cursor), files)
    }
            .onFailure { ex -> warn("[DropBox] Getting page data error", ex) }

    override fun hasNext(): Boolean = folder.hasMore
}