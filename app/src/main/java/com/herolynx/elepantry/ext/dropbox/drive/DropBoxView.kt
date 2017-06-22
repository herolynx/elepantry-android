package com.herolynx.elepantry.ext.dropbox.drive

import com.dropbox.core.v2.DbxClientV2
import com.herolynx.elepantry.core.func.Retry
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.log.warn
import com.herolynx.elepantry.resources.core.service.ResourcePage
import com.herolynx.elepantry.resources.core.service.ResourceView
import com.herolynx.elepantry.resources.core.service.SearchCriteria
import org.funktionale.tries.Try

class DropBoxView(private val client: DbxClientV2) : ResourceView {

    private fun nextSearch(c: com.herolynx.elepantry.resources.core.service.SearchCriteria, start: Long = 0) =
            Retry.executeWithRetries(logic = {
                Try {
                    client.files()
                            .searchBuilder(DropBoxView.ROOT_PATH, c.text)
                            .withMaxResults(c.pageSize.toLong())
                            .withStart(start)
                            .start()
                }
            })
                    .onFailure { ex -> warn("[DropBox] Search API error", ex) }

    override fun search(c: SearchCriteria): Try<out ResourcePage> = Try {
        debug("[DropBox] Running search - criteria: $c")
        if (!c.text.isNullOrEmpty()) {
            debug("[DropBox] Search API - criteria: $c")
            DropBoxSearchPage(
                    nextSearch(c),
                    client.files(),
                    { from -> nextSearch(c, from) }
            )
        } else {
            debug("[DropBox] Search - using list API to list all files...")
            DropBoxListPage(
                    Retry.executeWithRetries(logic = {
                        Try {
                            client.files()
                                    .listFolderBuilder(ROOT_PATH)
                                    .withRecursive(true)
                                    .start()
                        }
                    }).onFailure { ex -> warn("[DropBox] List API error", ex) },
                    client.files()
            )
        }
    }
            .onFailure { ex -> warn("[DropBox] Search error - criteria: $c, path: ${DropBoxView.ROOT_PATH}", ex) }

    companion object {

        private val ROOT_PATH = ""

    }
}