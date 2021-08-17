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
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mai.packageviewer.BuildConfig
import com.mai.packageviewer.R
import com.mai.packageviewer.adapter.AppAdapter
import com.mai.packageviewer.data.AppInfo
import com.mai.packageviewer.databinding.ActivityMainBinding
import com.mai.packageviewer.util.AppInfoHelper
import com.mai.packageviewer.view.MainMenu
import net.sourceforge.pinyin4j.PinyinHelper
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.min


class MainActivity : AppCompatActivity() {
    private val tag = MainActivity::class.simpleName
    private lateinit var binder: ActivityMainBinding
    private lateinit var mainMenu: MainMenu

    private var appInfoList = ArrayList<AppInfo>()
    private var appInfoFilterList = ArrayList<AppInfo>()
    private val appAdapter = AppAdapter(appInfoFilterList)

    private var onBackPressedTimeStamp = System.currentTimeMillis()

    private var isPause = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binder.root)
        initRecyclerView()
    }

    override fun onPause() {
        super.onPause()
        isPause = true
    }

    override fun onResume() {
        super.onResume()
        if (isPause && !AppInfoHelper.isRunning)
            onDataSetChanged()
    }

    private fun initRecyclerView() {
        binder.recycler.layoutManager = LinearLayoutManager(this)
        binder.recycler.adapter = appAdapter
    }

    private val installedPackages: MutableList<PackageInfo>
        get() {
            val flags =
                PackageManager.GET_META_DATA or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                    PackageManager.GET_SIGNING_CERTIFICATES else PackageManager.GET_SIGNATURES)
            return packageManager.getInstalledPackages(flags)
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

        AppInfoHelper.handle(packages, object : AppInfoHelper.AppInfoCallback {
            override fun onResult(ret: Vector<MutableList<AppInfo>>) {
                appInfoList.clear()
                ret.forEach {
                    appInfoList.addAll(it)
                }
                onOptionsChanged()
            }
        }, packages.size / 60 + 1)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onOptionsChanged(sortOnly: Boolean = false) {
        runOnUiThread {
            if (!sortOnly) {
                appInfoFilterList.clear()
                appInfoFilterList.addAll(appInfoList.filterNot {
                    it.packageName == BuildConfig.APPLICATION_ID
                            || (!mainMenu.showSystemApp && it.isSystemApp)
                            || (!mainMenu.showReleaseApp && !it.isDebugApp)
                            || (!mainMenu.showDebugApp && it.isDebugApp)
                            || (!mainMenu.showTestOnlyApp && it.isTestOnlyApp)
                            || (!mainMenu.showGameApp && it.isGameApp)
                })
            }
            Log.e("test", "appInfoFilterList.size = ${appInfoFilterList.size}")

            if (mainMenu.orderByName) {
                appInfoFilterList.sortWith { lh, rh ->
                    val charL = lh.label[0].toLowerCase()
                    val charR = rh.label[0].toLowerCase()
                    val strL = if (charL.isLowerCase() || charL.isDigit()) {
                        lh.label.toLowerCase(Locale.getDefault()).toCharArray()
                    } else {
                        PinyinHelper.toHanyuPinyinStringArray(charL)[0].toCharArray()
                    }
                    val strR = if (charR.isLowerCase() || charR.isDigit()) {
                        rh.label.toLowerCase(Locale.getDefault()).toCharArray()
                    } else {
                        PinyinHelper.toHanyuPinyinStringArray(charR)[0].toCharArray()
                    }

                    var result = 1
                    for (i in 0 until min(strL.size, strR.size)) {
                        if (strL[i] != strR[i]) {
                            result = strL[i].toInt() - strR[i].toInt()
                            break
                        } else {
                            continue
                        }
                    }
                    result
                }
            } else {
                appInfoFilterList.sortBy {
                    it.lastUpdateTime
                }
            }
            hideLoading()
            Snackbar.make(binder.root, "共找到${appInfoFilterList.size}个应用", Snackbar.LENGTH_SHORT)
                .show()
            appAdapter.update(appInfoFilterList)
            appAdapter.notifyDataSetChanged()
        }
    }

//    private fun onQueryTextChange(newText: String) {
//
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        mainMenu = MainMenu(menu!!, this)
        mainMenu.mainMenuListener = object : MainMenu.MainMenuListener {

            override fun onOrderChanged(orderByName: Boolean) {
                onOptionsChanged(true)
            }

            override fun onIncludeSystemApp(includeSystemApp: Boolean) {
                onOptionsChanged()
            }

            override fun onIncludeReleaseApp(includeReleaseApp: Boolean) {
                onOptionsChanged()
            }

            override fun onIncludeDebugApp(includeDebugApp: Boolean) {
                onOptionsChanged()
            }

            override fun onIncludeTestOnlyApp(includeTestOnlyApp: Boolean) {
                onOptionsChanged()
            }

            override fun onIncludeGameApp(includeGameApp: Boolean) {
                onOptionsChanged()
            }

            override fun onQueryTextChange(newText: String) {
                appAdapter.filter.filter(newText)
//                onQueryTextChange(newText)
            }

        }

        onDataSetChanged()
        return true
    }

    private fun showLoading() {
        binder.loadingView.visibility = View.VISIBLE
        mainMenu.menu.setGroupEnabled(R.id.menu_group_sort, false)
        mainMenu.menu.setGroupEnabled(R.id.menu_group_filters, false)
    }

    private fun hideLoading() {
        binder.loadingView.visibility = View.GONE
        mainMenu.menu.setGroupEnabled(R.id.menu_group_sort, true)
        mainMenu.menu.setGroupEnabled(R.id.menu_group_filters, true)
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
                        if (AppInfoHelper.isRunning) {
                            AppInfoHelper.forceStop()
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