package com.herolynx.elepantry.resources

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
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.herolynx.elepantry.R
import com.herolynx.elepantry.auth.SignInActivity
import com.herolynx.elepantry.core.func.toObservable
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.navigation.navigateTo
import com.herolynx.elepantry.ext.google.drive.GoogleDrive
import com.herolynx.elepantry.getAppContext
import org.funktionale.option.toOption
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class ResourcesActivity : AppCompatActivity() {

    private fun initViewHandlers() {
        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener(View.OnClickListener { view ->
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
        getAppContext()
                .flatMap { a -> a.getMainAccount().toOption() }
                .map { acc -> GoogleDrive.create(acc, this) }
                .toObservable()
                .flatMap { gDrive -> gDrive.search() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { f ->
                    debug("[File] Name: " + f.name)
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_settings) {
            FirebaseAuth.getInstance().signOut()
//            GoogleAuth.logout(GoogleAuth.build(this, {}))
            navigateTo(SignInActivity::class.java)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

}