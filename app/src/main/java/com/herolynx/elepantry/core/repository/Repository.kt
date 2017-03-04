package com.herolynx.elepantry.core.repository

import com.herolynx.elepantry.core.rx.DataEvent
import rx.Observable

interface Repository<T> {

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