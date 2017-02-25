package com.herolynx.elepantry.ext.google.drive

import com.google.api.services.drive.Drive
import com.herolynx.elepantry.core.log.error
import com.herolynx.elepantry.resources.ResourcePage
import com.herolynx.elepantry.resources.model.Resource
import org.funktionale.tries.Try
import rx.Observable

class GoogleDrivePage(
        private val list: (String?) -> Drive.Files.List,
        private val first: Boolean = false,
        private val nextPageToken: String? = null,
        private val files: List<Resource> = listOf()
) : ResourcePage {


    override fun resources() = Observable.from(files)

    override fun next(): Try<GoogleDrivePage> {
        if (!first && nextPageToken == null) {
            return Try.Failure(RuntimeException("No more data"))
        }
        return Try {
            val req = list(nextPageToken).execute()
            GoogleDrivePage(
                    list,
                    false,
                    req.nextPageToken,
                    req.files.map { f -> f.toResource() }
            )
        }
                .onFailure { ex -> error("[GoogleDrive] Getting page data error", ex) }
    }

    override fun hasNext(): Boolean = nextPageToken != null

    companion object {

        fun create(list: (String?) -> Drive.Files.List) = GoogleDrivePage(list, true, null).next()

    }

}