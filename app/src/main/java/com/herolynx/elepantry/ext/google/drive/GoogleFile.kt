package com.herolynx.elepantry.ext.google.drive

import com.google.api.services.drive.model.File
import com.herolynx.elepantry.resources.core.model.Resource
import com.herolynx.elepantry.resources.core.model.ResourceType

fun File.toResource(): Resource {
    val f = this
    return Resource(
            id = f.id,
            name = f.name,
            type = ResourceType.GOOGLE,
            extension = getExtension(),
            mimeType = f.mimeType,
            tags = listOf(),
            createdTime = f.createdTime?.toStringRfc3339(),
            lastModifiedDate = f.modifiedTime?.toStringRfc3339(),
            webViewLink = f.webViewLink,
            downloadLink = f.webContentLink,
            thumbnailLink = f.thumbnailLink,
            iconLink = f.iconLink,
            version = "" + f.version
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