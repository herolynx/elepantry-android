package com.herolynx.elepantry.ext.google.firebase.db

import com.google.firebase.database.FirebaseDatabase
import com.herolynx.elepantry.ext.google.firebase.auth.FirebaseAuth


object FirebaseDb {

    val user = FirebaseAuth.getCurrentUser().get().uid
    private val database = FirebaseDatabase.getInstance()
    var myRef = database.getReference("message").

}