package com.herolynx.elepantry.resources

import android.os.Bundle
import android.os.SystemClock
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
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.log.info
import com.herolynx.elepantry.core.ui.recyclerview.ListAdapter
import com.herolynx.elepantry.core.ui.recyclerview.onInfiniteLoading
import com.herolynx.elepantry.ext.google.drive.GoogleDrive
import com.herolynx.elepantry.ext.google.drive.GoogleDriveSearch
import com.herolynx.elepantry.ext.google.firebase.db.FirebaseDb
import com.herolynx.elepantry.resources.model.Resource
import com.herolynx.elepantry.resources.model.UserId
import com.herolynx.elepantry.resources.model.UserViews
import com.herolynx.elepantry.resources.model.View
import com.herolynx.elepantry.resources.view.ResourceItemView
import com.herolynx.elepantry.resources.view.ResourceList
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class ResourcesActivity : AppCompatActivity() {

    private var googleDrive: GoogleDrive? = null
    private var googleSearch: GoogleDriveSearch? = null
    private var listAdapter: ListAdapter<Resource, ResourceItemView>? = null

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
        navigationView
                .setNavigationItemSelectedListener { item ->
                    val id = item.itemId

                    if (id == R.id.nav_camera) {
                        // Handle the camera action
                    } else if (id == R.id.nav_gallery) {

                    } else if (id == R.id.nav_slideshow) {

                    } else if (id == R.id.nav_manage) {

                    } else if (id == R.id.nav_share) {

                    } else if (id == R.id.nav_send) {

                    }

                    val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
                    drawer.closeDrawer(GravityCompat.START)
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
//        loadNextResults()
        FirebaseDb.userViews.read()
                .subscribe { uv -> info("[111] User views: %s", uv) }
        1.until(5).forEach { i ->
            FirebaseDb.userViews.save(
                    UserViews(userId = UserId("aa"), views = listOf(
                            View(name = "v" + i),
                            View(name = "v22")
                    ))
            )
            SystemClock.sleep(500)
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
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

}
