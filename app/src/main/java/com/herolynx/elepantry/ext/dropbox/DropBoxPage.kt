package com.herolynx.elepantry.ext.dropbox

import com.dropbox.core.v2.files.DbxUserFilesRequests
import com.dropbox.core.v2.files.FileMetadata
import com.dropbox.core.v2.files.ListFolderResult
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.log.warn
import com.herolynx.elepantry.core.rx.DataEvent
import com.herolynx.elepantry.resources.core.model.Resource
import com.herolynx.elepantry.resources.core.service.ResourcePage
import org.funktionale.tries.Try
import rx.Observable

internal class DropBoxPage(
        private val folder: ListFolderResult,
        private val files: DbxUserFilesRequests
) : ResourcePage {

    override fun resources(): Observable<DataEvent<Resource>> = Observable.from(folder.entries)
            .filter { m -> m is FileMetadata }
            .map { m -> m as FileMetadata }
            .map { m ->
                debug("[DropBox] m: $m, resource: ${m.toResource()}")
                m.toResource()
            }
            .map { r -> DataEvent(r, deleted = false) }

    override fun next(): Try<out ResourcePage> = Try {
        DropBoxPage(files.listFolderContinue(folder.cursor), files)
    }
            .onFailure { ex -> warn("[DropBox] Getting page data error", ex) }

    override fun hasNext(): Boolean = folder.hasMore
}