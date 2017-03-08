package com.herolynx.elepantry.resources.view

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.conversion.fromJsonString
import com.herolynx.elepantry.core.conversion.toJsonString
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.ui.navigation.navigateTo
import com.herolynx.elepantry.core.ui.recyclerview.ListAdapter
import com.herolynx.elepantry.resources.ResourceView
import com.herolynx.elepantry.resources.model.Resource
import com.herolynx.elepantry.resources.model.Tag
import com.herolynx.elepantry.resources.model.View
import com.herolynx.elepantry.resources.view.menu.UserViewsMenu
import com.herolynx.elepantry.resources.view.ui.TagItemView
import com.herolynx.elepantry.resources.view.ui.TagsList

class ResourceTagsActivity : UserViewsMenu() {

    override val layoutId = R.layout.resource_tags
    override val topMenuId = R.menu.resource_tags_top_menu

    private var resourceName: EditText? = null
    private var newTag: EditText? = null
    private var addTag: Button? = null
    private var resourceCtrl: ResourceTagsCtrl<*>? = null
    private var tagsAdapter: ListAdapter<Tag, TagItemView>? = null
    private var viewType: TYPE = TYPE.VIEW;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        loadParams(intent!!.extras)
    }

    private fun loadParams(b: Bundle) {
        val resourceType = TYPE.valueOf(b.getString(PARAM_TYPE, ""))
        val resource = b.getString(PARAM_RESOURCE, "")
        debug("[ResourceTagActivity] Loading params - resource type: $resourceType, resource: $resource")
        when (resourceType) {
            TYPE.VIEW -> {
                viewType = TYPE.VIEW;
                setTitle(getString(R.string.resource_type_view))
                val viewTagsCtrl = ResourceTagsCtrlFactory.viewTagsCtrl(this)
                viewTagsCtrl.init(resource.fromJsonString(View::class.java).get())
                resourceCtrl = viewTagsCtrl
            }

            TYPE.RESOURCE -> {
                viewType = TYPE.RESOURCE;
                setTitle(getString(R.string.resource_type_resource))
                val resourceTagsCtrl = ResourceTagsCtrlFactory.resourceTagsCtrl(this)
                resourceTagsCtrl.init(resource.fromJsonString(Resource::class.java).get())
                resourceCtrl = resourceTagsCtrl
            }

            else -> throw UnsupportedOperationException("Unknown resource type: $resourceType")
        }
        resourceName?.isEnabled = resourceCtrl?.canChangeName() ?: false
        initActions(resourceCtrl?.canChangeName() ?: false, true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val created = super.onCreateOptionsMenu(menu)
        if (viewType == TYPE.RESOURCE) {
            topMenuItems().map { i -> i.setVisible(false) }
        }
        return created
    }

    private fun changeResourceName(showConfirmation: Boolean = false) {
        val name = resourceName?.text.toString()
        if (resourceCtrl?.isNameValid(name) ?: false) {
            resourceCtrl?.changeName(name, showConfirmation = showConfirmation)
        } else {
            resourceName?.error = getString(R.string.resource_name_valid_length).format(ResourceTagsCtrl.MIN_LENGTH, ResourceTagsCtrl.MAX_LENGTH)
        }
    }

    private fun initActions(changeName: Boolean, changeTags: Boolean) {
        if (changeName) {
            resourceName?.setOnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) {
                    changeResourceName()
                }
            }
        }
        if (changeTags) {
            addTag?.setOnClickListener {
                val tagName = newTag?.text.toString()
                if (resourceCtrl?.isNameValid(tagName) ?: false) {
                    resourceCtrl?.addTag(tagName)
                    newTag?.text?.clear()
                } else {
                    newTag?.error = getString(R.string.resource_name_valid_length).format(ResourceTagsCtrl.MIN_LENGTH, ResourceTagsCtrl.MAX_LENGTH)
                }
            }
        }
    }

    private fun initView() {
        resourceName = findViewById(R.id.resource_name) as EditText

        newTag = findViewById(R.id.new_tag) as EditText
        addTag = findViewById(R.id.add_tag) as Button

        val tagLists = findViewById(R.id.resource_tags_list) as RecyclerView
        tagsAdapter = TagsList.adapter(deleteHandler = { tag -> resourceCtrl?.deleteTag(tag) })
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        debug("[TopMenu] Item selected - item: ${item.title}, action id: ${item.itemId}")
        when (item.itemId) {
            R.id.action_save -> {
                debug("[TopMenu] Saving...")
                changeResourceName(showConfirmation = true)
                return true
            }

            R.id.action_delete -> {
                debug("[TopMenu] Deleting...")
                resourceCtrl?.delete()
                return true
            }

            else -> throw UnsupportedOperationException("Unknown action - item: ${item.title}, action id: ${item.itemId}")
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        private enum class TYPE {
            VIEW, RESOURCE
        }

        private val PARAM_RESOURCE = "resource"
        private val PARAM_TYPE = "resourceType"

        fun navigate(a: Activity, r: Resource) {
            debug("[Navigation] Navigation to resource tags - resource: $r")
            a.navigateTo(
                    ResourceTagsActivity::class.java,
                    Pair(PARAM_TYPE, TYPE.RESOURCE.toString()),
                    Pair(PARAM_RESOURCE, r.toJsonString().get())
            )
        }

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