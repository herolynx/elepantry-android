package com.herolynx.elepantry.ext.google.firebase.db

import com.herolynx.elepantry.resources.model.Tag
import com.herolynx.elepantry.resources.model.View

object SampleData {

    fun initViews() {
        val r = FirebaseDb.userViews
        r.save(View(name = "images", tags = listOf(Tag(name = "png"), Tag(name = "jpeg"))))
        r.save(View(name = "docs", tags = listOf(Tag(name = "pdf"), Tag(name = "doc"))))
    }

}