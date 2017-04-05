package com.herolynx.elepantry.core.repository

import com.herolynx.elepantry.core.rx.DataEvent
import org.funktionale.option.Option
import org.funktionale.tries.Try
import rx.Observable

interface Repository<T> {

    /**
     * Find all resources
     * @return results
     */
    fun findAll(): Try<List<T>>

    /**
     * Find resource
     * @param id ID of a resource
     * @return results
     */
    fun find(id: String): Try<Option<T>>

    /**
     * Create stream and observe changes on data source
     *
     * @return new stream
     */
    fun asObservable(): Observable<DataEvent<T>>

    /**
     * Delete data
     * @param t data to be deleted
     * @param new observable
     */
    fun delete(t: T): Observable<DataEvent<T>>

    /**
     * Save data
     * @param t data to be updated
     * @param new observable
     */
    fun save(t: T): Observable<DataEvent<T>>

}