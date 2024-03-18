package com.example.cloudy

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.cloudy.databinding.ActivityHomeBinding
import com.example.cloudy.favorite.FavoriteFragment
import com.example.cloudy.home.view.HomeFragment
import com.google.android.material.navigation.NavigationView


private const val TAG = "HomeActivity"
class HomeActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {

    lateinit  var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigator_layout)

        val actionBar = supportActionBar
        actionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu)
        actionBar!!.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)

        navigationView.setNavigationItemSelectedListener(this)
        val toogle=ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()
        if(savedInstanceState==null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment,HomeFragment())
                .commit()
            navigationView.setCheckedItem(R.id.homeFragment)

        }

    }
   override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.homeFragment -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, HomeFragment())
                    .commit()
                Log.i(TAG, "onNavigationItemSelected: home")
            }
            R.id.favoriteFragment -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, FavoriteFragment())
                    .commit()
                Log.i(TAG, "onNavigationItemSelected: fav")
            }
            R.id.alertFragment -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, AlertFragment())
                    .commit()
                Log.i(TAG, "onNavigationItemSelected: alert")
            }
            else -> {
                Log.i(TAG, "onNavigationItemSelected: false")
                return false
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)

        return true
    }

}
