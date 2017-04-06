package com.herolynx.elepantry.ext.google.firebase.db

import android.os.SystemClock
import com.google.firebase.database.DatabaseReference
import com.herolynx.elepantry.core.repository.Repository
import com.herolynx.elepantry.core.rx.DataEvent
import com.herolynx.elepantry.ext.google.firebase.db.listener.CompletionListener
import com.herolynx.elepantry.ext.google.firebase.db.listener.DeltaChangeListener
import com.herolynx.elepantry.ext.google.firebase.db.listener.ValueListener
import org.funktionale.option.firstOption
import org.joda.time.Duration
import rx.Observable

class FirebaseRepository<T>(
        private val rootRef: DatabaseReference,
        private val entityClass: Class<T>,
        private val idGetter: (T) -> String,
        private val waitTime: Duration = Duration.millis(50),
        private val maxWaitTime: Duration = Duration.standardSeconds(30)
) : Repository<T> {

    private val valueListener = ValueListener<T>(entityClass)
    private val deltaListener = DeltaChangeListener<T>(entityClass)

    init {
        rootRef.addValueEventListener(valueListener)
        rootRef.addChildEventListener(deltaListener)
    }

    override fun findAll() = Observable.defer {
        var resources: List<DataEvent<T>> = valueListener.loadedData
        var time = Duration.ZERO
        while (resources.isEmpty() && time.isShorterThan(maxWaitTime)) {
            resources = valueListener.loadedData
            time = time.plus(waitTime)
            SystemClock.sleep(waitTime.millis)
        }
        Observable.just(resources.filter { e -> !e.deleted }.map { e -> e.data })
    }

    override fun find(id: String) = findAll()
            .map { l ->
                l.filter { t -> idGetter(t).equals(id) }.firstOption()
            }

    override fun asObservable(): Observable<DataEvent<T>> = Observable.merge(
            Observable.from(valueListener.loadedData),
            Observable.create({ p -> deltaListener.subsribe(p) })
    )

    override fun delete(t: T): Observable<DataEvent<T>> = modify(DataEvent(data = t, deleted = true))

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