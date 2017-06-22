package com.herolynx.elepantry.resources.core.model

import com.herolynx.elepantry.drive.DriveType

data class View(
        val id: Id = newId(),
        val name: String,
        val tags: List<Tag> = listOf(),
        val type: ViewType = ViewType.DYNAMIC
) {
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
    GOOGLE,
    DROP_BOX;

    fun driveType(): DriveType = when (this) {
        GOOGLE -> DriveType.GOOGLE_DRIVE
        DROP_BOX -> DriveType.DROP_BOX

        else -> throw IllegalArgumentException("No drive supported for view type: $this")

    }

}