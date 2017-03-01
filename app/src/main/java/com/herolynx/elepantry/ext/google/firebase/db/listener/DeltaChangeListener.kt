package com.herolynx.elepantry.ext.google.firebase.db.listener

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.rx.DataEvent
import rx.Subscriber

/**
 * Listener checks only changes made in DB and dispatches info among its subscribers
 */
class DeltaChangeListener<T>(
        entityClass: Class<T>,
        subscribers: MutableList<Subscriber<in DataEvent<T>>> = mutableListOf()
) : Listener<T>(entityClass, subscribers), ChildEventListener {

    private val TAG_NAME = "[Firebase][DeltaChangeListener]"

    override fun onChildMoved(snapshot: DataSnapshot?, prevChildName: String?) {
        debug("$TAG_NAME On child moved - prev child name: $prevChildName")
        handleSingleChange(snapshot)
    }

    override fun onChildChanged(snapshot: DataSnapshot?, prevChildName: String?) {
        debug("$TAG_NAME On child changed - prev child name: $prevChildName ")
        handleSingleChange(snapshot)
    }

    override fun onChildAdded(snapshot: DataSnapshot?, prevChildName: String?) {
        debug("$TAG_NAME On child added - prev child name: $prevChildName")
        handleSingleChange(snapshot)
    }

    override fun onChildRemoved(snapshot: DataSnapshot?) {
        debug("$TAG_NAME On child removed")
        handleSingleChange(snapshot, deleted = true)
    }

    override fun onCancelled(error: DatabaseError?) {
        debug("$TAG_NAME On cancelled: $error")
        close()
    }
}