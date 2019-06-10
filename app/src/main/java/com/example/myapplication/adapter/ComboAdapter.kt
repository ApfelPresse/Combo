package com.example.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.myapplication.R
import com.example.myapplication.model.combo.Combo

class ComboAdapter(
    context: Context,
    arrayListDetails: List<Combo>,
    changeView: (combo: Combo) -> Unit
) : BaseAdapter() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private val arrayListDetails: List<Combo> = arrayListDetails
    private val changeView = changeView

    override fun getCount(): Int {
        return arrayListDetails.size
    }

    override fun getItem(position: Int): Any {
        return arrayListDetails.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val view: View?
        val listRowHolder: ListRowHolder
        if (convertView == null) {
            view = this.layoutInflater.inflate(R.layout.combo_content, parent, false)
            listRowHolder = ListRowHolder(view)
            view.tag = listRowHolder
        } else {
            view = convertView
            listRowHolder = view.tag as ListRowHolder
        }

        listRowHolder.name.text = arrayListDetails.get(position).name
        listRowHolder.linearLayout.setOnClickListener { changeView(arrayListDetails.get(position)) }
        return view
    }
}