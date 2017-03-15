package com.herolynx.elepantry.resources.model

import org.funktionale.option.toOption

enum class ResourceType {
    GOOGLE
}

data class Resource(
        val id: String = newId(),
        val type: ResourceType,
        val name: String,
        val mimeType: String? = null,
        val tags: List<Tag> = listOf(),
        val createdTime: String? = null,
        val lastModifiedDate: String? = null,
        val version: String? = null,
        val webViewLink: String? = null,
        val downloadLink: String? = null,
        val thumbnailLink: String? = null,
        val extension: String? = null
) {
    constructor() : this(name = "", type = ResourceType.GOOGLE)

    fun <C : Collection<String>> containsAny(names: C) = tags.find { t -> names.contains(t.name) }.toOption().isDefined()

    fun containsText(search: String): Boolean {
        if (name.startsWith(search)) {
            return true
        }
        return !tags
                .filter { t -> t.name.startsWith(search) }
                .toOption()
                .isDefined()
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        val v = other as? Resource
        return v?.id.equals(id)
    }
}
