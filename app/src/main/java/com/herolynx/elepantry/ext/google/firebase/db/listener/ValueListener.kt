package com.herolynx.elepantry.ext.google.firebase.db.listener

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.log.error
import com.herolynx.elepantry.core.rx.DataEvent
import com.herolynx.elepantry.ext.google.firebase.db.checkError
import rx.Subscriber

/**
 * Listener gets whole value from DB and dispatches it among its subscribers
 */
class ValueListener<T>(
        entityClass: Class<T>,
        subscribers: MutableList<Subscriber<in DataEvent<T>>> = mutableListOf(),
        val loadedData: MutableList<DataEvent<T>> = mutableListOf()
) : Listener<T>(entityClass, subscribers), ValueEventListener {

    private val TAG_NAME = "[Firebase][ValueListener]"

    override fun onDataChange(dataSnapshot: DataSnapshot) {
        debug("$TAG_NAME On data change")
        loadedData.clear()
        loadedData.addAll(toList(dataSnapshot).map { d -> DataEvent(d) })
    }

    override fun onCancelled(databaseError: DatabaseError) {
        databaseError.checkError()
                .map { ex -> error("$TAG_NAME Error", ex) }
        handleError(databaseError)
        close()
    }

}