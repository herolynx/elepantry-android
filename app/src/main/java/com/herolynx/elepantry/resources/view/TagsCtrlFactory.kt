package com.herolynx.elepantry.resources.view

import com.herolynx.elepantry.config.Config
import com.herolynx.elepantry.core.repository.Repository
import com.herolynx.elepantry.resources.model.Resource
import com.herolynx.elepantry.resources.model.View
import org.funktionale.option.Option

internal object TagsCtrlFactory {

    fun viewTagsCtrl(
            v: ResourceTagsActivity,
            r: Repository<View> = Config.repository.userViews()
    ): TagsCtrl<View> = TagsCtrl<View>(
            v,
            r,
            nameGetter = { v -> v.name },
            nameChange = Option.Some({ v, newName -> v.copy(name = newName) }),
            tagsGetter = { v -> v.tags },
            tagsSetter = { v, newTags -> v.copy(tags = newTags) }
    )

    fun resourceTagsCtrl(
            v: ResourceTagsActivity,
            r: Repository<Resource> = Config.repository.userResources()
    ): TagsCtrl<Resource> = TagsCtrl<Resource>(
            v,
            r,
            nameGetter = { r -> r.name },
            nameChange = Option.None,
            tagsGetter = { r -> r.tags },
            tagsSetter = { r, newTags -> r.copy(tags = newTags) }
    )

}