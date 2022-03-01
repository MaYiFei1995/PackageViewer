package com.mai.packageviewer.view

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mai.packageviewer.BuildConfig
import com.mai.packageviewer.adapter.AppInfoDetailAdapter
import com.mai.packageviewer.data.AppInfo
import com.mai.packageviewer.databinding.DialogAppinfoDetailBinding
import com.mai.packageviewer.util.AppInfoHelper.toDetailList
import java.io.File

/**
 * 详情页Dialog
 */
class AppInfoDetailDialog(val context: Context, data: AppInfo) {

    init {
        var alertDialog: AlertDialog? = null
        val binder = DialogAppinfoDetailBinding.inflate(LayoutInflater.from(context))
        binder.appInfoDetailRecyclerView.layoutManager = LinearLayoutManager(context)
        // 添加位移，通过background实现分割线效果
        binder.appInfoDetailRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.set(0, 1, 0, 1)
            }
        })
        binder.appInfoDetailRecyclerView.adapter = AppInfoDetailAdapter(data.toDetailList(), this)
        // 通过AlertDialog实现

        // 导出apk-file
        binder.exportApkFile.setOnClickListener {
            share(
                context,
                apkFileUri = FileProvider.getUriForFile(
                    context,
                    "${BuildConfig.APPLICATION_ID}.fileProvider",
                    File(data.apkPath)
                )
            )
        }

        // 导出adb-pull
        binder.exportApkPath.setOnClickListener {
            // get "ro.serialno" err ...
            share(context, "adb pull ${data.apkPath}")
        }

        // 设置
        binder.settingsBtn.setOnClickListener {
            try {
                context.startActivity(
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(
                        Uri.fromParts("package", data.packageName, null)
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "跳转设置页失败", Toast.LENGTH_SHORT).show()
            }
        }

        // 拉起
        binder.launchBtn.setOnClickListener {
            try {
                val intent: Intent =
                    context.packageManager.getLaunchIntentForPackage(data.packageName)
                        ?: throw Exception("getLaunchIntentForPackage:${data.packageName} return null!")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                val packageManager: PackageManager = context.packageManager
                val resolveInfo =
                    packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                if (resolveInfo.size > 0) {
                    context.startActivity(intent)
                }
                alertDialog?.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "拉起失败", Toast.LENGTH_SHORT).show()
            }
        }

        // 卸载
        if (data.isSystemApp) {
            binder.uninstallBtn.isEnabled = false
        } else {
            binder.uninstallBtn.setOnClickListener {
                try {
                    context.startActivity(
                        Intent().setAction(Intent.ACTION_UNINSTALL_PACKAGE)
                            .setData(Uri.parse("package:${data.packageName}"))
                    )
                    alertDialog?.dismiss()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, "卸载失败", Toast.LENGTH_SHORT).show()
                }

            }
        }

        alertDialog = AlertDialog.Builder(context).setCancelable(true).setView(binder.root).create()
        alertDialog.show()
    }

    /**
     * 调用系统分享
     */
    fun share(context: Context, adbCmdStr: String? = null, apkFileUri: Uri? = null) {
        val intent = Intent(Intent.ACTION_SEND)
        if (adbCmdStr != null) {
            intent.putExtra(Intent.EXTRA_TEXT, adbCmdStr)
            intent.type = "text/plain"
        } else if (apkFileUri != null) {
            intent.putExtra(Intent.EXTRA_STREAM, apkFileUri)
            intent.type = "*/*"
        }
        context.startActivity(Intent.createChooser(intent, "发送至..."))
    }

}