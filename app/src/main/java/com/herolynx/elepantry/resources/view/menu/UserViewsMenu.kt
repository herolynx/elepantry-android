package com.herolynx.elepantry.resources.view.menu

import android.app.ProgressDialog
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
import android.widget.LinearLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.log.metrics
import com.herolynx.elepantry.core.log.viewVisit
import com.herolynx.elepantry.core.ui.notification.WithProgressDialog
import com.herolynx.elepantry.drive.Drives
import com.herolynx.elepantry.ext.android.Permissions
import com.herolynx.elepantry.resources.core.model.View
import com.herolynx.elepantry.resources.core.service.ResourceView
import com.herolynx.elepantry.resources.view.list.DriveList
import com.herolynx.elepantry.resources.view.tags.ResourceTagsActivity
import com.herolynx.elepantry.user.view.menu.UserBadge


abstract class UserViewsMenu : AppCompatActivity(), WithProgressDialog {

    abstract val layoutId: Int
    abstract val topMenuId: Int

    private var menuCtrl: UserViewsMenuCtrl? = null

    override var mProgressDialog: ProgressDialog? = null
    protected var blockScreen = false
    protected var analytics: FirebaseAnalytics? = null
    protected var loadDefaultItem: () -> Unit = {}
    protected var closeMenu: () -> Unit = {}
    private val topMenuItems: MutableList<MenuItem> = mutableListOf()
    protected var fabEditButton: FloatingActionButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Permissions.requestWriteExternalStoragePermission(this)
        analytics = FirebaseAnalytics.getInstance(this)
        analytics?.viewVisit(this)
        setContentView(R.layout.menu_frame)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        menuCtrl = UserViewsMenuCtrl(this)
        setSupportActionBar(toolbar)
        initMenu(menuCtrl!!)
        initToolbar(toolbar)
        initView()
        initAdditionalMenuActions()
    }

    private fun initAdditionalMenuActions() {
        fabEditButton = findViewById(R.id.fab_actio_edit) as FloatingActionButton
        fabEditButton?.visibility = android.view.View.INVISIBLE
        fabEditButton?.setOnClickListener({ view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        })
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
        menuLayout.addView(userBadge.layout)
        userBadge.display()

        val menuLeft = layoutInflater.inflate(R.layout.menu_user_views, navigationView, false)
        menuLeft.findViewById(R.id.add_new_view).setOnClickListener {
            analytics?.metrics("ViewAdd")
            menuCtrl?.handleAddNewView()
            closeMenu()
        }
        menuLeft.findViewById(R.id.sign_out).setOnClickListener {
            menuCtrl.handleSignOutAction()
        }
        menuLayout.addView(menuLeft)

        initDriveViews(menuLeft.findViewById(R.id.drive_views) as RecyclerView)
        initUserViews(menuLeft.findViewById(R.id.user_views) as RecyclerView, menuCtrl)
    }

    private fun initDriveViews(layout: RecyclerView) {
        debug("[initDriveViews] Creating...")
        val listAdapter = DriveList.adapter(
                activity = this,
                onClickHandler = { driveItem ->
                    onViewChange(driveItem.asView())
                },
                refreshClickHandler = { start ->
                    showProgressBar(start)
                    if (!start) {
                        runOnUiThread {
                            closeMenu()
                            refreshView()
                        }
                    }
                }
        )
        layout.adapter = listAdapter
        val linearLayoutManager = LinearLayoutManager(this)
        layout.layoutManager = linearLayoutManager

        Drives.drives(this)
                .map { drive ->
                    listAdapter.add(drive)
                    listAdapter.sort()
                    listAdapter.notifyDataSetChanged()
                }
    }

    private fun initUserViews(layout: RecyclerView, menuCtrl: UserViewsMenuCtrl) {
        debug("[initUserViews] Creating...")
        val listAdapter = UserViewsList.adapter(
                clickHandler = { v ->
                    closeMenu()
                    analytics?.metrics("ViewChange", id = v.id, name = v.name, value = v.type.toString())
                    onViewChange(v)
                },
                editHandler = { v ->
                    closeMenu()
                    analytics?.metrics("ViewEdit", id = v.id, name = v.name)
                    ResourceTagsActivity.navigate(this, v)
                }
        )
        layout.adapter = listAdapter
        val linearLayoutManager = LinearLayoutManager(this)
        layout.layoutManager = linearLayoutManager

        menuCtrl.getUserViews()
                .subscribe { v ->
                    debug("[initUserViews] Adding view: $v")
                    listAdapter.add(v)
                    listAdapter.sort()
                    listAdapter.notifyDataSetChanged()
                }
    }

    private fun showProgressBar(start: Boolean) {
        blockScreen = start
        if (start)
            runOnUiThread { showProgressDialog(this) }
        else
            runOnUiThread { hideProgressDialog() }
    }

    protected open fun refreshView() {
        //empty
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