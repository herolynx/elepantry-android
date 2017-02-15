package com.herolynx.elepantry.ext.google.firebase.db

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.log.error
import rx.Observable
import rx.Subscriber

class FirebaseRepository<T>(
        private val rootRef: DatabaseReference,
        private val entityClass: Class<T>,
        private val subscribers: MutableList<Subscriber<in T>> = mutableListOf()
) {

    private val valueListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val t = dataSnapshot.getValue(entityClass)
            if (t != null) {
                debug("[Firebase][ValueEventListener][onDataChange] Value: %s", t)
                subscribers.map { s -> s.onNext(t) }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            error("[Firebase][ValueEventListener][onCancelled] Error", databaseError.toException())
            subscribers.map { s -> s.onError(databaseError.toException()) }
        }
    }

    init {
        rootRef.addValueEventListener(valueListener)
    }

    fun read(): Observable<T> = Observable.create({ p -> subscribers.add(p) })

    fun delete(): Observable<Boolean> {
        val ws: MutableList<Subscriber<in Boolean>> = mutableListOf()
        rootRef.removeValue({ err, ref ->
            if (err != null) {
                error("[Firebase][CompletionListener] Delete error", err.toException())
                ws.map { s -> s.onError(err.toException()) }
            } else {
                ws.map { s -> s.onNext(true) }
            }
        })
        return Observable.create({ p -> ws.add(p) })
    }

    fun save(t: T): Observable<T> {
        val ws: MutableList<Subscriber<in T>> = mutableListOf()
        rootRef.setValue(t, null, { err, ref ->
            if (err != null) {
                error("[Firebase][CompletionListener] Save error", err.toException())
                ws.map { s -> s.onError(err.toException()) }
            } else {
                ws.map { s -> s.onNext(t) }
            }
        })
        return Observable.create({ p -> ws.add(p) })
    }

}