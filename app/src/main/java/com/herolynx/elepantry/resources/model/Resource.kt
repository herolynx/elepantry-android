package com.herolynx.elepantry.resources.model

import java.util.Date

data class Tag(val id: String = newId(), val name: String)

data class View(val id: String = newId(), val name: String, val tags: List<Tag>)

enum class ResourceType {
    GOOGLE
}

data class Resource(
        val id: String = newId(),
        val type: ResourceType,
        val name: String,
        val tags: List<Tag> = listOf(),
        val createdTime: Date? = null,
        val lastModifiedDate: Date? = null,
        val downloadLink: String? = null,
        val extension: String? = null
)
