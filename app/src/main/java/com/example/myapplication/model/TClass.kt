package com.example.myapplication.model

import com.example.myapplication.model.combo.Strike
import kotlinx.serialization.Serializable

@Serializable
class TClass(val name: String) {

    var rounds: Int = 1

    var roundsInMSec: Int = 60000

    var breaksInMSec: Int = 60000

    // ms per strike in combo
    var timePerStrike: Int = 700

    var additionalStrikesProb: Double = 0.1

    val additionalStrikes: MutableList<Strike> = ArrayList()

    /**
     * additional strike weights
     */
    var weights: MutableList<Int> = ArrayList()
}