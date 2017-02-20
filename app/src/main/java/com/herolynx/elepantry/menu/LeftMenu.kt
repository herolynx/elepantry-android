package com.herolynx.elepantry.menu

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.ext.google.firebase.db.FirebaseDb
import com.herolynx.elepantry.getAppContext

abstract class LeftMenu : AppCompatActivity() {

    abstract val layoutWithMenuId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutWithMenuId)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        initViewHandlers()
        initToolbar(toolbar)
    }

    private fun initViewHandlers() {
        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener({ view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        })
        initLeftMenuHandlers()
    }

    fun initLeftMenuHandlers() {
        val navigationView = findViewById(R.id.nav_view) as NavigationView
        initUserViews(navigationView.menu)
        getAppContext().map { c -> UserBadge(navigationView.getHeaderView(0) as ViewGroup).display(c.user!!) }
        navigationView
                .setNavigationItemSelectedListener { item ->
                    debug("[LeftMenu] Item selected: %s", item.title)
                    true
                }
    }

    private fun initUserViews(menu: Menu) {
        debug("[initUserViews] Creating...")
        var i = Menu.FIRST
        menu.clear()
        menu.add(0, i++, Menu.NONE, getString(R.string.google_drive)).setIcon(R.drawable.ic_menu_gallery)
        val menuUserViews = menu.addSubMenu(0, i++, Menu.NONE, getString(R.string.user_views))
        FirebaseDb.userViews.read()
                .subscribe { v ->
                    debug("[initUserViews] Adding view: %s", v.name)
                    menuUserViews.add(i, i++, Menu.NONE, v.name).setIcon(R.drawable.ic_menu_share)
                }
    }


    private fun initToolbar(toolbar: Toolbar) {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        debug("[TopMenu] Item selected: %s", item.title)
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

}