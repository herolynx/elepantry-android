package com.herolynx.elepantry.resources.model

data class UserViews(val userId: UserId, val views: List<View> = listOf()) {
    constructor() : this(UserId(""))
}

data class UserId(val uid: String) {
    constructor() : this("")
}