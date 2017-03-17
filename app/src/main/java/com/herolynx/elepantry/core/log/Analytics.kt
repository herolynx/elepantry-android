package com.herolynx.elepantry.core.log

import android.app.Activity
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.herolynx.elepantry.core.generic.put

fun FirebaseAnalytics.metrics(event: String) {
    logEvent(
            event,
            Bundle()
    )
}

fun FirebaseAnalytics.metrics(event: String, value: String) {
    logEvent(
            event,
            Bundle()
                    .put(FirebaseAnalytics.Param.VALUE, value)
    )
}

fun FirebaseAnalytics.metrics(event: String, id: String = "n/a", name: String) {
    logEvent(
            event,
            Bundle()
                    .put(FirebaseAnalytics.Param.ITEM_ID, id)
                    .put(FirebaseAnalytics.Param.ITEM_NAME, name)
    )
}

fun FirebaseAnalytics.metrics(event: String, id: String = "n/a", name: String, value: String) {
    logEvent(
            event,
            Bundle()
                    .put(FirebaseAnalytics.Param.ITEM_ID, id)
                    .put(FirebaseAnalytics.Param.ITEM_NAME, name)
                    .put(FirebaseAnalytics.Param.VALUE, value)
    )
}

fun FirebaseAnalytics.viewVisit(viewName: String) = metrics(viewName)

fun FirebaseAnalytics.viewVisit(view: Activity) = viewVisit(view.javaClass.simpleName)