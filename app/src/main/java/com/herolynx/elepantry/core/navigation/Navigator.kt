package com.herolynx.elepantry.core.navigation

import android.app.Activity
import android.content.Intent

fun <A : Activity> Activity.navigateTo(target: Class<A>) {
    val navigateTo = Intent(this, target)
    startActivity(navigateTo)
}