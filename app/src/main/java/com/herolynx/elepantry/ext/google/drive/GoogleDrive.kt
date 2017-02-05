package com.herolynx.elepantry.ext.google.drive

//import com.google.android.gms.drive.Drive
//import com.google.android.gms.drive.query.Filters
//import com.google.android.gms.drive.query.Query
//import com.google.android.gms.drive.query.SearchableField
import com.google.android.gms.common.api.GoogleApiClient
import com.herolynx.elepantry.core.log.debug


class GoogleDrive(private val api: GoogleApiClient) {

    fun search(text: String = "") {
        debug("[GDrive] Searching: %s", text)

//        val dir = Drive.DriveApi.getAppFolder(api)
//        debug("[GDrive] Dir: %s", dir.driveId)
//        val query = Query.Builder()
//                .addFilter(Filters.contains(SearchableField.TITLE, text))
////                .setPageToken(mNextPageToken)
//                .build()
//        Drive.DriveApi
//                .query(api, query)
//                .setResultCallback { result ->
//                    debug("[GDrive] Result: %s", result.status)
//                    if (result.status.isSuccess) {
//                        result.metadataBuffer.forEach { item ->
//                            debug("[GDrive] File: %s", item.title)
//                        }
//                    }
//                }
    }

//    companion object Factory {
//
//        fun create(c: Context): GoogleDrive {
//            val mGoogleApiClient = GoogleApiClient.Builder(c)
//                    .addApi(Drive.API)
//                    .addScope(Drive.SCOPE_FILE)
//                    .addOnConnectionFailedListener { r ->
//                        error("[GDrive] Couldn't connect to Google Drive - code: " + r.errorCode + ", msg: " + r.errorMessage)
//                    }
//                    .build()
//            mGoogleApiClient.connect()
//            return GoogleDrive(mGoogleApiClient)
//        }
//
//    }

}
