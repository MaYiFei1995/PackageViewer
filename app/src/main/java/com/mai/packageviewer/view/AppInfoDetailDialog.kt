package com.mai.packageviewer.view

import android.content.Context
import android.graphics.Rect
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
        AlertDialog.Builder(context).setCancelable(true).setView(binder.root).create().show()
    }

}