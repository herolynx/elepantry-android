package com.herolynx.elepantry.ext.dropbox

import com.dropbox.core.v2.files.FileMetadata
import com.dropbox.core.v2.files.SearchMatch
import com.herolynx.elepantry.core.generic.toISO8601
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.resources.core.model.Resource
import com.herolynx.elepantry.resources.core.model.ResourceType
import org.funktionale.option.Option

internal fun FileMetadata.toResource(): Resource {
    val f = this
    return Resource(
            id = f.id,
            name = f.name,
            type = ResourceType.DROP_BOX,
            extension = "",
            mimeType = "",
            tags = listOf(),
            createdTime = f.serverModified.toISO8601().getOrElse { "" },
            lastModifiedDate = f.clientModified.toISO8601().getOrElse { "" },
            webViewLink = f.pathLower,
            downloadLink = f.pathDisplay,
            thumbnailLink = null,
            iconLink = null,
            version = f.rev
    )
}

internal fun SearchMatch.toResource(): Option<Resource> {
    if (!matchType.name.equals("filename", ignoreCase = true)) return Option.None
    val f = metadata as FileMetadata
    return Option.Some(Resource(
            id = f.id,
            name = f.name,
            type = ResourceType.DROP_BOX,
            extension = "",
            mimeType = "",
            tags = listOf(),
            createdTime = f.serverModified.toISO8601().getOrElse { "" },
            lastModifiedDate = f.clientModified.toISO8601().getOrElse { "" },
            webViewLink = f.pathLower,
            downloadLink = f.pathDisplay,
            thumbnailLink = null,
            iconLink = null,
            version = f.rev
    ))
}