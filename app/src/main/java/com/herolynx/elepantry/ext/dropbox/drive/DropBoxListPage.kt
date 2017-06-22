package com.herolynx.elepantry.ext.dropbox.drive

import com.dropbox.core.v2.files.DbxUserFilesRequests
import com.dropbox.core.v2.files.ListFolderResult
import com.herolynx.elepantry.core.func.Retry
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.log.warn
import com.herolynx.elepantry.core.rx.DataEvent
import com.herolynx.elepantry.resources.core.model.Resource
import com.herolynx.elepantry.resources.core.service.ResourcePage
import org.funktionale.tries.Try

internal class DropBoxListPage(
        private val folder: Try<ListFolderResult>,
        private val files: DbxUserFilesRequests
) : ResourcePage {

    override fun resources(): rx.Observable<DataEvent<Resource>> {
        debug("[DropBox] List page - size: ${folder.map { f -> f.entries.size }.getOrElse { 0 }}")
        return rx.Observable.from(folder.map { f -> f.entries }.getOrElse { listOf() })
                .map { m -> m.toResource() }
                .filter { m -> m.isDefined() }
                .map { r -> com.herolynx.elepantry.core.rx.DataEvent(r.get(), deleted = false) }
    }

    override fun next(): org.funktionale.tries.Try<out ResourcePage> =
            if (folder.isFailure()) Try.Failure(RuntimeException("DropBox - last page wasn't loaded so cannot load the next one"))
            else {
                org.funktionale.tries.Try {
                    DropBoxListPage(
                            Retry.executeWithRetries(logic = {
                                Try {
                                    files.listFolderContinue(folder.get().cursor)
                                }
                            })
                            ,
                            files
                    )
                }
                        .onFailure { ex -> warn("[DropBox] Getting page data error", ex) }
            }

    override fun hasNext(): Boolean = folder.map { f -> f.hasMore }.getOrElse { false }
}