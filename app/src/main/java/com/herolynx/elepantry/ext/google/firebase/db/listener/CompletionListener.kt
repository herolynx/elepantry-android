package com.herolynx.elepantry.ext.google.firebase.db.listener

import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.herolynx.elepantry.core.log.error
import com.herolynx.elepantry.core.rx.DataEvent
import rx.Subscriber

/**
 * Listener checks modification operation on DB and dispatches its status among its subscribers
 */
class CompletionListener<T>(
        private val changedData: DataEvent<T>,
        entityClass: Class<T>,
        subscribers: MutableList<Subscriber<in DataEvent<T>>> = mutableListOf()
) : Listener<T>(entityClass, subscribers), DatabaseReference.CompletionListener {

    private val TAG_NAME = "[Firebase][CompletionListener]"

    override fun onComplete(error: DatabaseError?, ref: DatabaseReference?) {
        if (error != null) {
            error("$TAG_NAME Save error", error.toException())
            handleError(error)
        } else {
            dispatch(changedData)
        }
        close()
    }
}