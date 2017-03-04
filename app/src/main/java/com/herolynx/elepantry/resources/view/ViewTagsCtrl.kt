package com.herolynx.elepantry.resources.view

import com.herolynx.elepantry.config.Config
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.repository.Repository
import com.herolynx.elepantry.resources.model.View
import com.herolynx.elepantry.resources.model.add
import com.herolynx.elepantry.resources.model.remove

class ViewTagsCtrl(
        private val view: ResourceTagsActivity,
        private val repository: Repository<View> = Config.repository.userViews()
) {

    private val TAG = "[ViewTagsCtrl]"

    private var v: View = View(id = "", name = "")

    fun init(v: View) {
        debug("$TAG Init - view: $v")
        this.v = v
    }

    private fun save(changed: View) {
        repository.save(changed)
        v = changed
    }

    fun changeName(name: String) {
        debug("$TAG Changing name - view: $v, new name: $name")
        save(v.copy(name = name))
    }

    fun addTag(name: String) {
        debug("$TAG Add tag - view: $v, new tag: $name")
        save(v.copy(tags = v.tags.add(name)))
    }

    fun deleteTag(name: String) {
        debug("$TAG Delete tag - view: $v, delete tag: $name")
        save(v.copy(tags = v.tags.remove(name)))
    }

    fun getTags() = v.tags

}