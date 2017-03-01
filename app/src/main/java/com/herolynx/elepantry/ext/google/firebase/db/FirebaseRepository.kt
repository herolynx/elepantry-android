package com.herolynx.elepantry.ext.google.firebase.db

import com.google.firebase.database.DatabaseReference
import com.herolynx.elepantry.core.rx.DataEvent
import com.herolynx.elepantry.ext.google.firebase.db.listener.CompletionListener
import com.herolynx.elepantry.ext.google.firebase.db.listener.DeltaChangeListener
import com.herolynx.elepantry.ext.google.firebase.db.listener.ValueListener
import rx.Observable

class FirebaseRepository<T>(
        private val rootRef: DatabaseReference,
        private val entityClass: Class<T>,
        private val idGetter: (T) -> String
) {

    private val valueListener = ValueListener<T>(entityClass)
    private val deltaListener = DeltaChangeListener<T>(entityClass)

    init {
        rootRef.addListenerForSingleValueEvent(valueListener)
        rootRef.addChildEventListener(deltaListener)
    }

    /**
     * Create stream and observe changes on data source
     *
     * @return new stream
     */
    fun observe(): Observable<DataEvent<T>> = Observable.merge(
            Observable.from(valueListener.loadedData),
            Observable.create({ p -> deltaListener.subsribe(p) })
    )

    /**
     * Delete data
     * @param t data to be deleted
     * @param new observable
     */
    fun delete(t: T): Observable<DataEvent<T>> = modify(DataEvent(data = t, deleted = true))

    /**
     * Save data
     * @param t data to be updated
     * @param new observable
     */
    fun save(t: T): Observable<DataEvent<T>> = modify(DataEvent(t))

    /**
     * Modify data in DB
     * @param t data change event
     * @param new observable
     */
    private fun modify(t: DataEvent<T>): Observable<DataEvent<T>> {
        val l = CompletionListener<T>(t, entityClass)
        val id = idGetter(t.data)
        if (t.deleted) {
            rootRef.child(id).removeValue(l)
        } else {
            rootRef.child(id).setValue(t, l)
        }
        return Observable.create({ s -> l.subsribe(s) })
    }

}