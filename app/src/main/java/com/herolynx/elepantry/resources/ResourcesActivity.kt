package com.herolynx.elepantry.resources

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.ui.recyclerview.ListAdapter
import com.herolynx.elepantry.core.ui.recyclerview.onInfiniteLoading
import com.herolynx.elepantry.ext.google.drive.GoogleDrive
import com.herolynx.elepantry.ext.google.drive.GoogleDriveSearch
import com.herolynx.elepantry.menu.LeftMenu
import com.herolynx.elepantry.resources.model.Resource
import com.herolynx.elepantry.resources.view.ResourceItemView
import com.herolynx.elepantry.resources.view.ResourceList
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class ResourcesActivity : LeftMenu() {

    private var googleDrive: GoogleDrive? = null
    private var googleSearch: GoogleDriveSearch? = null
    private var listAdapter: ListAdapter<Resource, ResourceItemView>? = null

    override val layoutWithMenuId: Int = R.layout.resources_view

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initResourceView()
        googleDrive = GoogleDrive.create(this).get()
        googleSearch = googleDrive?.search()
        loadNextResults()
    }

    private fun initResourceView() {
        val listView: RecyclerView = findViewById(R.id.resource_list) as RecyclerView
        listAdapter = ResourceList.adapter()
        listView.adapter = listAdapter
        val linearLayoutManager = LinearLayoutManager(this)
        listView.layoutManager = linearLayoutManager

        listView.onInfiniteLoading(linearLayoutManager)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { page ->
                    debug("[LazyLoading] NextPage: " + page)
                    loadNextResults()
                }
    }

    private fun loadNextResults() {
        (googleSearch?.next() ?: Observable.empty())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    result.files.map { r -> listAdapter?.add(r) }
                    listAdapter?.notifyDataSetChanged()
                    googleSearch = result
                }
    }

}
