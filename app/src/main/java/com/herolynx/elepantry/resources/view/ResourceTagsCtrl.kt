package com.herolynx.elepantry.resources.view

import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.repository.Repository
import com.herolynx.elepantry.core.rx.observe
import com.herolynx.elepantry.core.rx.schedule
import com.herolynx.elepantry.resources.model.Tag
import com.herolynx.elepantry.resources.model.add
import com.herolynx.elepantry.resources.model.remove
import org.funktionale.option.Option
import org.funktionale.option.getOrElse
import org.funktionale.option.toOption

internal class ResourceTagsCtrl<T>(
        private val view: ResourceTagsActivity,
        private val repository: Repository<T>,
        private val autoReload: Boolean,
        private val idGetter: (T) -> String,
        private val nameGetter: (T) -> String,
        private val nameChange: Option<(T, String) -> T>,
        private val tagsGetter: (T) -> List<Tag>,
        private val tagsSetter: (T, List<Tag>) -> T
) {

    private val TAG = "[TagsCtrl]"

    private var t: T? = null

    fun init(data: T) {
        debug("$TAG Init - resource: $data")
        this.t = data
        refresh(data)
        if (autoReload) {
            repository
                    .asObservable()
                    .schedule()
                    .observe()
                    .filter { e -> idGetter(e.data).equals(idGetter(data)) }
                    .subscribe { changed ->
                        debug("$TAG Resource changed, refreshing - resource: $data")
                        this.t = changed.data
                        refresh(this.t)
                    }
        }
    }

    private fun refresh(r: T?) {
        r.toOption().map { res ->
            view.displayName(nameGetter(res))
            val tags = tagsGetter(res)
            view.displayTags(tags)
        }
    }

    private fun save(changed: T, tagsChanged: Boolean = false): T? {
        repository.save(changed)
        t = changed
        if (tagsChanged) {
            refresh(t)
        }
        return t
    }

    fun delete() {
        debug("$TAG Deleting - resource: $t")
        t.toOption()
                .map { res ->
                    repository.delete(res)
                            .subscribe { ResourcesActivity.navigate(view) }
                }
    }

    fun canChangeName() = nameChange.isDefined()

    fun changeName(name: String): T? {
        return nameChange
                .flatMap { logic -> t.toOption().map { res -> Pair(logic, res) } }
                .map { logicAndData ->
                    debug("$TAG Changing name - resource: $t, new name: $name")
                    save(logicAndData.first(logicAndData.second, name))
                }
                .getOrElse { t }

    }

    fun addTag(name: String): T? = changeTags(t, { tags -> tags.add(name) })

    fun deleteTag(t: Tag): T? = changeTags(this.t, { tags -> tags.remove(t) })

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