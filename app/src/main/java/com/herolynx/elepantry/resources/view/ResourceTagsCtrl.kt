package com.herolynx.elepantry.resources.view

import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.repository.Repository
import com.herolynx.elepantry.resources.model.Tag
import com.herolynx.elepantry.resources.model.add
import com.herolynx.elepantry.resources.model.remove
import org.funktionale.option.Option
import org.funktionale.option.getOrElse
import org.funktionale.option.toOption

internal class ResourceTagsCtrl<T>(
        private val view: ResourceTagsActivity,
        private val repository: Repository<T>,
        private val nameGetter: (T) -> String,
        private val nameChange: Option<(T, String) -> T>,
        private val tagsGetter: (T) -> List<Tag>,
        private val tagsSetter: (T, List<Tag>) -> T
) {

    private val TAG = "[TagsCtrl]"

    private var r: T? = null

    fun init(r: T) {
        debug("$TAG Init - resource: $r")
        this.r = r
        refresh(r)
    }

    private fun refresh(r: T?) {
        r.toOption().map { res ->
            view.displayName(nameGetter(res))
            view.displayTags(tagsGetter(res))
        }
    }

    private fun save(changed: T, tagsChanged: Boolean = false): T? {
        repository.save(changed)
        r = changed
        if (tagsChanged) {
            refresh(r)
        }
        return r
    }

    fun delete() {
        debug("$TAG Deleting - resource: $r")
        r.toOption()
                .map { res ->
                    repository.delete(res)
                            .subscribe { ResourcesActivity.navigate(view) }
                }
    }

    fun canChangeName() = nameChange.isDefined()

    fun changeName(name: String): T? {
        return nameChange
                .flatMap { logic -> r.toOption().map { res -> Pair(logic, res) } }
                .map { logicAndData ->
                    debug("$TAG Changing name - resource: $r, new name: $name")
                    save(logicAndData.first(logicAndData.second, name))
                }
                .getOrElse { r }

    }

    fun addTag(name: String): T? = changeTags(r, { tags -> tags.add(name) })

    fun deleteTag(t: Tag): T? = changeTags(r, { tags -> tags.remove(t) })

    private fun changeTags(r: T?, changeTags: (List<Tag>) -> List<Tag>): T? {
        return r.toOption()
                .map { res ->
                    val tags = changeTags(tagsGetter(res))
                    debug("$TAG Changing tags - resource: $r, new tags: $tags")
                    save(tagsSetter(res, tags), tagsChanged = true)
                }
                .getOrElse { r }
    }

}