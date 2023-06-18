package com.janicolas.puzzlecollector.menu

import java.io.Serializable

class Section(private var name: String, private var image: Int) : Serializable {
    fun getName():String {return name}
    fun getImage():Int {return image}
}