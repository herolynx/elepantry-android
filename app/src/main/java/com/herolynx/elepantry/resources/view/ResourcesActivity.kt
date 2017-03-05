package com.herolynx.elepantry.resources.view

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.conversion.fromJsonString
import com.herolynx.elepantry.core.conversion.toJsonString
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.log.error
import com.herolynx.elepantry.core.rx.DataEvent
import com.herolynx.elepantry.core.rx.observe
import com.herolynx.elepantry.core.rx.schedule
import com.herolynx.elepantry.core.ui.navigation.navigateTo
import com.herolynx.elepantry.core.ui.recyclerview.ListAdapter
import com.herolynx.elepantry.core.ui.recyclerview.onInfiniteLoading
import com.herolynx.elepantry.resources.ResourcePage
import com.herolynx.elepantry.resources.ResourceView
import com.herolynx.elepantry.resources.model.Resource
import com.herolynx.elepantry.resources.model.View
import com.herolynx.elepantry.resources.model.ViewType
import com.herolynx.elepantry.resources.view.menu.UserViewsMenu
import com.herolynx.elepantry.resources.view.ui.ResourceItemView
import com.herolynx.elepantry.resources.view.ui.ResourceList
import org.funktionale.tries.Try
import rx.Observable

class ResourcesActivity : UserViewsMenu() {

    private var resourceView: ResourceView? = null
    private var resourcePage: Try<out ResourcePage>? = null
    private var listAdapter: ListAdapter<Resource, ResourceItemView>? = null
    private var loadData: () -> Unit = {}

    override val layoutId: Int = R.layout.resources_list
    override val topMenuId = R.menu.resources_top_menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initResourceView()
        if (intent.extras != null) {
            loadParams(intent.extras)
        }
    }

    private fun loadParams(b: Bundle) {
        val view = b.getString(PARAM_VIEW, "")
        debug("[ResourceActivity] Loading params - view: $view")
        if (!view.isEmpty()) {
            view.fromJsonString(View::class.java)
                    .map { v -> onViewChange(v) }
        }
    }

    private fun initResourceView() {
        val listView: RecyclerView = findViewById(R.id.resource_list) as RecyclerView
        listAdapter = ResourceList.adapter()
        listAdapter?.onSelectedItemsChange { selected ->
            topMenuItems().map { i -> i.setVisible(!selected.isEmpty()) }
        }
        listView.adapter = listAdapter
        val linearLayoutManager = LinearLayoutManager(this)
        listView.layoutManager = linearLayoutManager
        loadData = { initDataLoading(listView, linearLayoutManager) }
        loadDefaultItem()
    }

    private fun initDataLoading(listView: RecyclerView, linearLayoutManager: LinearLayoutManager) {
        listView.clearOnScrollListeners()
        Observable.merge(
                //initiate loading of first page
                Observable.just(0),
                //receive events about next needed pages to load
                listView.onInfiniteLoading(linearLayoutManager)
        )
                .flatMap { pageNr ->
                    debug("[PageRequest] Loading next page - number: %s", pageNr)
                    loadNextPage()
                }
                .filter { p -> p.isSuccess() }
                .map { p -> p.get().resources() }
                .schedule()
                .observe()
                .subscribe(
                        { pageResources -> displayPage(pageResources) },
                        { ex -> error("[PageRequest] Loading error", ex) }
                )
    }

    private fun displayPage(pageResources: Observable<DataEvent<Resource>>) {
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

    private fun loadNextPage() = Observable.defer {
        resourcePage = if (resourcePage == null) resourceView?.search()
        else if (resourcePage!!.isFailure()) resourcePage
        else resourcePage!!.get().next()
        Observable.just(resourcePage!!)
    }
            .schedule()

    override fun onViewChange(v: View, rv: ResourceView): Boolean {
        debug("[onViewChange] View selected: %s", v)
        closeMenu()
        title = v.name
        resourceView = rv
        resourcePage = null
        listAdapter?.clear()
        listAdapter?.notifyDataSetChanged()
        loadData()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        debug("[TopMenu] Item selected - item: ${item.title}, action id: ${item.itemId}")
        when (item.itemId) {
            R.id.action_delete -> {

                return true
            }

            R.id.action_edit -> {

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
