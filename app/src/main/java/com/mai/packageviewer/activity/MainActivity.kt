package com.mai.packageviewer.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.mai.packageviewer.BuildConfig
import com.mai.packageviewer.R
import com.mai.packageviewer.data.AppInfo
import com.mai.packageviewer.databinding.ActivityMainBinding
import com.mai.packageviewer.view.MainMenu


class MainActivity : AppCompatActivity() {
    private val tag = MainActivity::class.simpleName
    private lateinit var binder: ActivityMainBinding
    private lateinit var mainMenu: MainMenu

    private var appInfoList = ArrayList<AppInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binder.root)
        initRecyclerView()
        onDataSetChanged()
    }

    private fun initRecyclerView(){
        binder.recycler.layoutManager = LinearLayoutManager(this)

    }

    private fun onDataSetChanged() {
        val start = System.currentTimeMillis()
        val installedPackages =
            packageManager.getInstalledPackages(PackageManager.GET_META_DATA or PackageManager.GET_SIGNING_CERTIFICATES or PackageManager.GET_SIGNATURES)
        if (installedPackages.size < 15) {
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
                            );
                            intent.putExtra(
                                "com.android.settings.ApplicationPkgName",
                                packageName
                            );
                        }
                        startActivity(intent)
                    } catch (e: Exception) {
                        Snackbar.make(binder.root, "请手动开启获取应用列表权限", Snackbar.LENGTH_LONG)
                            .show()
                    }
                }
                .show()
        }

        appInfoList.clear()
        installedPackages.forEach {
            if (it.packageName == BuildConfig.APPLICATION_ID)
                return
            val appInfo = AppInfo(it)
            appInfo.label = it.applicationInfo.loadLabel(packageManager).toString()
            appInfoList.add(appInfo)
//            Log.e("test", Gson().toJson(appInfo))
        }

        Log.e("TEST", "onInitEnd...duration = ${(System.currentTimeMillis() - start)/1000f}s")
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