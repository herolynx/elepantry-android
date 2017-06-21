package com.herolynx.elepantry.ext.dropbox

import com.dropbox.core.v2.files.FileMetadata
import com.herolynx.elepantry.core.generic.toISO8601
import com.herolynx.elepantry.resources.core.model.Resource
import com.herolynx.elepantry.resources.core.model.ResourceType

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