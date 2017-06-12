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
            loadFilter = Option.Some({ v1, v2 -> v1.id.equals(v2.id) }),
            nameGetter = Option.Some(View::name),
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
            loadFilter = Option.Some({ v1, v2 -> v1.id.equals(v2.id) }),
            nameGetter = Option.Some(Resource::name),
            nameChange = Option.None,
            tagsGetter = Resource::tags,
            tagsSetter = { r, newTags -> r.copy(tags = newTags) }
    )

    fun groupTagsCtrl(
            v: ResourceTagsActivity,
            r: Repository<Resource> = Config.repository.userResources(),
            group: List<Resource>
    ): ResourceTagsCtrl<GroupResourcesTags> {
        val groupRepo = GroupResourcesTagsRepository(r, group)
        return ResourceTagsCtrl<GroupResourcesTags>(
                view = v,
                repository = groupRepo,
                loadFilter = Option.Some({ v1, v2 -> true }),
                nameGetter = Option.None,
                nameChange = Option.None,
                tagsGetter = GroupResourcesTags::getTags,
                tagsSetter = { g, newTags -> g.addTags(newTags) },
                showProgress = { show ->
                    if (show) v.runOnUiThread { v.showProgressDialog(v) }
                    else v.runOnUiThread { v.hideProgressDialog() }
                },
                suggestionResources = { groupRepo.all() }
        )
    }


}