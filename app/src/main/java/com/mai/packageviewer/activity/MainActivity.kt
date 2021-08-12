package com.mai.packageviewer.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mai.packageviewer.BuildConfig
import com.mai.packageviewer.R
import com.mai.packageviewer.adapter.AppAdapter
import com.mai.packageviewer.data.AppInfo
import com.mai.packageviewer.databinding.ActivityMainBinding
import com.mai.packageviewer.view.MainMenu


class MainActivity : AppCompatActivity() {
    private val tag = MainActivity::class.simpleName
    private lateinit var binder: ActivityMainBinding
    private lateinit var mainMenu: MainMenu

    private var appInfoList = ArrayList<AppInfo>()
    private var appInfoFilterList = ArrayList<AppInfo>()
    private val appAdapter = AppAdapter(appInfoFilterList)

    private var onBackPressedTimeStamp = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binder.root)
        initRecyclerView()
    }


    private fun initRecyclerView() {
        binder.recycler.layoutManager = LinearLayoutManager(this)
        binder.recycler.adapter = appAdapter
    }

    private val installedPackages: MutableList<PackageInfo> by lazy {
        val flags =
            PackageManager.GET_META_DATA or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                PackageManager.GET_SIGNING_CERTIFICATES else PackageManager.GET_SIGNATURES)
        packageManager.getInstalledPackages(flags)
    }

    private val thread = Thread {
        appInfoList.clear()
        installedPackages.forEach {
            val appInfo = AppInfo(it)
            appInfoList.add(appInfo)
        }
        onOptionsChanged()
    }

    private fun onDataSetChanged() {
        val packages = installedPackages
        if (packages.size < 15) {
            Snackbar.make(binder.root, "无法获取应用列表，请检查权限", Snackbar.LENGTH_INDEFINITE)
                .setAction("去授权") {
                    try {
                        val intent = Intent().setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            intent.data = Uri.fromParts("package", packageName, null)
                        } else {
                            intent.action = Intent.ACTION_VIEW
                            intent.setClassName(
                                "com.android.settings",
                                "com.android.setting.InstalledAppDetails"
                            )
                            intent.putExtra(
                                "com.android.settings.ApplicationPkgName",
                                packageName
                            )
                        }
                        startActivity(intent)
                    } catch (e: Exception) {
                        Snackbar.make(binder.root, "请手动开启获取应用列表权限", Snackbar.LENGTH_LONG)
                            .show()
                    }
                }
                .show()
        }
        showLoading()
        thread.start()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onOptionsChanged() {
        runOnUiThread {
            appInfoFilterList.clear()
            appInfoFilterList.addAll(appInfoList.filterNot {
                it.packageName == BuildConfig.APPLICATION_ID
                        || (!mainMenu.showSystemApp && it.isSystemApp)
                        || (!mainMenu.showReleaseApp && !it.isDebugApp)
                        || (!mainMenu.showDebugApp && it.isDebugApp)
                        || (!mainMenu.showTestOnlyApp && it.isTestOnlyApp)
                        || (!mainMenu.showGameApp && it.isGameApp)
            })
            hideLoading()
            appAdapter.notifyDataSetChanged()
        }
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
                onOptionsChanged()
            }

            override fun onIncludeReleaseApp(includeReleaseApp: Boolean) {
                Log.e(tag, "onIncludeReleaseApp...includeReleaseApp = $includeReleaseApp")
                onOptionsChanged()
            }

            override fun onIncludeDebugApp(includeDebugApp: Boolean) {
                Log.e(tag, "onIncludeDebugApp...includeDebugApp = $includeDebugApp")
                onOptionsChanged()
            }

            override fun onIncludeTestOnlyApp(includeTestOnlyApp: Boolean) {
                Log.e(tag, "onIncludeTestOnlyApp...includeTestOnlyApp = $includeTestOnlyApp")
                onOptionsChanged()
            }

            override fun onIncludeGameApp(includeGameApp: Boolean) {
                Log.e(tag, "onIncludeGameApp...includeGameApp = $includeGameApp")
                onOptionsChanged()
            }

            override fun onQueryTextChange(newText: String) {
                Log.e(tag, "onQueryTextChange...newText = $newText")
            }

        }

        onDataSetChanged()
        return true
    }

    private fun showLoading() {
        binder.loadingView.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binder.loadingView.visibility = View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (::mainMenu.isInitialized) {
            if (mainMenu.onOptionsItemSelected(item))
                return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mainMenu.handleBackPresses()) {
                true
            } else {
                if (System.currentTimeMillis() - onBackPressedTimeStamp > 2000L) {
                    onBackPressedTimeStamp = System.currentTimeMillis()
                    Snackbar.make(binder.root, "再按一次退出", 2000).show()
                    true
                } else {
                    try {
                        if (thread.isAlive) {
                            thread.interrupt()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    super.onKeyDown(keyCode, event)
                }
            }
        } else {
            super.onKeyDown(keyCode, event)
        }
    }


}