package com.herolynx.elepantry.core.ui.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent

fun <A : Activity> Context.navigateTo(target: Class<A>) {
    val navigateTo = Intent(this, target)
    startActivity(navigateTo)
}