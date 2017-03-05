package com.herolynx.elepantry.resources.view

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
import android.widget.EditText
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.conversion.fromJsonString
import com.herolynx.elepantry.core.conversion.toJsonString
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.ui.navigation.navigateTo
import com.herolynx.elepantry.core.ui.recyclerview.ListAdapter
import com.herolynx.elepantry.resources.ResourceView
import com.herolynx.elepantry.resources.model.Tag
import com.herolynx.elepantry.resources.model.View
import com.herolynx.elepantry.resources.view.menu.UserViewsMenu
import com.herolynx.elepantry.resources.view.ui.TagItemView
import com.herolynx.elepantry.resources.view.ui.TagsList
import org.funktionale.option.toOption

class ResourceTagsActivity : UserViewsMenu() {

    override val layoutId = R.layout.resource_tags

    private var resourceName: EditText? = null
    private var newTag: EditText? = null
    private var addTag: Button? = null
    private var ctrl: ViewTagsCtrl? = null
    private var tagsAdapter: ListAdapter<Tag, TagItemView>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(getString(R.string.resource_type_view))
        initView()
        loadParams(intent!!.extras)
    }

    private fun loadParams(b: Bundle) {
        val resourceType = TYPE.valueOf(b.getString(PARAM_TYPE, ""))
        val resource = b.getString(PARAM_RESOURCE, "")
        debug("[ResourceTagActivity] Loading params - resource type: $resourceType, resource: $resource")
        when (resourceType) {
            TYPE.VIEW -> {
                ctrl = ViewTagsCtrl(this)
                ctrl?.init(resource.fromJsonString(View::class.java).get())
            }

            else -> throw UnsupportedOperationException("Unknown resource type: $resourceType")
        }
    }

    private fun initView() {
        resourceName = findViewById(R.id.resource_name) as EditText
        resourceName?.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                ctrl?.changeName(resourceName?.text.toString())
            }
        }

        newTag = findViewById(R.id.new_tag) as EditText
        addTag = findViewById(R.id.add_tag) as Button
        addTag?.setOnClickListener {
            newTag.toOption().map { t -> ctrl?.addTag(t.text.toString()) }
            newTag?.text?.clear()
        }

        val tagLists = findViewById(R.id.resource_tags_list) as RecyclerView
        tagsAdapter = TagsList.adapter(deleteHandler = { tag -> ctrl?.deleteTag(tag) })
        tagLists.adapter = tagsAdapter
        val linearLayoutManager = LinearLayoutManager(this)
        tagLists.layoutManager = linearLayoutManager
    }

    internal fun displayName(name: String) {
        resourceName?.text?.clear()
        resourceName?.text?.append(name)
    }

    internal fun displayTags(tags: List<Tag>) {
        tagsAdapter?.clear()
        tags.map { t -> tagsAdapter?.add(t) }
        tagsAdapter?.notifyDataSetChanged()
    }

    override fun onViewChange(v: View, rv: ResourceView): Boolean {
        ResourcesActivity.navigate(this, v)
        return true
    }

    companion object {

        private enum class TYPE {
            VIEW, RESOURCE
        }

        private val PARAM_RESOURCE = "resource"
        private val PARAM_TYPE = "resourceType"

        fun navigate(a: Activity, v: View) {
            debug("[Navigation] Navigation to resource tags - view: $v")
            a.navigateTo(
                    ResourceTagsActivity::class.java,
                    Pair(PARAM_TYPE, TYPE.VIEW.toString()),
                    Pair(PARAM_RESOURCE, v.toJsonString().get())
            )
        }

        fun navigateNewView(a: Activity) = navigate(a, View())

    }
}