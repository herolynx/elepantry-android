package com.herolynx.elepantry.resources.model

import org.funktionale.option.toOption

enum class ResourceType {
    GOOGLE
}

data class Resource(
        val id: String = newId(),
        val type: ResourceType,
        val name: String,
        val tags: List<Tag> = listOf(),
        val createdTime: String? = null,
        val lastModifiedDate: String? = null,
        val downloadLink: String? = null,
        val extension: String? = null
) {
    constructor() : this(name = "", type = ResourceType.GOOGLE)

    fun <C : Collection<String>> containsAny(names: C) = tags.find { t -> names.contains(t.name) }.toOption().isDefined()

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        val v = other as Resource?
        return v?.id.equals(id)
    }
}
