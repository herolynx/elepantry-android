package com.herolynx.elepantry.config

import com.herolynx.elepantry.core.repository.Repository
import com.herolynx.elepantry.ext.google.firebase.db.FirebaseDb
import com.herolynx.elepantry.resources.model.Resource
import com.herolynx.elepantry.resources.model.View

class RepoConfig internal constructor() {

    fun userViews(): Repository<View> = FirebaseDb.userViews()

    fun userResources(): Repository<Resource> = FirebaseDb.userResources()

}