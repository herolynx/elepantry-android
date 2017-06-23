package com.herolynx.elepantry.ext.dropbox.drive

import android.os.Environment
import com.dropbox.core.v2.files.FileMetadata
import com.dropbox.core.v2.files.FolderMetadata
import com.dropbox.core.v2.files.Metadata
import com.dropbox.core.v2.files.SearchMatch
import com.herolynx.elepantry.core.generic.toISO8601
import com.herolynx.elepantry.drive.DriveType
import com.herolynx.elepantry.resources.core.model.Resource
import org.funktionale.option.Option

internal fun Metadata.toResource(): Option<Resource> {
    if (this is FolderMetadata) return Option.None
    else return Option.Some((this as FileMetadata).toResource())
}

internal fun FileMetadata.toResource(): Resource {
    val f = this
    return Resource(
            id = f.id,
            name = f.name,
            type = DriveType.DROP_BOX,
            extension = getExtension(),
            mimeType = "",
            tags = listOf(),
            createdTime = f.serverModified.toISO8601().getOrElse { "" },
            lastModifiedDate = f.clientModified.toISO8601().getOrElse { "" },
            webViewLink = f.pathLower,
            downloadLink = Environment.getExternalStorageDirectory().getPath() + f.pathDisplay,
            thumbnailLink = f.pathLower,
            iconLink = null,
            version = f.rev
    )
}

internal fun SearchMatch.toResource(): Option<Resource> = metadata.toResource()

internal fun FileMetadata.getExtension(): String? {
    val nameExtIdx = name.lastIndexOf('.')
    if (nameExtIdx > 0) {
        return name.substring(nameExtIdx + 1)
    }
    return null
}