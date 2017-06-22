package com.herolynx.elepantry.ext.dropbox.drive

import com.dropbox.core.v2.DbxClientV2
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.log.warn
import com.herolynx.elepantry.resources.core.service.ResourcePage
import com.herolynx.elepantry.resources.core.service.ResourceView
import com.herolynx.elepantry.resources.core.service.SearchCriteria

class DropBoxView(private val client: DbxClientV2) : ResourceView {

    private fun nextSearch(c: com.herolynx.elepantry.resources.core.service.SearchCriteria, start: Long = 0) = client.files()
            .searchBuilder(com.herolynx.elepantry.ext.dropbox.drive.DropBoxView.Companion.ROOT_PATH, c.text)
            .withMaxResults(c.pageSize.toLong())
            .withStart(start)
            .start()

    override fun search(c: SearchCriteria): org.funktionale.tries.Try<out ResourcePage> = org.funktionale.tries.Try {
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
                    client.files()
                            .listFolderBuilder(ROOT_PATH)
                            .withRecursive(true)
                            .start(),
                    client.files()
            )
        }
    }
            .onFailure { ex -> warn("[DropBox] Search error - criteria: $c, path: ${DropBoxView.ROOT_PATH}", ex) }

    companion object {

        private val ROOT_PATH = ""

        fun create(token: com.herolynx.elepantry.auth.Token): com.herolynx.elepantry.ext.dropbox.drive.DropBoxView {
            val requestConfig = com.dropbox.core.DbxRequestConfig.newBuilder(com.herolynx.elepantry.config.Config.elepantryUrl)
                    .withHttpRequestor(com.dropbox.core.http.OkHttp3Requestor(com.dropbox.core.http.OkHttp3Requestor.defaultOkHttpClient()))
                    .build()
            return com.herolynx.elepantry.ext.dropbox.drive.DropBoxView(com.dropbox.core.v2.DbxClientV2(requestConfig, token))
        }

        fun create(a: android.app.Activity): rx.Observable<DropBoxView> = com.herolynx.elepantry.ext.dropbox.auth.DropBoxAuth.getToken(a)
                .map { token ->
                    com.herolynx.elepantry.ext.dropbox.drive.DropBoxView.Companion.create(token)
                }

    }
}