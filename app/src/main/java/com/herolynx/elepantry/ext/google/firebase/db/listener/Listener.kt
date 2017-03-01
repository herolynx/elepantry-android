package com.herolynx.elepantry.ext.google.firebase.db.listener

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.herolynx.elepantry.core.log.error
import com.herolynx.elepantry.core.rx.DataEvent
import org.funktionale.option.getOrElse
import org.funktionale.option.toOption
import rx.Subscriber

/**
 * Basic DB listener that can dispatch info among its subscribers
 */
abstract class Listener<T>(
        protected val entityClass: Class<T>,
        private val subscribers: MutableList<Subscriber<in DataEvent<T>>> = mutableListOf()
) {

    /**
     * Add new subscriber
     * @param s subscriber
     */
    fun subsribe(s: Subscriber<in DataEvent<T>>) {
        subscribers.add(s)
    }

    /**
     * Close listener and its subscribers
     */
    protected fun close() {
        subscribers.map { s -> s.onCompleted() }
        subscribers.clear()
    }

    /**
     * Handle data change
     * @param elementSnapshot DB snapshot of modified data (root)
     * @param deleted flag indicates whether data has been removed
     */
    protected fun handleSingleChange(elementSnapshot: DataSnapshot?, deleted: Boolean = false) {
        val t = elementSnapshot?.getValue(entityClass)
        t.toOption().map { t -> dispatch(DataEvent(t, deleted = deleted)) }
    }

    /**
     * Handle data error
     * @param error DB error
     */
    protected fun handleError(error: DatabaseError?) {
        try {
            subscribers.map { s -> s.onError(error?.toException()) }
        } catch(e: Exception) {
            error("Error while sending exception to subscribers", e)
        }
    }

    /**
     * Dispatch info among subscribers
     * @param d event to be dispatched
     */
    protected fun dispatch(d: DataEvent<T>) {
        subscribers.map { s -> s.onNext(d) }
    }

    /**
     * Convert DB snapshot to list
     * @param snapshot DB snapshot
     * @return new list
     */
    protected fun toList(snapshot: DataSnapshot?): List<T> {
        return snapshot?.children
                ?.map { c -> c.getValue(entityClass) }
                ?.toList()
                .toOption()
                .getOrElse { listOf() }
    }

}