package com.example.steamflexer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.main_activity_content.*

var login_id = ""

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        login_id = intent.getStringExtra("login") ?: ""
        id_input.setText(login_id)

        button.setOnClickListener() {
            var steamID64 = id_input.text.toString()
            if(steamID64.length != 17) {
                Toast.makeText(this,"Please enter the correct steamid64 format",Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this,SteamProfile::class.java)
                intent.putExtra("steamID64", steamID64)
                startActivity(intent)
            }
        }

        steam_login_pic.setOnClickListener() {
            val intent = Intent(this,LoginActivity::class.java)
            finish()
            startActivity(intent)
        }


    }
    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.temp, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_profile -> {
                var steamID64 = id_input.text.toString()
                if(steamID64.length != 17) {
                    Toast.makeText(this,"Please enter the correct steamid64 or login first",Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(this,SteamProfile::class.java)
                    intent.putExtra("steamID64", steamID64)
                    startActivity(intent)
                }

            }
            R.id.nav_ladder -> {
                val intent = Intent(this, LadderActivity::class.java)
                intent.putExtra("country", "Worldwide")
                intent.putExtra("type", "Highest Level")
                startActivity(intent)
            }
            R.id.nav_share -> {


            }
            R.id.nav_send -> {

            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}

