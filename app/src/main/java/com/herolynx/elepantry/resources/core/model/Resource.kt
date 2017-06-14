package com.herolynx.elepantry.resources.core.model

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
        val iconLink: String? = null,
        val extension: String? = null
) {
    constructor() : this(name = "", type = ResourceType.GOOGLE)

    fun <C : Collection<String>> containsAny(names: C) = tags.find { t -> names.contains(t.name) }.toOption().isDefined()

    fun containsText(search: String): Boolean {
        if (name.contains(search, ignoreCase = true)) {
            return true
        }
        return tags
                .filter { t -> t.name.contains(search, ignoreCase = true) }
                .size > 0
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        val v = other as? Resource
        return v?.id.equals(id)
    }

    fun isTheSame(r2: Resource): Boolean = super.equals(r2.copy(tags = this.tags))

    fun merge(r2: Resource): Resource = this.copy(tags = r2.tags)

}

fun Resource.getTagValue() = if (tags.isEmpty()) "" else tags.map { t -> "#${t.name}" }.reduce { t, s -> "$t, $s" }
