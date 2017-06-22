package com.herolynx.elepantry.repository

import com.herolynx.elepantry.repository.Repository
import com.herolynx.elepantry.ext.google.firebase.db.FirebaseDb
import com.herolynx.elepantry.ext.google.firebase.db.TagRepository
import com.herolynx.elepantry.resources.core.model.Resource
import com.herolynx.elepantry.resources.core.model.Tag
import com.herolynx.elepantry.resources.core.model.View

class RepoConfig internal constructor() {

    fun userViews(): Repository<View> = FirebaseDb.userViews()

    fun userResources(): Repository<Resource> = FirebaseDb.userResources()

    fun tags(): Repository<Tag> = TagRepository(userResources())

}