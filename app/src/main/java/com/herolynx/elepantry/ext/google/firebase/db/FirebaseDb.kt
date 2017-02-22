package com.herolynx.elepantry.ext.google.firebase.db

import com.google.firebase.database.FirebaseDatabase
import com.herolynx.elepantry.ext.google.firebase.auth.FirebaseAuth
import com.herolynx.elepantry.resources.model.Resource
import com.herolynx.elepantry.resources.model.View
import com.herolynx.elepantry.user.model.UserId

object FirebaseDb {

    private val userId = UserId(FirebaseAuth.getCurrentUser().get().uid)
    private val database = FirebaseDatabase.getInstance()

    val userViews = userRepo("views", View::class.java, View::id)

    val userResources = userRepo("resources", Resource::class.java, Resource::id)

    private fun <T> userRepo(name: String, entityClass: Class<T>, idGetter: (T) -> String) = FirebaseRepository(
            database.getReference(name).child(userId.uid),
            entityClass,
            idGetter
    )

}