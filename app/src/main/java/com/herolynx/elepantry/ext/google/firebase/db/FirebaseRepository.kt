package com.herolynx.elepantry.ext.google.firebase.db

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.herolynx.elepantry.core.log.error
import com.herolynx.elepantry.resources.model.User


class FirebaseRepository<T>(val ref: DatabaseReference) {

    fun read() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                error("[Firebase] onCancelled error", databaseError.toException())
            }
        }
        ref.addValueEventListener(postListener)
    }


}