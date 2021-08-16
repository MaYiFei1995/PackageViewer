package com.mai.packageviewer.adapter

import android.widget.Filter
import com.mai.packageviewer.data.AppInfo

class AppFilter(data:List<AppInfo>) : Filter() {
    override fun performFiltering(constraint: CharSequence): FilterResults {


    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        TODO("Not yet implemented")
    }
}