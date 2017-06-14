package com.herolynx.elepantry.ext.google.firebase.db

import com.google.firebase.database.FirebaseDatabase
import com.herolynx.elepantry.ext.google.firebase.auth.FirebaseAuth
import com.herolynx.elepantry.resources.core.model.Resource
import com.herolynx.elepantry.resources.core.model.View

object FirebaseDb {

    private val database = FirebaseDatabase.getInstance()

    private fun userId() = FirebaseAuth.getCurrentUser().get().uid

    fun userViews() = userRepo("views", View::class.java, View::id)

    fun userResources() = userRepo("resources", Resource::class.java, Resource::id)

    private fun <T> userRepo(name: String, entityClass: Class<T>, idGetter: (T) -> String) = FirebaseRepository(
            database.getReference(name).child(userId()),
            entityClass,
            idGetter
    )

}