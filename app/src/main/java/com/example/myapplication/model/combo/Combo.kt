package com.example.myapplication.model.combo

import kotlinx.serialization.Serializable

@Serializable
class Combo(val name: String?) {

    val description: String? = null

    val strikes: MutableList<Strike> = ArrayList()

    /**
     * strike weights
     */
    var weights: MutableList<Int> = ArrayList()
}