package com.herolynx.elepantry.resources.view.menu

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
import android.widget.Button
import android.widget.LinearLayout
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.ext.google.auth.SignInUseCase
import com.herolynx.elepantry.ext.google.drive.GoogleDriveView
import com.herolynx.elepantry.ext.google.firebase.db.FirebaseDb
import com.herolynx.elepantry.resources.ResourceView
import com.herolynx.elepantry.resources.dynamic.DynamicResourceView
import com.herolynx.elepantry.resources.model.View
import com.herolynx.elepantry.user.menu.UserBadge


abstract class UserViewsMenu : AppCompatActivity() {

    abstract val layoutWithMenuId: Int

    protected var loadDefaultItem: () -> Unit = {}
    protected var closeMenu: () -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutWithMenuId)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        initView()
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

    fun initView() {
        val navigationView = findViewById(R.id.nav_view) as NavigationView
        val menuLayout = findViewById(R.id.left_menu_layout) as LinearLayout

        val userBadge = UserBadge.create(this, navigationView)
        menuLayout.addView(userBadge.layout)
        userBadge.display(this)

        val menuLeft = layoutInflater.inflate(R.layout.menu_user_views, navigationView, false)
        menuLayout.addView(menuLeft)
        initGoogleDriveView(menuLeft.findViewById(R.id.drive_google) as Button)
        initUserViews(menuLeft.findViewById(R.id.user_views) as LinearLayout)
        (menuLeft.findViewById(R.id.sign_out) as Button).setOnClickListener {
            SignInUseCase.logOut(this)
        }
    }

    private fun initUserViews(layout: LinearLayout) {
        debug("[initUserViews] Creating...")
        layout.removeAllViews()

        FirebaseDb.userViews().read()
                .subscribe { v ->
                    debug("[initUserViews] Adding view: %s", v.name)
                    val userViewLayout = layoutInflater.inflate(R.layout.menu_user_views_item, layout, false)
                    layout.addView(userViewLayout)
                    val b = userViewLayout.findViewById(R.id.user_view_button) as Button
                    b.text = v.name
                    b.setOnClickListener {
                        onViewChange(
                                v,
                                DynamicResourceView(v, { FirebaseDb.userResources().read() })
                        )
                    }
                }
    }

    private fun initGoogleDriveView(b: Button) {
        debug("[initUserViews] Creating Google Drive view")
        val name = getString(R.string.google_drive)
        val v = View(name = name)
        val rv = GoogleDriveView.create(this).get()
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