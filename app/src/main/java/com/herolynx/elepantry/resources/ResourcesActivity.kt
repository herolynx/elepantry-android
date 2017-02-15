package com.herolynx.elepantry.resources

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
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.ui.recyclerview.ListAdapter
import com.herolynx.elepantry.core.ui.recyclerview.onInfiniteLoading
import com.herolynx.elepantry.core.view.download
import com.herolynx.elepantry.ext.google.drive.GoogleDrive
import com.herolynx.elepantry.ext.google.drive.GoogleDriveSearch
import com.herolynx.elepantry.ext.google.firebase.db.FirebaseDb
import com.herolynx.elepantry.getAppContext
import com.herolynx.elepantry.resources.model.Resource
import com.herolynx.elepantry.resources.view.ResourceItemView
import com.herolynx.elepantry.resources.view.ResourceList
import org.funktionale.option.toOption
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class ResourcesActivity : AppCompatActivity() {

    private var googleDrive: GoogleDrive? = null
    private var googleSearch: GoogleDriveSearch? = null
    private var listAdapter: ListAdapter<Resource, ResourceItemView>? = null

    private fun initUserBadge(v: ViewGroup) {
        getAppContext().map { c -> c.googleAccount!! }
                .map { user ->
                    debug("[UserBadge] Displaying user info: %s", user)
                    val userName = v.findViewById(R.id.menu_user_name) as TextView
                    userName.text = user.displayName
                    user.photoUrl.toOption()
                            .map { url ->
                                url
                                        .download()
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe { bitmap ->
                                            val userImage = v.findViewById(R.id.menu_user_picture) as ImageView
                                            userImage.setImageBitmap(bitmap)
                                        }
                            }
                }
    }

    private fun initViewHandlers() {
        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener({ view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        })
        initLeftMenuHandlers()
    }

    private fun initLeftMenuHandlers() {
        val navigationView = findViewById(R.id.nav_view) as NavigationView
        initUserViews(navigationView.menu)
        initUserBadge(navigationView.getHeaderView(0) as ViewGroup)
        navigationView
                .setNavigationItemSelectedListener { item ->
                    debug("[LeftMenu] Item selected: %s", item.title)
                    true
                }
    }

    private fun initToolbar(toolbar: Toolbar) {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.resources_view)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        initViewHandlers()
        initToolbar(toolbar)
        initResourceView()
        googleDrive = GoogleDrive.create(this).get()
        googleSearch = googleDrive?.search()
        loadNextResults()
    }

    private fun initUserViews(menu: Menu) {
        debug("[initUserViews] Creating...")
        FirebaseDb.userViews.read()
                .subscribe { uv ->
                    var i = Menu.FIRST
                    menu.clear()
                    menu.add(0, i++, Menu.NONE, getString(R.string.google_drive)).setIcon(R.drawable.ic_menu_gallery)
                    val menuUserViews = menu.addSubMenu(0, i++, Menu.NONE, getString(R.string.user_views))
                    uv.views.map { v ->
                        debug("[initUserViews] Adding view: %s", v.name)
                        menuUserViews.add(i, i++, Menu.NONE, v.name).setIcon(R.drawable.ic_menu_share)
                    }
                }
    }

    private fun initResourceView() {
        val listView: RecyclerView = findViewById(R.id.resource_list) as RecyclerView
        listAdapter = ResourceList.adapter()
        listView.adapter = listAdapter
        val linearLayoutManager = LinearLayoutManager(this)
        listView.layoutManager = linearLayoutManager

        listView.onInfiniteLoading(linearLayoutManager)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { page ->
                    debug("[LazyLoading] NextPage: " + page)
                    loadNextResults()
                }
    }

    private fun loadNextResults() {
        (googleSearch?.next() ?: Observable.empty())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    result.files.map { r -> listAdapter?.add(r) }
                    listAdapter?.notifyDataSetChanged()
                    googleSearch = result
                }
    }

    override fun onBackPressed() {
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
