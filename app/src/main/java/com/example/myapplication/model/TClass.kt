package com.example.myapplication.model

import com.example.myapplication.model.combo.Combo
import com.example.myapplication.model.combo.Strike
import kotlinx.serialization.Serializable

@Serializable
class TClass(val name: String) {

    var rounds: Int = 1

    var roundsInMSec: Int = 60000

    var breaksInMSec: Int = 60000

    // ms per strike in combo
    var strikeDelateTime: Int = 700

    val additionalStrikes: List<Strike> = emptyList()
}