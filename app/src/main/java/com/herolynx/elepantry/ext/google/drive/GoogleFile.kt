package com.herolynx.elepantry.ext.google.drive

import com.google.api.services.drive.model.File
import com.herolynx.elepantry.resources.model.Resource
import com.herolynx.elepantry.resources.model.ResourceType
import com.herolynx.elepantry.resources.model.Tag

fun File.toResource(): Resource {
    val f = this
    val ext = getExtension()
    return Resource(
            id = f.id,
            name = f.name,
            type = ResourceType.GOOGLE,
            extension = ext,
            tags = listOf(Tag(name = ext)),
            createdTime = f.createdTime?.toStringRfc3339(),
            lastModifiedDate = f.modifiedTime?.toStringRfc3339(),
            downloadLink = f.webViewLink
    )
}

internal fun File.getExtension(): String {
    if (fileExtension != null) {
        return fileExtension
    }
    val nameExtIdx = name.lastIndexOf('.')
    if (nameExtIdx > 0) {
        return name.substring(nameExtIdx + 1)
    }
    return mimeType.substring(mimeType.indexOf('/') + 1)
}