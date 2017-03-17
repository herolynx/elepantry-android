package com.herolynx.elepantry.resources.view.list

import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.log.error
import com.herolynx.elepantry.core.rx.DataEvent
import com.herolynx.elepantry.core.rx.observe
import com.herolynx.elepantry.core.rx.schedule
import com.herolynx.elepantry.resources.core.model.Resource
import com.herolynx.elepantry.resources.core.model.View
import com.herolynx.elepantry.resources.core.service.ResourcePage
import com.herolynx.elepantry.resources.core.service.ResourceView
import com.herolynx.elepantry.resources.core.service.SearchCriteria
import org.funktionale.tries.Try
import rx.Observable
import rx.Subscription

internal class ResourcesCtrl {

    internal var currentView: View? = null
    private var resourceView: ResourceView? = null
    private var resourcePage: Try<out ResourcePage>? = null
    private var loadDataSubs: Subscription? = null

    fun changeView(v: View, rv: ResourceView) {
        currentView = v
        resourceView = rv
    }

    fun clearLoadState() {
        resourcePage = null
        loadDataSubs?.unsubscribe()
        loadDataSubs = null
    }

    fun loadData(
            pageRequests: Observable<Int>,
            search: String? = null,
            viewDisplay: (pageResources: Observable<DataEvent<Resource>>) -> Unit
    ) {
        loadDataSubs = pageRequests
                .flatMap { pageNr ->
                    debug("[PageRequest] Loading next page - number: %s", pageNr)
                    loadNextPage(search)
                }
                .filter { p -> p.isSuccess() }
                .map { p -> p.get().resources() }
                .schedule()
                .observe()
                .subscribe(
                        { pageResources -> viewDisplay(pageResources) },
                        { ex -> error("[PageRequest] Loading error", ex) }
                )
    }

    private fun loadNextPage(search: String? = null) = Observable.defer {
        resourcePage = if (resourcePage == null) resourceView?.search(SearchCriteria(text = search))
        else if (resourcePage!!.isFailure()) resourcePage
        else resourcePage!!.get().next()
        Observable.just(resourcePage!!)
    }
            .schedule()

}