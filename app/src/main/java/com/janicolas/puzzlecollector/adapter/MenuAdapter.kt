package com.janicolas.puzzlecollector.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import net.iessochoa.puzzlecollector.R
import com.janicolas.puzzlecollector.menu.Section

class MenuAdapter(private val context: Context, private val menu:List<Section>):BaseAdapter() {

    override fun getCount(): Int {
        return menu.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val root:View = LayoutInflater.from(context).inflate(R.layout.menu_item, parent, false)

        val text:TextView = root.findViewById(R.id.text)
        val image:ImageView = root.findViewById(R.id.image)

        if(position == 2)
            text.textSize = 22F
        else
            text.textSize = 24F
        text.text = menu[position].getName()
        image.setImageResource(menu[position].getImage())

        return root
    }
}