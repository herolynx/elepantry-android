package com.herolynx.elepantry.resources.view.ui

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.herolynx.elepantry.R
import com.herolynx.elepantry.config.Config
import com.herolynx.elepantry.core.repository.Repository
import com.herolynx.elepantry.core.ui.recyclerview.ListAdapter
import com.herolynx.elepantry.resources.model.Resource
import org.funktionale.option.Option
import org.funktionale.option.toOption

object ResourceList {

    fun adapter(userResourceRepository: Repository<Resource> = Config.repository.userResources()):
            ListAdapter<Resource, ResourceItemView> =
            ListAdapter(
                    { ctx -> ResourceItemView(ctx) },
                    { r, h -> display(r, h, userResourceRepository) }
            )

    fun display(r: Resource?, h: ListAdapter.ViewHolder<ResourceItemView>, userResourceRepository: Repository<Resource>) {
        h.view.name.text = r?.name
        h.view.ext.text = r?.extension
        r.toOption()
                .map(Resource::id)
                .flatMap { id -> userResourceRepository.find(id).getOrElse { Option.None } }
                .map { userResource ->
                    h.view.ext.text = userResource.extension
                    h.view.tags.text = userResource.tags.map { t -> "#${t.name}" }.reduce { t, s -> "$t, $s" }
                }
    }

}

class ResourceItemView(ctx: Context) : LinearLayout(ctx) {

    init {
        LayoutInflater.from(context).inflate(R.layout.resources_list_item, this)
    }

    val name = findViewById(R.id.resource_item_name) as TextView
    val tags = findViewById(R.id.resource_item_tags) as TextView
    val ext = findViewById(R.id.resource_item_ext) as TextView

}

