package com.herolynx.elepantry.resources.view.list

import android.app.Activity
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.conversion.fromJsonString
import com.herolynx.elepantry.core.conversion.toJsonString
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.log.error
import com.herolynx.elepantry.core.rx.DataEvent
import com.herolynx.elepantry.core.ui.navigation.navigateTo
import com.herolynx.elepantry.core.ui.notification.toast
import com.herolynx.elepantry.core.ui.recyclerview.ListAdapter
import com.herolynx.elepantry.core.ui.recyclerview.onInfiniteLoading
import com.herolynx.elepantry.resources.core.model.Resource
import com.herolynx.elepantry.resources.core.model.View
import com.herolynx.elepantry.resources.core.model.ViewType
import com.herolynx.elepantry.resources.core.service.ResourceView
import com.herolynx.elepantry.resources.view.content.ResourceContentActivity
import com.herolynx.elepantry.resources.view.menu.UserViewsMenu
import com.herolynx.elepantry.resources.view.tags.ResourceTagsActivity
import org.funktionale.option.getOrElse
import org.funktionale.option.toOption
import rx.Observable

class ResourcesActivity : UserViewsMenu() {

    private var listAdapter: ListAdapter<Resource, ResourceItemView>? = null
    private var loadData: (String?) -> Unit = {}
    private var ctrl: ResourcesCtrl? = null
    private var clearSearchAction: () -> Unit = {}

    override val layoutId: Int = R.layout.resources_list
    override val topMenuId = R.menu.resources_top_menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ctrl = ResourcesCtrl()
        initResourceView()
        if (intent.extras != null) {
            loadParams(intent.extras)
        }
    }

    override fun onResume() {
        super.onResume()
        debug("[onResume] Loading state - currentView: ${ctrl?.currentView}")
        topMenuItems().filter { i -> i.itemId == R.id.action_edit }.map { i -> i.setVisible(false) }
        ctrl.toOption()
                .flatMap { c -> c.currentView.toOption() }
                .map { v -> onViewChange(v) }
                .getOrElse { clearLoadStatus() }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        debug("[onRestoreInstanceState] Loading state - bundle: $savedInstanceState")
        if (savedInstanceState != null) {
            loadParams(savedInstanceState)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        debug("[onSaveInstanceState] Saving state - current view: ${ctrl?.currentView}")
        outState?.putString(PARAM_VIEW, ctrl?.currentView.toJsonString().get())
    }

    private fun loadParams(b: Bundle) {
        val view = b.getString(PARAM_VIEW, "")
        debug("[ResourceActivity] Loading params - view: $view")
        if (!view.isEmpty()) {
            view.fromJsonString(View::class.java)
                    .map { v ->
                        debug("[ResourceActivity] Loading params - displaying view: $v")
                        onViewChange(v)
                    }
        }
    }

    private fun initResourceView() {
        val listView: RecyclerView = findViewById(R.id.resource_list) as RecyclerView
        listAdapter = ResourceList.adapter(onClickHandler = { r -> ResourceContentActivity.navigate(this, r) })
        listAdapter?.onSelectedItemsChange { selected ->
            topMenuItems().filter { i -> i.itemId == R.id.action_edit }.map { i -> i.setVisible(!selected.isEmpty()) }
        }
        listView.adapter = listAdapter
        val linearLayoutManager = LinearLayoutManager(this)
        listView.layoutManager = linearLayoutManager
        loadData = { search ->
            listView.clearOnScrollListeners()
            ctrl?.loadData(
                    pageRequests = Observable.merge(
                            //initiate loading of first page
                            Observable.just(0),
                            //receive events about next needed pages to load
                            listView.onInfiniteLoading(linearLayoutManager)
                    ),
                    search = search,
                    viewDisplay = { page -> displayPage(page) }
            )
        }
        loadDefaultItem()
    }

    internal fun displayPage(pageResources: Observable<DataEvent<Resource>>) {
        pageResources.subscribe(
                { r ->
                    listAdapter?.add(r)
                    listAdapter?.notifyDataSetChanged()
                },
                { ex -> error("[PageRequest] Page result error", ex) },
                {
                    debug("[PageRequest] Page loaded")
                    listAdapter?.notifyDataSetChanged()
                }
        )
    }

    override fun onViewChange(v: View, rv: ResourceView): Boolean {
        debug("[onViewChange] View selected: %s", v)
        closeMenu()
        title = v.name
        clearSearchAction()
        ctrl?.changeView(v, rv)
        clearLoadStatus()
        loadData(null)
        return true
    }

    private fun clearLoadStatus() {
        ctrl?.clearLoadState()
        listAdapter?.clear()
        listAdapter?.notifyDataSetChanged()
    }

    private fun initSearchOption(searchView: SearchView) {
        clearSearchAction = {
            searchView.onActionViewCollapsed()
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(word: String): Boolean {
                debug("[Search] onQueryTextSubmit: $word")
                clearLoadStatus()
                loadData(word)
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(text: String): Boolean {
                debug("[Search] onQueryTextChange: $text")
                clearLoadStatus()
                loadData(text)
                return true
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = MenuItemCompat.getActionView(searchItem) as SearchView
        initSearchOption(searchView)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        debug("[TopMenu] Item selected - item: ${item.title}, action id: ${item.itemId}")
        when (item.itemId) {
            R.id.action_search -> {
                val searchView = MenuItemCompat.getActionView(item) as SearchView
                initSearchOption(searchView)
            }

            R.id.action_delete -> {
                //TODO logic here
                toast("Deleting tags of a resource not supported yet")
                return true
            }

            R.id.action_edit -> {
                when (listAdapter?.selectedItems?.size ?: 0) {
                    0 -> {
                    }
                    1 -> ResourceTagsActivity.navigate(this, listAdapter!!.selectedItems.get(0))

                    else -> {
                        //TODO logic here
                        toast("Multi-resource action not supported yet")
                    }
                }
                return true
            }

            else -> throw UnsupportedOperationException("Unknown action - item: ${item.title}, action id: ${item.itemId}")
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        private val PARAM_VIEW = "view"

        fun navigate(a: Activity, v: View = View(name = a.getString(R.string.google_drive), type = ViewType.GOOGLE)) {
            a.navigateTo(
                    ResourcesActivity::class.java,
                    Pair(PARAM_VIEW, v.toJsonString().get())
            )
        }


    }

}
