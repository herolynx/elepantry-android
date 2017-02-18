package com.herolynx.elepantry.menu

import android.app.Activity
import android.support.design.widget.NavigationView
import android.view.Menu
import android.view.ViewGroup
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.ext.google.firebase.db.FirebaseDb
import com.herolynx.elepantry.getAppContext

interface LeftMenuItems {

    val activity: Activity?

    fun initLeftMenuHandlers() {
        val navigationView = activity?.findViewById(R.id.nav_view) as NavigationView
        initUserViews(navigationView.menu)
        activity?.getAppContext()
                ?.map { c -> UserBadge(navigationView.getHeaderView(0) as ViewGroup).display(c.user!!) }
        navigationView
                .setNavigationItemSelectedListener { item ->
                    debug("[LeftMenu] Item selected: %s", item.title)
                    true
                }
    }

    private fun initUserViews(menu: Menu) {
        debug("[initUserViews] Creating...")
        FirebaseDb.userViews.read()
                .subscribe { uv ->
                    var i = Menu.FIRST
                    menu.clear()
                    menu.add(0, i++, Menu.NONE, activity?.getString(R.string.google_drive)).setIcon(R.drawable.ic_menu_gallery)
                    val menuUserViews = menu.addSubMenu(0, i++, Menu.NONE, activity?.getString(R.string.user_views))
                    uv.views.map { v ->
                        debug("[initUserViews] Adding view: %s", v.name)
                        menuUserViews.add(i, i++, Menu.NONE, v.name).setIcon(R.drawable.ic_menu_share)
                    }
                }
    }

}