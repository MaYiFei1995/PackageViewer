package com.mai.packageviewer.activity

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.mai.packageviewer.R
import com.mai.packageviewer.view.MainMenu


class MainActivity : AppCompatActivity() {
    private val tag = MainActivity::class.simpleName
    private lateinit var mainMenu: MainMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_app_info)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_main, menu)
        mainMenu = MainMenu(menu!!, this)
        mainMenu.mainMenuListener = object : MainMenu.MainMenuListener {

            override fun onOrderChanged(orderByName: Boolean) {
                Log.e(tag, "onOrderChanged...orderByName = $orderByName")
            }

            override fun onIncludeSystemApp(includeSystemApp: Boolean) {
                Log.e(tag, "onIncludeSystemApp...includeSystemApp = $includeSystemApp")
            }

            override fun onIncludeReleaseApp(includeReleaseApp: Boolean) {
                Log.e(tag, "onIncludeReleaseApp...includeReleaseApp = $includeReleaseApp")
            }

            override fun onIncludeDebugApp(includeDebugApp: Boolean) {
                Log.e(tag, "onIncludeDebugApp...includeDebugApp = $includeDebugApp")
            }

            override fun onQueryTextChange(newText: String) {
                Log.e(tag, "onQueryTextChange...newText = $newText")
            }

        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (::mainMenu.isInitialized) {
            if (mainMenu.onOptionsItemSelected(item))
                return true
        }
        return super.onOptionsItemSelected(item)
    }


}