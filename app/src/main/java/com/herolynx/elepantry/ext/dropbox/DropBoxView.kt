package com.herolynx.elepantry.ext.dropbox

import android.app.Activity
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.http.OkHttp3Requestor
import com.dropbox.core.v2.DbxClientV2
import com.herolynx.elepantry.auth.Token
import com.herolynx.elepantry.config.Config
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.log.warn
import com.herolynx.elepantry.resources.core.service.ResourcePage
import com.herolynx.elepantry.resources.core.service.ResourceView
import com.herolynx.elepantry.resources.core.service.SearchCriteria
import org.funktionale.tries.Try
import rx.Observable

class DropBoxView(private val client: DbxClientV2) : ResourceView {

    private fun nextSearch(c: SearchCriteria, start: Long = 0) = client.files()
            .searchBuilder(ROOT_PATH, c.text)
            .withMaxResults(c.pageSize.toLong())
            .withStart(start)
            .start()

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
                    client.files()
                            .listFolderBuilder(ROOT_PATH)
                            .withRecursive(true)
                            .start(),
                    client.files()
            )
        }
    }
            .onFailure { ex -> warn("[DropBox] Search error - criteria: $c, path: $ROOT_PATH", ex) }

    companion object {

        private val ROOT_PATH = ""

        fun create(token: Token): DropBoxView {
            val requestConfig = DbxRequestConfig.newBuilder(Config.elepantryUrl)
                    .withHttpRequestor(OkHttp3Requestor(OkHttp3Requestor.defaultOkHttpClient()))
                    .build()
            return DropBoxView(DbxClientV2(requestConfig, token))
        }

        fun create(a: Activity): Observable<DropBoxView> = DropBoxAuth.getToken(a)
                .map { token ->
                    create(token)
                }

    }
}