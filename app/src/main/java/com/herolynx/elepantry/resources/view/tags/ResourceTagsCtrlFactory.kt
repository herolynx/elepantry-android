package com.herolynx.elepantry.resources.view.tags

import com.herolynx.elepantry.config.Config
import com.herolynx.elepantry.core.repository.Repository
import com.herolynx.elepantry.resources.core.model.Resource
import com.herolynx.elepantry.resources.core.model.View
import org.funktionale.option.Option

internal object ResourceTagsCtrlFactory {

    fun viewTagsCtrl(
            v: ResourceTagsActivity,
            r: Repository<View> = Config.repository.userViews()
    ): ResourceTagsCtrl<View> = ResourceTagsCtrl<View>(
            view = v,
            repository = r,
            autoReload = true,
            idGetter = View::id,
            nameGetter = View::name,
            nameChange = Option.Some({ v, newName -> v.copy(name = newName) }),
            tagsGetter = View::tags,
            tagsSetter = { v, newTags -> v.copy(tags = newTags) }
    )

    fun resourceTagsCtrl(
            v: ResourceTagsActivity,
            r: Repository<Resource> = Config.repository.userResources()
    ): ResourceTagsCtrl<Resource> = ResourceTagsCtrl<Resource>(
            view = v,
            repository = r,
            autoReload = true,
            idGetter = Resource::id,
            nameGetter = Resource::name,
            nameChange = Option.None,
            tagsGetter = Resource::tags,
            tagsSetter = { r, newTags -> r.copy(tags = newTags) }
    )

}