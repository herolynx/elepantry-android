package com.herolynx.elepantry.resources.view

import com.herolynx.elepantry.config.Config
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.repository.Repository
import com.herolynx.elepantry.resources.model.Tag
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

    private fun save(changed: View, tagsChanged: Boolean = false) {
        repository.save(changed)
        v = changed
        if (tagsChanged) {
            view.displayTags(v.tags)
        }
    }

    fun changeName(name: String) {
        debug("$TAG Changing name - view: $v, new name: $name")
        save(v.copy(name = name))
    }

    fun addTag(name: String) {
        debug("$TAG Add tag - view: $v, new tag: $name")
        save(v.copy(tags = v.tags.add(name)), tagsChanged = true)
    }

    fun deleteTag(t: Tag) {
        debug("$TAG Delete tag - view: $v, delete tag: $t")
        save(v.copy(tags = v.tags.remove(t)), tagsChanged = true)
    }

}