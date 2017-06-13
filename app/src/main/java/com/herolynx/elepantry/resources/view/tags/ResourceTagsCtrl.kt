package com.herolynx.elepantry.resources.view.tags

import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.log.error
import com.herolynx.elepantry.core.repository.Repository
import com.herolynx.elepantry.core.rx.observe
import com.herolynx.elepantry.core.rx.schedule
import com.herolynx.elepantry.core.ui.notification.toast
import com.herolynx.elepantry.resources.core.model.Tag
import com.herolynx.elepantry.resources.core.model.add
import com.herolynx.elepantry.resources.core.model.remove
import com.herolynx.elepantry.resources.view.list.ResourcesActivity
import org.funktionale.option.Option
import org.funktionale.option.firstOption
import org.funktionale.option.getOrElse
import org.funktionale.option.toOption

internal class ResourceTagsCtrl<T>(
        private val view: ResourceTagsActivity,
        private val repository: Repository<T>,
        private val loadFilter: Option<(T, T) -> Boolean>,
        private val nameGetter: Option<(T) -> String>,
        private val nameChange: Option<(T, String) -> T>,
        private val tagsGetter: (T) -> List<Tag>,
        private val tagsSetter: (T, List<Tag>) -> T,
        private val showProgress: (Boolean) -> Unit = {},
        private val suggestionTags: Repository<Tag>
) {

    private val TAG = "[TagsCtrl]"

    private var t: T? = null

    fun init(data: T) {
        debug("$TAG Init - resource: $data")
        this.t = data
        refresh(data)
        loadFilter.map { f ->
            repository.findAll()
                    .schedule()
                    .observe()
                    .map { l -> l.filter { e -> f(t!!, e) }.firstOption() }
                    .filter { o -> o.isDefined() }
                    .map { o -> o.get() }
                    .subscribe(
                            { changed ->
                                debug("$TAG Resource changed, refreshing - resource: $data")
                                this.t = changed
                                refresh(this.t)
                            }
                            , { ex -> error("$TAG Couldn't load resource: $data", ex) }
                    )
        }
    }

    fun isNameValid(name: String?) = name != null && name.length >= MIN_LENGTH && name.length <= MAX_LENGTH

    private fun refresh(r: T?) {
        r.toOption().map { res ->
            if (nameGetter.isDefined()) {
                view.displayName(nameGetter.get()(res))
            }
            val tags = tagsGetter(res)
            view.displayTags(tags)
            showProgress(false)
        }
    }

    private fun save(changed: T, tagsChanged: Boolean = false, showConfirmation: Boolean = false, redirect: Boolean = false): T? {
        repository.save(changed)
                .schedule()
                .observe()
                .subscribe(
                        {
                            if (showConfirmation) {
                                view.toast(R.string.confirmation_saved)
                            }
                            if (redirect) {
                                ResourcesActivity.navigate(view)
                            }
                        },
                        { ex -> error("$TAG Couldn't save changes - resource: $changed") }
                )
        t = changed
        if (tagsChanged) {
            refresh(t)
        }
        return t
    }

    fun delete(showConfirmation: Boolean = true) {
        debug("$TAG Deleting - resource: $t")
        t.toOption()
                .map { res ->
                    repository.delete(res)
                            .schedule()
                            .observe()
                            .subscribe(
                                    {
                                        if (showConfirmation) {
                                            view.toast(R.string.confirmation_deleted)
                                        }
                                        ResourcesActivity.navigate(view)
                                    },
                                    { ex -> error("$TAG Couldn't delete resource: $res") }
                            )
                }
    }

    fun canChangeName() = nameChange.isDefined()

    fun changeName(name: String, showConfirmation: Boolean = false, redirect: Boolean = false): T? {
        return nameChange
                .filter { logic -> isNameValid(name) }
                .flatMap { logic -> t.toOption().map { res -> Pair(logic, res) } }
                .map { logicAndData ->
                    debug("$TAG Changing name - resource: $t, new name: $name")
                    save(logicAndData.first(logicAndData.second, name), showConfirmation = showConfirmation, redirect = redirect)
                }
                .getOrElse { t }

    }

    fun addTag(name: String, showConfirmation: Boolean = false): T? =
            if (isNameValid(name)) {
                changeTags(t, { tags -> tags.add(name) }, showConfirmation)
            } else {
                t
            }

    fun deleteTag(t: Tag, showConfirmation: Boolean = false): T? = changeTags(this.t, { tags -> tags.remove(t) }, showConfirmation)

    fun saveTags(showConfirmation: Boolean = false, redirect: Boolean = false): T? = changeTags(this.t, { tags -> tags }, showConfirmation, redirect = redirect)

    private fun changeTags(r: T?, changeTags: (List<Tag>) -> List<Tag>, showConfirmation: Boolean = false, redirect: Boolean = false): T? {
        showProgress(true)
        return r.toOption()
                .map { res ->
                    val tags = changeTags(tagsGetter(res))
                    debug("$TAG Changing tags - resource: $r, new tags: $tags")
                    save(tagsSetter(res, tags), tagsChanged = true, showConfirmation = showConfirmation, redirect = redirect)
                }
                .getOrElse { r }
    }

    fun getTagSuggestions() = suggestionTags.findAll()
            .map { tags -> tags.map { t -> t.name }.toSet() }

    companion object {

        val MIN_LENGTH = 2
        val MAX_LENGTH = 20

    }

}