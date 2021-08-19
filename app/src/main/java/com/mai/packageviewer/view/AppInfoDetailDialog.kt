package com.mai.packageviewer.view

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mai.packageviewer.adapter.AppInfoDetailAdapter
import com.mai.packageviewer.data.AppInfo
import com.mai.packageviewer.databinding.DialogAppinfoDetailBinding
import com.mai.packageviewer.util.AppInfoHelper.toDetailList

/**
 * 详情页Dialog
 */
class AppInfoDetailDialog(context: Context, data: AppInfo) {

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
        binder.appInfoDetailRecyclerView.adapter = AppInfoDetailAdapter(data.toDetailList())
        // 通过AlertDialog实现


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
                }

            }
        }

        alertDialog = AlertDialog.Builder(context).setCancelable(true).setView(binder.root).create()
        alertDialog.show()
    }

}