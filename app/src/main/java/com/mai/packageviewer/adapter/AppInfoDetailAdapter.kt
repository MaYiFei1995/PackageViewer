package com.mai.packageviewer.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.snackbar.Snackbar
import com.mai.packageviewer.R
import com.mai.packageviewer.data.BaseKVObject
import com.mai.packageviewer.view.AppInfoDetailDialog

/**
 * 应用详情的Adapter，显示详细信息
 */
class AppInfoDetailAdapter(data: MutableList<BaseKVObject<String>>, val dialog: AppInfoDetailDialog) :
    BaseQuickAdapter<BaseKVObject<String>, BaseViewHolder>(R.layout.item_app_info_detail, data) {

    override fun convert(holder: BaseViewHolder, item: BaseKVObject<String>) {
        holder.setText(R.id.keyTv, item.k)
        holder.setText(R.id.valueTv, item.v)
        // 长按value复制信息，主要用于复制签名信息
        holder.getView<TextView>(R.id.valueTv).setOnLongClickListener {
            val valueStr = (it as TextView).text.toString()
            val clipboardManager =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager.setPrimaryClip(
                ClipData.newPlainText(
                    holder.getView<TextView>(R.id.keyTv).text.toString(),
                    valueStr
                )
            )
            Snackbar.make(it, "复制成功", Snackbar.LENGTH_SHORT).setAction("发送") { dialog.share(dialog.context, valueStr) }.show()
            true
        }
    }
}