package com.mai.packageviewer.view

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import com.mai.packageviewer.App
import com.mai.packageviewer.R
import com.mai.packageviewer.setting.MainSettings

/**
 * 筛选与搜索菜单
 */
class MainMenu(val menu: Menu, val activity: Activity) {

    /**
     * 回调
     */
    var mainMenuListener: MainMenuListener? = null

    // 搜索框，用于判断处理back事件
    private lateinit var searchViewSearchButton: View
    private lateinit var searchViewCloseButton: View

    // 筛选条件
    var orderByName = true
    var showSystemApp = false
    var showReleaseApp = true
    var showDebugApp = true
    var showTestOnlyApp = true
    var showGameApp = true

    // 框架筛选
    var platFilter: String =
        MainSettings.INSTANCE.getString(MainSettings.SHOW_APPS_PLAT, MainSettings.SHOW_ALL_APPS)

    companion object {
        // 快速模式
        var initFastMode = MainSettings.INSTANCE.getBool(MainSettings.INIT_FAST_MODE, false)
    }

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
                    if (!MainSettings.INSTANCE.getBool(MainSettings.ORDER_BY_NAME, true)) {
                        it.isChecked = true
                        orderByName = !it.isChecked
                    }
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

                R.id.show_all_app -> {
                    it.isChecked = platFilter == MainSettings.SHOW_APPS_PLAT
                }
                R.id.show_api_cloud_app -> {
                    if (platFilter == MainSettings.SHOW_API_CLOUD_APPS) {
                        it.isChecked = true
                    }
                }
                R.id.show_dcloud_app -> {
                    if (platFilter == MainSettings.SHOW_DCLOUD_APPS) {
                        it.isChecked = true
                    }
                }
                R.id.show_yimen_app -> {
                    if (platFilter == MainSettings.SHOW_YI_MEN_APPS) {
                        it.isChecked = true
                    }
                }
                R.id.show_flutter_app -> {
                    if (platFilter == MainSettings.SHOW_FLUTTER_APPS) {
                        it.isChecked = true
                    }
                }
                R.id.show_rn_app -> {
                    if (platFilter == MainSettings.SHOW_RN_APPS) {
                        it.isChecked = true
                    }
                }
                R.id.fast_mode -> {
                    it.isChecked = MainSettings.INSTANCE.getBool(MainSettings.INIT_FAST_MODE, false)
                    initFastMode = it.isChecked
                }
                R.id.app_bar_search -> {
                    val searchView = it.actionView as SearchView
                    searchView.queryHint = "输入应用名或包名"
                    searchViewSearchButton =
                        searchView.findViewById(androidx.appcompat.R.id.search_button)
                    searchViewCloseButton =
                        searchView.findViewById(androidx.appcompat.R.id.search_close_btn)
                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            // 实时搜索
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

    /**
     * 处理菜单的点击事件
     */
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
            R.id.show_all_app -> {
                item.isChecked = true
                MainSettings.INSTANCE.setString(
                    MainSettings.SHOW_APPS_PLAT,
                    MainSettings.SHOW_ALL_APPS
                )
                platFilter = MainSettings.SHOW_ALL_APPS
                mainMenuListener?.onPlatFilterChange()
            }
            R.id.show_api_cloud_app -> {
                item.isChecked = true
                MainSettings.INSTANCE.setString(
                    MainSettings.SHOW_APPS_PLAT,
                    MainSettings.SHOW_API_CLOUD_APPS
                )
                platFilter = MainSettings.SHOW_API_CLOUD_APPS
                mainMenuListener?.onPlatFilterChange()
            }
            R.id.show_dcloud_app -> {
                item.isChecked = true
                MainSettings.INSTANCE.setString(
                    MainSettings.SHOW_APPS_PLAT,
                    MainSettings.SHOW_DCLOUD_APPS
                )
                platFilter = MainSettings.SHOW_DCLOUD_APPS
                mainMenuListener?.onPlatFilterChange()
            }
            R.id.show_yimen_app -> {
                item.isChecked = true
                MainSettings.INSTANCE.setString(
                    MainSettings.SHOW_APPS_PLAT,
                    MainSettings.SHOW_YI_MEN_APPS
                )
                platFilter = MainSettings.SHOW_YI_MEN_APPS
                mainMenuListener?.onPlatFilterChange()
            }
            R.id.show_flutter_app -> {
                item.isChecked = true
                MainSettings.INSTANCE.setString(
                    MainSettings.SHOW_APPS_PLAT,
                    MainSettings.SHOW_FLUTTER_APPS
                )
                platFilter = MainSettings.SHOW_FLUTTER_APPS
                mainMenuListener?.onPlatFilterChange()
            }
            R.id.show_rn_app -> {
                item.isChecked = true
                MainSettings.INSTANCE.setString(
                    MainSettings.SHOW_APPS_PLAT,
                    MainSettings.SHOW_RN_APPS
                )
                platFilter = MainSettings.SHOW_RN_APPS
                mainMenuListener?.onPlatFilterChange()
            }
            R.id.fast_mode -> {
                val b = !item.isChecked
                item.isChecked = b
                MainSettings.INSTANCE.setBool(MainSettings.INIT_FAST_MODE, b)

                initFastMode = b

                Toast.makeText(activity, "应用重启后生效", Toast.LENGTH_SHORT).show()
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

    /**
     * 处理back事件时的搜索框相应
     */
    fun handleBackPresses(): Boolean {
        return if (searchViewSearchButton.visibility != View.VISIBLE) {
            searchViewCloseButton.performClick()
            true
        } else {
            false
        }
    }

    /**
     * 展示关于界面
     */
    private fun showAboutDialog() {
        val webView =
            LayoutInflater.from(activity).inflate(R.layout.dialog_about, null, false) as WebView
        webView.webViewClient = object : WebViewClient() {

            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url != null)
                    if (url.startsWith("https://")) {
                        try {
                            // 通过浏览器访问
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
                request: WebResourceRequest?,
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

        // 属性过滤
        fun onIncludeSystemApp(includeSystemApp: Boolean)
        fun onIncludeReleaseApp(includeReleaseApp: Boolean)
        fun onIncludeDebugApp(includeDebugApp: Boolean)
        fun onIncludeTestOnlyApp(includeTestOnlyApp: Boolean)
        fun onIncludeGameApp(includeGameApp: Boolean)

        // 框架过滤
        fun onPlatFilterChange()

        // 文字变化
        fun onQueryTextChange(newText: String)
    }

}