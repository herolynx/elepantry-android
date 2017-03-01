package com.herolynx.elepantry.resources.view.menu

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.LinearLayout
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.resources.ResourceView
import com.herolynx.elepantry.resources.model.View
import com.herolynx.elepantry.user.view.menu.UserBadge


abstract class UserViewsMenu : AppCompatActivity() {

    abstract val layoutWithMenuId: Int

    private var menuCtrl: UserViewsMenuCtrl? = null

    protected var loadDefaultItem: () -> Unit = {}
    protected var closeMenu: () -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutWithMenuId)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        menuCtrl = UserViewsMenuCtrl(this)
        setSupportActionBar(toolbar)
        initView(menuCtrl!!)
        initViewHandlers()
        initToolbar(toolbar)
    }

    private fun initViewHandlers() {
        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener({ view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        })
    }

    private fun initView(menuCtrl: UserViewsMenuCtrl) {
        val navigationView = findViewById(R.id.nav_view) as NavigationView
        val menuLayout = findViewById(R.id.left_menu_layout) as LinearLayout

        val userBadge = UserBadge.create(this, navigationView)
        menuLayout.addView(userBadge.layout)
        userBadge.display()

        val menuLeft = layoutInflater.inflate(R.layout.menu_user_views, navigationView, false)
        menuLayout.addView(menuLeft)

        initGoogleDriveView(menuLeft.findViewById(R.id.drive_google) as Button, menuCtrl)
        initUserViews(menuLeft.findViewById(R.id.user_views) as RecyclerView, menuCtrl)

        (menuLeft.findViewById(R.id.sign_out) as Button).setOnClickListener { menuCtrl.logOut() }
    }

    private fun initUserViews(layout: RecyclerView, menuCtrl: UserViewsMenuCtrl) {
        debug("[initUserViews] Creating...")
        val listAdapter = UserViewsList.adapter({ v ->
            onViewChange(
                    v,
                    menuCtrl.getView(v)
            )
        })
        layout.adapter = listAdapter
        val linearLayoutManager = LinearLayoutManager(this)
        layout.layoutManager = linearLayoutManager

        menuCtrl.getUserViews()
                .subscribe { v ->
                    debug("[initUserViews] Adding view: %s", v)
                    listAdapter.add(v)
                    listAdapter.notifyDataSetChanged()
                }
    }

    private fun initGoogleDriveView(b: Button, menuCtrl: UserViewsMenuCtrl) {
        debug("[initUserViews] Creating Google Drive view")
        val name = getString(R.string.google_drive)
        val v = View(name = name)
        val rv = menuCtrl.getGoogleDriveView()
        b.setOnClickListener { onViewChange(v, rv) }
        loadDefaultItem = { onViewChange(v, rv) }
    }

    protected abstract fun onViewChange(
            v: View,
            rv: ResourceView
    ): Boolean

    private fun initToolbar(toolbar: Toolbar) {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        closeMenu = { drawer.closeDrawer(GravityCompat.START) }
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