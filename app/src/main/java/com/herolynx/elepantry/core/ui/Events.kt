package com.herolynx.elepantry.core.ui

import android.view.KeyEvent
import android.view.View
import com.jakewharton.rxbinding.view.keys

fun View.onKeyEnter() = this.keys(rx.functions.Func1 { event -> event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER })
        .filter { event -> event.action == KeyEvent.ACTION_DOWN }
        .filter { event -> event.keyCode == KeyEvent.KEYCODE_ENTER }
