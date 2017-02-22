package com.herolynx.elepantry.resources.view

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.log.error
import com.herolynx.elepantry.core.rx.observe
import com.herolynx.elepantry.core.rx.schedule
import com.herolynx.elepantry.core.ui.recyclerview.ListAdapter
import com.herolynx.elepantry.core.ui.recyclerview.onInfiniteLoading
import com.herolynx.elepantry.ext.google.drive.GoogleDriveView
import com.herolynx.elepantry.menu.LeftMenu
import com.herolynx.elepantry.resources.ResourcePage
import com.herolynx.elepantry.resources.ResourceView
import com.herolynx.elepantry.resources.model.Resource
import com.herolynx.elepantry.resources.view.ui.ResourceItemView
import com.herolynx.elepantry.resources.view.ui.ResourceList
import org.funktionale.tries.Try
import rx.Observable


class ResourcesActivity : LeftMenu() {

    private var resourceView: ResourceView? = null
    private var resourcePage: Try<out ResourcePage>? = null
    private var listAdapter: ListAdapter<Resource, ResourceItemView>? = null

    override val layoutWithMenuId: Int = R.layout.resources_view

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resourceView = GoogleDriveView.create(this).get()
        initResourceView()
    }

    private fun initResourceView() {
        val listView: RecyclerView = findViewById(R.id.resource_list) as RecyclerView
        listAdapter = ResourceList.adapter()
        listView.adapter = listAdapter
        val linearLayoutManager = LinearLayoutManager(this)
        listView.layoutManager = linearLayoutManager
        initDataLoading(listView, linearLayoutManager)
    }

    private fun initDataLoading(listView: RecyclerView, linearLayoutManager: LinearLayoutManager) {
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

    private fun displayPage(pageResources: Observable<Resource>) {
        pageResources.subscribe(
                { r -> listAdapter?.add(r) },
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


}
