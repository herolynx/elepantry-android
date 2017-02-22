package com.herolynx.elepantry.resources.model

import com.herolynx.elepantry.user.model.UserId
import org.funktionale.option.toOption

data class UserViews(val userId: UserId, val views: List<View> = listOf()) {
    constructor() : this(UserId(""))
}

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
        val createdTime: String? = null,
        val lastModifiedDate: String? = null,
        val downloadLink: String? = null,
        val extension: String? = null
) {
    constructor() : this(name = "", type = ResourceType.GOOGLE)

    fun <C : Collection<String>> containsAny(names: C) = tags.find { t -> names.contains(t.name) }.toOption().isDefined()
}
