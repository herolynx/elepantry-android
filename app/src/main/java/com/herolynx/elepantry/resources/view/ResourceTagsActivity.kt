package com.herolynx.elepantry.resources.view

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.conversion.fromJsonString
import com.herolynx.elepantry.core.conversion.toJsonString
import com.herolynx.elepantry.core.ui.navigation.navigateTo
import com.herolynx.elepantry.resources.ResourceView
import com.herolynx.elepantry.resources.model.View
import com.herolynx.elepantry.resources.view.menu.UserViewsMenu
import org.funktionale.option.toOption

class ResourceTagsActivity : UserViewsMenu() {

    override val layoutId = R.layout.resource_tags

    private var resourceName: EditText? = null
    private var newTag: EditText? = null
    private var addTag: Button? = null
    private var ctrl: ViewTagsCtrl? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(getString(R.string.resource_type_view))
        initView()
        loadParams(intent!!.extras)
    }

    private fun loadParams(b: Bundle) {
        //TODO load resource type here
//        val resourceType: TYPE = TYPE.valueOf(b.getString(PARAM_RESOURCE, "missing"))
        ctrl = ViewTagsCtrl(this)
        ctrl?.init(b.getString(PARAM_RESOURCE, "missing").fromJsonString(View::class.java).get())
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
        }

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
            a.navigateTo(
                    ResourceTagsActivity::class.java,
                    Pair(PARAM_TYPE, TYPE.VIEW.toString()),
                    Pair(PARAM_RESOURCE, v.toJsonString().get())
            )
        }

        fun navigateNewView(a: Activity) = navigate(a, View())

    }
}