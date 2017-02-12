package com.herolynx.elepantry.ext.google.drive

import com.google.api.services.drive.Drive
import com.herolynx.elepantry.resources.Resource
import rx.Observable

class GoogleDriveSearch(
        private val list: (String?) -> Drive.Files.List,
        private val first: Boolean = false,
        private val nextPageToken: String? = null,
        val files: List<Resource> = listOf()
) {

    fun next(): Observable<GoogleDriveSearch> {
        if (!first && nextPageToken == null) {
            return Observable.empty()
        }
        return Observable.defer {
            val req = list(nextPageToken).execute()
            Observable.just(GoogleDriveSearch(
                    list,
                    false,
                    req.nextPageToken,
                    req.files.map { f -> Resource(f.name) }
            ))
        }
    }

}