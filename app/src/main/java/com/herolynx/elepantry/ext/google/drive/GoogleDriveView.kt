package com.herolynx.elepantry.ext.google.drive

import com.google.api.services.drive.Drive
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.resources.core.service.ResourcePage
import com.herolynx.elepantry.resources.core.service.ResourceView
import com.herolynx.elepantry.resources.core.service.SearchCriteria
import org.funktionale.tries.Try

class GoogleDriveView(private val service: Drive) : ResourceView {

    override fun search(c: SearchCriteria): Try<out ResourcePage> = GoogleDrivePage.create { nextPageToken ->
        debug("[GoogleDriveView] Search - criteria: $c")
        service.files()
                .list()
                .setFields(DOWNLOAD_FIELDS)
                .setQ(String.format("$QUERY_BY_NAME and $QUERY_NOT_DIRECTORY and $QUERY_NOT_TRASHED", c.text ?: ""))
                .setPageSize(c.pageSize)
                .setSpaces("$SPACE_DRIVE,$SPACE_PHOTOS")
                .setPageToken(nextPageToken)
    }

    companion object Factory {

        private val DOWNLOAD_FIELDS = "nextPageToken, files(id,name,mimeType,createdTime,modifiedTime,webContentLink,webViewLink,thumbnailLink,iconLink)"
        private val QUERY_BY_NAME = "name contains '%s'"
        private val QUERY_NOT_DIRECTORY = "mimeType != 'application/vnd.google-apps.folder'"
        private val QUERY_NOT_TRASHED = "trashed=false"

        private val SPACE_DRIVE = "drive"
        private val SPACE_PHOTOS = "photos"

    }

}
