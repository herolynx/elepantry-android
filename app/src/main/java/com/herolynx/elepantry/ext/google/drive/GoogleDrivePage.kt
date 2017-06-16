package com.herolynx.elepantry.ext.google.drive

import com.google.api.services.drive.Drive
import com.herolynx.elepantry.core.func.Retry
import com.herolynx.elepantry.core.log.warn
import com.herolynx.elepantry.core.rx.DataEvent
import com.herolynx.elepantry.resources.core.model.Resource
import com.herolynx.elepantry.resources.core.service.ResourcePage
import org.funktionale.tries.Try
import rx.Observable

internal class GoogleDrivePage(
        private val list: (String?) -> Drive.Files.List,
        private val first: Boolean = false,
        private val nextPageToken: String? = null,
        private val files: List<Resource> = listOf()
) : ResourcePage {

    override fun resources() = Observable.from(files.map { f -> DataEvent(f) })

    override fun next(): Try<GoogleDrivePage> {
        if (!first && nextPageToken == null) {
            return Try.Failure(RuntimeException("No more data"))
        }
        return Retry.executeWithRetries(logic = {
            Try {
                val req = list(nextPageToken).execute()
                GoogleDrivePage(
                        list,
                        false,
                        req.nextPageToken,
                        req.files.map { f -> f.toResource() }
                )
            }
        })
                .onFailure { ex -> warn("[GoogleDrive] Getting page data error", ex) }
    }

    override fun hasNext(): Boolean = nextPageToken != null

    companion object {

        fun create(list: (String?) -> Drive.Files.List) = GoogleDrivePage(list, true, null).next()

    }

}