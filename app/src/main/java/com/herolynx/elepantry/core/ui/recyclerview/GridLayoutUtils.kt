package com.herolynx.elepantry.core.ui.recyclerview

import android.content.Context

object GridLayoutUtils {

    fun calculateNoOfColumns(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        return (dpWidth / 180).toInt()
    }

}