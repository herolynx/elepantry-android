package com.herolynx.elepantry.resources.view.list

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.herolynx.elepantry.R
import com.herolynx.elepantry.config.Config
import com.herolynx.elepantry.core.log.error
import com.herolynx.elepantry.core.rx.observeOnDefault
import com.herolynx.elepantry.core.rx.subscribeOnDefault
import com.herolynx.elepantry.core.ui.image.download
import com.herolynx.elepantry.core.ui.recyclerview.ListAdapter
import com.herolynx.elepantry.drive.CloudDrive
import com.herolynx.elepantry.drive.CloudResource
import com.herolynx.elepantry.drive.DriveType
import com.herolynx.elepantry.repository.Repository
import com.herolynx.elepantry.resources.core.model.Resource
import com.herolynx.elepantry.resources.core.model.getTagValue
import org.funktionale.option.Option
import org.funktionale.option.toOption
import org.funktionale.tries.Try
import rx.Observable
import rx.Subscription

internal object ResourceList {

    fun adapter(
            driveFactory: (DriveType) -> CloudDrive,
            userResourceRepository: Repository<Resource> = Config.repository.userResources(),
            onClickHandler: (Resource) -> Unit,
            layoutId: Int = R.layout.resources_list_item
    ):
            ListAdapter<Resource, ResourceItemView> =
            ListAdapter(
                    { ctx -> ResourceItemView(ctx, layoutId) },
                    { r, h -> display(r, driveFactory(r!!.type).cloudResource(r!!), h, userResourceRepository, onClickHandler) }
            )

    private fun display(
            r: Resource?,
            cloudResource: Try<CloudResource>,
            h: ListAdapter.ViewHolder<ResourceItemView>,
            userResourceRepository: Repository<Resource>,
            onClickHandler: (Resource) -> Unit
    ) {
        h.view.lastSubscription.filter { s -> !s.isUnsubscribed }.map { s -> s.unsubscribe() }
        h.view.open.setOnClickListener { _ ->
            if (r != null) {
                onClickHandler(r)
            }
        }
        h.view.name.text = r?.name
        h.view.ext.text = r?.extension
        h.view.parentId = r.toOption().map(Resource::id)
        displayTags(r, h, userResourceRepository)
        if (r?.hasThumbnails() ?: false) {
            h.view.lastSubscription = Option.Some(h.view.thumbnail.download(
                    cloudResource.map { cr -> cr.thumbnail() }.getOrElse { Observable.empty() },
                    h.view.parentId,
                    { h.view.parentId }
            ))
        } else {
            h.view.lastSubscription = Option.None
            h.view.thumbnail.setImageBitmap(null)
        }
    }

    private fun displayTags(r: Resource?, h: ListAdapter.ViewHolder<ResourceItemView>, userResourceRepository: Repository<Resource>) {
        h.view.tags.text = ""
        h.view.ext.text = ""
        r.toOption()
                .map(Resource::id)
                .map { id ->
                    userResourceRepository.find(id)
                            .filter { ur -> ur.isDefined() }
                            .map { ur -> ur.get() }
                            .subscribeOnDefault()
                            .observeOnDefault()
                            .subscribe(
                                    { userResource ->
                                        h.view.ext.text = userResource.extension
                                        h.view.tags.text = userResource.getTagValue()
                                    },
                                    { ex -> error("[ResourceList] Couldn't display tags of resource: $r", ex) }
                            )
                }
    }

}

internal class ResourceItemView(ctx: Context, layoutId: Int = R.layout.resources_list_item) : LinearLayout(ctx) {

    init {
        LayoutInflater.from(context).inflate(layoutId, this)
    }

    val name = findViewById(R.id.resource_item_name) as TextView
    val tags = findViewById(R.id.resource_item_tags) as TextView
    val thumbnail = findViewById(R.id.resource_thumbnail) as ImageView
    val ext = findViewById(R.id.resource_item_ext) as TextView
    var parentId: Option<String> = Option.None
    var lastSubscription: Option<Subscription> = Option.None
    val open = findViewById(R.id.resource_open) as LinearLayout

}

