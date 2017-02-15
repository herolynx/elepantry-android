package com.herolynx.elepantry.resources.model

import java.util.*

data class Tag(val id: String = newId(), val name: String) {
    constructor() : this(name = "")
}

data class View(val id: String = newId(), val name: String, val tags: List<Tag> = listOf()) {
    constructor() : this(name = "")
}

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
) {
    constructor() : this(name = "", type = ResourceType.GOOGLE)
}
