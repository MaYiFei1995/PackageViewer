package com.mai.packageviewer.data

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import com.mai.packageviewer.App


class AppInfo(packageInfo: PackageInfo) {

    val isSystemApp =
        ((packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
                ) or (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0)

    val isDebugApp = (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

    val isTestOnlyApp = (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_TEST_ONLY) != 0

    val isGameApp =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            (packageInfo.applicationInfo.category == ApplicationInfo.CATEGORY_GAME) or ((packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_IS_GAME) != 0)
        } else {
            packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_IS_GAME != 0
        }

    val className = packageInfo.applicationInfo.className

    val minSdkVersion =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) packageInfo.applicationInfo.minSdkVersion else -1

    val targetSdkVersion = packageInfo.applicationInfo.targetSdkVersion

    val packageName = packageInfo.applicationInfo.packageName

    val versionCode =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) packageInfo.longVersionCode else packageInfo.versionCode.toLong()

    val versionName = packageInfo.versionName

    val firstInstallTime = packageInfo.firstInstallTime

    val lastUpdateTime = packageInfo.lastUpdateTime

    val singingInfo: Array<Signature> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (packageInfo.signingInfo.hasMultipleSigners())
                packageInfo.signingInfo.apkContentsSigners
            else
                packageInfo.signingInfo.signingCertificateHistory
        } else
            packageInfo.signatures

    var label = ""

    var iconDrawable: Drawable? = null

    var hasInit = false

    init {
        val applicationInfo = packageInfo.applicationInfo
        label = applicationInfo.loadLabel(App.app.packageManager).toString()
        iconDrawable = applicationInfo.loadIcon(App.app.packageManager)
    }
}
