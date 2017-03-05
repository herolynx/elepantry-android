package com.herolynx.elepantry.resources.model

data class View(val id: String = newId(), val name: String, val tags: List<Tag> = listOf(), val type: ViewType = ViewType.DYNAMIC) {
    constructor() : this(name = "")

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        val v = other as? View
        return v?.id.equals(id)
    }

}

enum class ViewType {

    DYNAMIC,
    GOOGLE

}