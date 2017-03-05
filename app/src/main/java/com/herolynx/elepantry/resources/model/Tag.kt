package com.herolynx.elepantry.resources.model

data class Tag(val id: String = newId(), val name: String) {
    constructor() : this(name = "")

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        val v = other as? Tag
        return v?.id.equals(id)
    }

}

fun List<Tag>.add(name: String): List<Tag> {
    var t = toMutableList()
    t.add(Tag(name = name))
    return t.toList()
}


fun List<Tag>.remove(toDelete: Tag): List<Tag> = filter { t -> !t.equals(toDelete) }
