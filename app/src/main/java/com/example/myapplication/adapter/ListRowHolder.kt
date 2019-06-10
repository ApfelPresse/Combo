package com.example.myapplication.adapter

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.example.myapplication.R

class ListRowHolder(row: View?) {
    val name: TextView = row?.findViewById(R.id.name) as TextView
    val linearLayout: LinearLayout = row?.findViewById(R.id.linearLayout) as LinearLayout
}