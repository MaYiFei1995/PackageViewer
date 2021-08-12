package com.mai.packageviewer.view

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import androidx.core.view.forEach
import com.google.gson.Gson
import com.mai.packageviewer.App
import com.mai.packageviewer.R
import com.mai.packageviewer.setting.MainSettings

class MainMenu(menu: Menu, val activity: Activity) {

    var mainMenuListener: MainMenuListener? = null
    lateinit var searchViewSearchButton: View
    lateinit var searchViewCloseButton: View

    var orderByName = true
    var showSystemApp = false
    var showReleaseApp = true
    var showDebugApp = true
    var showTestOnlyApp = true
    var showGameApp = true

    var searchViewStatus = false

    init {
        menu.forEach {
            when (it.itemId) {
                R.id.menuTitle -> {
                    val spannableString = SpannableString(it.title)
                    spannableString.setSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                App.app,
                                R.color.design_default_color_primary
                            )
                        ),
                        0,
                        spannableString.length,
                        0
                    )
                    it.title = spannableString
                }
                R.id.order_by_name -> {
                    it.isChecked = MainSettings.INSTANCE.getBool(MainSettings.ORDER_BY_NAME, true)
                    orderByName = it.isChecked
                }
                R.id.order_by_date -> {
                    it.isChecked = !MainSettings.INSTANCE.getBool(MainSettings.ORDER_BY_NAME, true)
                    orderByName = !it.isChecked
                }
                R.id.show_system_app -> {
                    it.isChecked =
                        MainSettings.INSTANCE.getBool(MainSettings.SHOW_SYSTEM_APP, false)
                    showSystemApp = it.isChecked
                }
                R.id.show_release_app -> {
                    it.isChecked =
                        MainSettings.INSTANCE.getBool(MainSettings.SHOW_RELEASE_APP, true)
                    showReleaseApp = it.isChecked
                }
                R.id.show_debug_app -> {
                    it.isChecked = MainSettings.INSTANCE.getBool(MainSettings.SHOW_DEBUG_APP, true)
                    showDebugApp = it.isChecked
                }
                R.id.show_test_only_app -> {
                    it.isChecked =
                        MainSettings.INSTANCE.getBool(MainSettings.SHOW_TEST_ONLY_APP, true)
                    showTestOnlyApp = it.isChecked
                }
                R.id.show_game_app -> {
                    it.isChecked = MainSettings.INSTANCE.getBool(MainSettings.SHOW_GAME_APP, true)
                    showGameApp = it.isChecked
                }
                R.id.app_bar_search -> {
                    val searchView = it.actionView as SearchView
                    searchView.queryHint = "输入应用名或包名"
                    searchViewSearchButton = searchView.findViewById(androidx.appcompat.R.id.search_button)
                    searchViewCloseButton = searchView.findViewById(androidx.appcompat.R.id.search_close_btn)
                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            return true
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            if (newText != null)
                                mainMenuListener?.onQueryTextChange(newText)
                            return true
                        }
                    })
                }
            }
        }
    }

    fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.order_by_name -> {
                item.isChecked = true
                MainSettings.INSTANCE.setBool(MainSettings.ORDER_BY_NAME, true)

                orderByName = true
                mainMenuListener?.onOrderChanged(true)
            }
            R.id.order_by_date -> {
                item.isChecked = true
                MainSettings.INSTANCE.setBool(MainSettings.ORDER_BY_NAME, false)

                orderByName = false
                mainMenuListener?.onOrderChanged(false)
            }
            R.id.show_system_app -> {
                val b = !item.isChecked
                item.isChecked = b
                MainSettings.INSTANCE.setBool(MainSettings.SHOW_SYSTEM_APP, b)

                showSystemApp = b
                mainMenuListener?.onIncludeSystemApp(b)
            }
            R.id.show_release_app -> {
                val b = !item.isChecked
                item.isChecked = b
                MainSettings.INSTANCE.setBool(MainSettings.SHOW_RELEASE_APP, b)

                showReleaseApp = b
                mainMenuListener?.onIncludeReleaseApp(b)
            }
            R.id.show_debug_app -> {
                val b = !item.isChecked
                item.isChecked = b
                MainSettings.INSTANCE.setBool(MainSettings.SHOW_DEBUG_APP, b)

                showDebugApp = b
                mainMenuListener?.onIncludeDebugApp(b)
            }
            R.id.show_test_only_app -> {
                val b = !item.isChecked
                item.isChecked = b
                MainSettings.INSTANCE.setBool(MainSettings.SHOW_TEST_ONLY_APP, b)

                showTestOnlyApp = b
                mainMenuListener?.onIncludeTestOnlyApp(b)
            }
            R.id.show_game_app -> {
                val b = !item.isChecked
                item.isChecked = b
                MainSettings.INSTANCE.setBool(MainSettings.SHOW_GAME_APP, b)

                showGameApp = b
                mainMenuListener?.onIncludeGameApp(b)
            }
            R.id.menu_about -> {
                showAboutDialog()
            }
            else -> {
                return false
            }
        }
        return true
    }

    fun handleBackPresses(): Boolean {
        return if (searchViewSearchButton.visibility != View.VISIBLE) {
            Log.e("TEST", "true...")
            searchViewCloseButton.performClick()
            true
        } else {
            false
        }
    }

    private fun showAboutDialog() {
        val webView =
            LayoutInflater.from(activity).inflate(R.layout.dialog_about, null, false) as WebView
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url != null)
                    if (url.startsWith("https://")) {
                        try {
                            activity.startActivity(
                                Intent(Intent.ACTION_VIEW).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    .setData(Uri.parse(url))
                            )
                            return true
                        } catch (ignore: ActivityNotFoundException) {
                        }
                    }
                return super.shouldOverrideUrlLoading(view, url)
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return shouldOverrideUrlLoading(view, request?.url.toString())
            }
        }

        webView.loadUrl("file:///android_asset/About.html")

        AlertDialog.Builder(activity)
            .setTitle("About")
            .setView(webView)
            .create()
            .show()
    }

    interface MainMenuListener {
        fun onOrderChanged(orderByName: Boolean)
        fun onIncludeSystemApp(includeSystemApp: Boolean)
        fun onIncludeReleaseApp(includeReleaseApp: Boolean)
        fun onIncludeDebugApp(includeDebugApp: Boolean)
        fun onIncludeTestOnlyApp(includeTestOnlyApp: Boolean)
        fun onIncludeGameApp(includeGameApp: Boolean)
        fun onQueryTextChange(newText: String)
    }

}