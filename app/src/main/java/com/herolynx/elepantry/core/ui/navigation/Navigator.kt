package com.herolynx.elepantry.core.ui.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent

fun <A : Activity> Context.navigateTo(target: Class<A>, vararg args: Pair<String, String>) {
    val navigateTo = Intent(this, target)
    args.map { a -> navigateTo.putExtra(a.first, a.second) }
    startActivity(navigateTo)
}