package com.herolynx.elepantry.resources.model

data class UserId(val uid: String)

data class User(val userId: UserId, val views: List<View>)

data class Id(val userId: UserId, val id: String = newId())