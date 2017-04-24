package com.herolynx.elepantry.core.ui.recyclerview

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import rx.Observable
import rx.Subscriber

/**
 * Create infinite on scroll listener
 *
 * @param linearLayoutManager layout manager to be able to say when next loading is needed
 * @return observeable to inform about needed next page loading
 */
fun RecyclerView.onInfiniteLoading(linearLayoutManager: LinearLayoutManager): Observable<Int> {
    val subscribers: MutableList<Subscriber<in Int>> = mutableListOf()
    addOnScrollListener(InfiniteRecyclerOnScrollListener(
            linearLayoutManager,
            { page ->
                subscribers.forEach { s -> s.onNext(page) }
            }
    ))
    return Observable.create({ p -> subscribers.add(p) })
}

/**
 * Listener informs when another portion of data should be loaded what
 * allows to provide infinite-scroll on UI lists
 *
 * @param linearLayoutManager layout manager to be able to say when next loading is needed
 * @param onLoadMore logic for loading next page
 * @param visibleThreshold the minimum number of items to have below current scroll position before loading more.
 */
class InfiniteRecyclerOnScrollListener(
        val linearLayoutManager: LinearLayoutManager,
        val onLoadMore: (Int) -> Unit,
        val visibleThreshold: Int = 30
) : RecyclerView.OnScrollListener() {

    private var previousTotal = 0
    private var loading = true
    private var currentPage = 1

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val visibleItemCount = recyclerView!!.childCount
        val totalItemCount = linearLayoutManager.itemCount
        val firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition()

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false
                previousTotal = totalItemCount
            }
        }
        if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
            currentPage++
            onLoadMore(currentPage)
            loading = true
        }
    }

}