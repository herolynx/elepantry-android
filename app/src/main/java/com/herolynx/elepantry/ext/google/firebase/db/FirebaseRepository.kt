package com.herolynx.elepantry.ext.google.firebase.db

import com.google.firebase.database.DatabaseReference
import com.herolynx.elepantry.core.repository.Repository
import com.herolynx.elepantry.core.rx.DataEvent
import com.herolynx.elepantry.ext.google.firebase.db.listener.CompletionListener
import com.herolynx.elepantry.ext.google.firebase.db.listener.DeltaChangeListener
import com.herolynx.elepantry.ext.google.firebase.db.listener.ValueListener
import org.funktionale.option.toOption
import org.funktionale.tries.Try
import rx.Observable

class FirebaseRepository<T>(
        private val rootRef: DatabaseReference,
        private val entityClass: Class<T>,
        private val idGetter: (T) -> String
) : Repository<T> {

    private val valueListener = ValueListener<T>(entityClass)
    private val deltaListener = DeltaChangeListener<T>(entityClass)

    init {
        rootRef.addListenerForSingleValueEvent(valueListener)
        rootRef.addChildEventListener(deltaListener)
    }

    override fun find(id: String) = Try {
        valueListener.loadedData
                .filter { e -> !e.deleted }
                .map { e -> e.data }
                .find { r -> idGetter(r).equals(id) }
                .toOption()
    }

    /**
     * Create stream and observe changes on data source
     *
     * @return new stream
     */
    override fun asObservable(): Observable<DataEvent<T>> = Observable.merge(
            Observable.from(valueListener.loadedData),
            Observable.create({ p -> deltaListener.subsribe(p) })
    )

    /**
     * Delete data
     * @param t data to be deleted
     * @param new observable
     */
    override fun delete(t: T): Observable<DataEvent<T>> = modify(DataEvent(data = t, deleted = true))

    /**
     * Save data
     * @param t data to be updated
     * @param new observable
     */
    override fun save(t: T): Observable<DataEvent<T>> = modify(DataEvent(t))

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
            rootRef.child(id).setValue(t.data, l)
        }
        return Observable.create({ s -> l.subsribe(s) })
    }

}