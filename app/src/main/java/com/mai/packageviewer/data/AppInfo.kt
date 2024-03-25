package com.mai.packageviewer.data

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.Signature
import android.graphics.drawable.Drawable
import android.os.Build
import com.mai.packageviewer.App
import com.mai.packageviewer.util.AppInfoHelper.calculateMD5
import com.mai.packageviewer.view.MainMenu
import com.mai.packageviewer.x.SDKAnalyzerImpl.Companion.parseSdk
import java.io.File
import java.lang.StringBuilder
import java.math.RoundingMode
import java.text.DecimalFormat


class AppInfo(packageInfo: PackageInfo) {

    /**
     * 系统App
     */
    val isSystemApp =
        ((packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
                ) or (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0)

    /**
     * Debug
     */
    val isDebugApp = (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

    /**
     * TestOnly
     */
    val isTestOnlyApp = (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_TEST_ONLY) != 0

    /**
     * Game
     */
    val isGameApp =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            (packageInfo.applicationInfo.category == ApplicationInfo.CATEGORY_GAME) or ((packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_IS_GAME) != 0)
        } else {
            packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_IS_GAME != 0
        }

    /**
     * Application的className
     */
    val className: String? = packageInfo.applicationInfo.className

    /**
     * minSdkVersion
     * AndroidN之前不支持
     */
    val minSdkVersion =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) packageInfo.applicationInfo.minSdkVersion else -1

    /**
     * targetSdkVersion
     */
    val targetSdkVersion = packageInfo.applicationInfo.targetSdkVersion

    /**
     * ApplicationID
     */
    val packageName: String = packageInfo.applicationInfo.packageName ?: ""

    /**
     * versionCode
     */
    val versionCode =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) packageInfo.longVersionCode else packageInfo.versionCode.toLong()

    /**
     * versionName
     */
    val versionName: String? = packageInfo.versionName

    /**
     * 首次安装时间
     */
    val firstInstallTime = packageInfo.firstInstallTime

    /**
     * 最后更新时间
     */
    val lastUpdateTime = packageInfo.lastUpdateTime

    /**
     * 签名信息
     */
    val signingInfo: Array<Signature> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (packageInfo.signingInfo.hasMultipleSigners())
                packageInfo.signingInfo.apkContentsSigners
            else
                packageInfo.signingInfo.signingCertificateHistory
        } else
            packageInfo.signatures

    /**
     * AppName
     */
    var label = ""

    /**
     * icon
     */
    var iconDrawable: Drawable? = null

    /**
     * base.apk path
     */
    val apkPath: String = packageInfo.applicationInfo.publicSourceDir

    /**
     * base.apk size
     */
    val apkSize: String
        get() {
            val decimalFormat = DecimalFormat("######0.00 MB")
            decimalFormat.roundingMode = RoundingMode.FLOOR
            return decimalFormat.format(File(apkPath).length() / (1024 * 1024f))
        }

    /**
     * APK文件的MD5
     */
    val apkFileMd5: String
        get() {
            return File(apkPath).calculateMD5()
        }

    /**
     * 加固/框架判断
     */
    val apkShellAndPlat: String
        get() {
            return when (className) {
                "com.stub.StubApp" ->
                    "360加固"
                "s.h.e.l.l.S" ->
                    "爱加密"
                "$packageName.MyWrapperProxyApplication" ->
                    "腾讯加固"
                "com.baidu.protect.StubApplication", "com.sagittarius.v6.StubApplication" ->
                    "百度加固"
                "com.secneo.apkwrapper.ApplicationWrapper", "com.secneo.apkwrapper.AW", "com.SecShell.SecShell.AW" ->
                    "梆梆加固"
                "com.ali.mobisecenhance.ld.StubApplication" ->
                    "阿里加固"
                "arm.StubApp" ->
                    "Arm加固"
                "com.mx.dA.dpa", "com.manxi.shell.MXApplication" ->
                    "蛮犀加固"
                "cn.beingyi.sub.apps.SubApp.SubApplication" ->
                    "随风加固"
                "com.uzmap.pkg.uzapp.UZApplication" ->
                    "ApiCloud"
                "io.dcloud.application.DCloudApplication" ->
                    "DCloud"
                "com.lt.app.App" ->
                    "一门"
                else ->
                    if (className != null && className.endsWith(".StubApp")) {
                        if (is360Pro(className)) {
                            "360高级加固"
                        } else {
                            "未知加固"
                        }
                    } else if (className != null && className.startsWith("com.security.shell.AppStub")) {
                        // com.security.shell.AppStub1 ???
                        "顶象加固?"
                    } else {
                        "未知"
                    }
            }
        }

    private fun is360Pro(appName: String): Boolean {
        val split = packageName.split(".")
        val retSB = StringBuilder()
        split.reversed().forEach {
            if (it != "StubApp") {
                retSB.append(it.reversed()).append(".")
            }
        }
        return appName == retSB.append("StubApp").toString()
    }

    /**
     * App开发平台判断
     */
    var devLang: String = "未知"

    /**
     * 包含的SDK列表
     */
    var sdkInfo: String = packageInfo.parseSdk()

    init {
        val applicationInfo = packageInfo.applicationInfo
        // 耗时操作
        label = applicationInfo.loadLabel(App.app.packageManager).toString()

        if (!MainMenu.initFastMode) {
            iconDrawable = applicationInfo.loadIcon(App.app.packageManager)
        }

        val nativeLibraryDir = applicationInfo.nativeLibraryDir
        if (nativeLibraryDir != null) {
            val libDirPath = File(nativeLibraryDir)
            if (libDirPath.exists()) {
                run getDevLanguage@{
                    val walk = libDirPath.walk()
                    walk.maxDepth(2)
                        .filter {
                            it.isFile && it.extension == "so"
                        }
                        .forEach {
                            when (it.nameWithoutExtension) {
                                "libflutter" -> {
                                    devLang = "Flutter"
                                    return@getDevLanguage
                                }
                                "libreactnativejni" -> {
                                    devLang = "RN"
                                    return@getDevLanguage
                                }
                            }
                        }
                }
            }
        }

    }
}
