package com.herolynx.elepantry.ext.google.drive

import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.drive.Drive
import com.google.android.gms.drive.query.Query
import com.herolynx.elepantry.core.log.debug


class GoogleDrive(private val api: GoogleApiClient) {

    fun search(text: String = "") {
        val query = Query.Builder()
//                .setPageToken(mNextPageToken)
                .build()
        Drive.DriveApi
                .query(api, query)
                .setResultCallback { result ->
                    debug("[Drive] Result: %s", result.status)
                    if (result.status.isSuccess) {
                        result.metadataBuffer.forEach { item ->
                            debug("[Drive] File: %s", item.title)
                        }
                    }
                }
    }


}
