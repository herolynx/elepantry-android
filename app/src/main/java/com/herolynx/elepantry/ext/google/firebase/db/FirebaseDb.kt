package com.herolynx.elepantry.ext.google.firebase.db

import com.google.firebase.database.FirebaseDatabase
import com.herolynx.elepantry.ext.google.firebase.auth.FirebaseAuth
import com.herolynx.elepantry.resources.model.UserViews
import com.herolynx.elepantry.user.model.UserId

object FirebaseDb {

    private val userId = UserId(FirebaseAuth.getCurrentUser().get().uid)
    private val database = FirebaseDatabase.getInstance()

    val userViews = userRepo("views", UserViews::class.java)

    private fun <T> userRepo(name: String, entityClass: Class<T>) = FirebaseRepository(
            database.getReference(name).child(userId.uid),
            entityClass
    )

}