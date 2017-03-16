package com.herolynx.elepantry.resources.view.menu

import android.os.Bundle
import android.support.design.widget.NavigationView
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
import com.herolynx.elepantry.resources.core.model.View
import com.herolynx.elepantry.resources.core.model.ViewType
import com.herolynx.elepantry.resources.core.service.ResourceView
import com.herolynx.elepantry.resources.view.tags.ResourceTagsActivity
import com.herolynx.elepantry.user.view.menu.UserBadge


abstract class UserViewsMenu : AppCompatActivity() {

    abstract val layoutId: Int
    abstract val topMenuId: Int

    private var menuCtrl: UserViewsMenuCtrl? = null

    protected var loadDefaultItem: () -> Unit = {}
    protected var closeMenu: () -> Unit = {}
    private val topMenuItems: MutableList<MenuItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_frame)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        menuCtrl = UserViewsMenuCtrl(this)
        setSupportActionBar(toolbar)
        initMenu(menuCtrl!!)
        initToolbar(toolbar)
        initView()
    }

    private fun initView() {
        val viewPlaceholder = findViewById(R.id.layout_placeholder) as LinearLayout
        val view = layoutInflater.inflate(layoutId, viewPlaceholder, false)
        viewPlaceholder.addView(view)
    }

    private fun initMenu(menuCtrl: UserViewsMenuCtrl) {
        val navigationView = findViewById(R.id.nav_view) as NavigationView
        val menuLayout = findViewById(R.id.left_menu_layout) as LinearLayout

        val userBadge = UserBadge.create(this, navigationView)
        userBadge.initAddNewViewAction({ closeMenu() })
        menuLayout.addView(userBadge.layout)
        userBadge.display()

        val menuLeft = layoutInflater.inflate(R.layout.menu_user_views, navigationView, false)
        menuLayout.addView(menuLeft)

        initGoogleDriveView(menuLeft.findViewById(R.id.drive_google) as Button)
        initUserViews(menuLeft.findViewById(R.id.user_views) as RecyclerView, menuCtrl)

        (menuLeft.findViewById(R.id.sign_out) as Button).setOnClickListener { menuCtrl.logOut() }
    }

    private fun initUserViews(layout: RecyclerView, menuCtrl: UserViewsMenuCtrl) {
        debug("[initUserViews] Creating...")
        val listAdapter = UserViewsList.adapter(
                clickHandler = { v ->
                    closeMenu()
                    onViewChange(v)
                },
                editHandler = { v ->
                    closeMenu()
                    ResourceTagsActivity.navigate(this, v)
                }
        )
        layout.adapter = listAdapter
        val linearLayoutManager = LinearLayoutManager(this)
        layout.layoutManager = linearLayoutManager

        menuCtrl.getUserViews()
                .subscribe { v ->
                    debug("[initUserViews] Adding view: %s", v)
                    listAdapter.add(v)
                    listAdapter.sort()
                    listAdapter.notifyDataSetChanged()
                }
    }

    private fun initGoogleDriveView(b: Button) {
        debug("[initUserViews] Creating Google Drive view")
        val name = getString(R.string.google_drive)
        val v = View(name = name, type = ViewType.GOOGLE)
        b.setOnClickListener { onViewChange(v) }
        loadDefaultItem = { onViewChange(v) }
    }

    protected abstract fun onViewChange(
            v: View,
            rv: ResourceView = menuCtrl!!.getResourceView(v)
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
        menuInflater.inflate(topMenuId, menu)
        var i = 0
        while (i < menu.size()) {
            topMenuItems.add(menu.getItem(i))
            i++
        }
        return true
    }

    protected fun topMenuItems() = topMenuItems.toList()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        debug("[TopMenu] Item selected - item: ${item.title}, action id: ${item.itemId}")
        return super.onOptionsItemSelected(item)
    }

}