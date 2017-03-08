package com.herolynx.elepantry.ext.google.firebase.db

import com.google.firebase.database.FirebaseDatabase
import com.herolynx.elepantry.ext.google.firebase.auth.FirebaseAuth
import com.herolynx.elepantry.resources.model.Resource
import com.herolynx.elepantry.resources.model.View
import com.herolynx.elepantry.user.model.UserId

object FirebaseDb {

    private val database = FirebaseDatabase.getInstance()

    private fun userId() = UserId(FirebaseAuth.getCurrentUser().get().uid)

    //TODO support chars: $ # [ ]
    private fun orderId(id: String, name: String) = name.replace('.', '_') + "_" + id.substring(0, 10)

    fun userViews() = userRepo("views", View::class.java, { v -> orderId(v.id, v.name) })

    fun userResources() = userRepo("resources", Resource::class.java, { r -> orderId(r.id, r.name) })

    private fun <T> userRepo(name: String, entityClass: Class<T>, idGetter: (T) -> String) = FirebaseRepository(
            database.getReference(name).child(userId().uid),
            entityClass,
            idGetter
    )

}