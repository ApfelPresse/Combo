package com.example.myapplication

import com.example.myapplication.model.TClass
import com.example.myapplication.model.combo.Combo

/**
 * holds selected settings and status
 */
class Settings {

    enum class Status {
        START, TRAIN, STOP, PAUSE, REST
    }

    var currentRound: Int = 0

    var currentStatus: Status = Status.STOP

    lateinit var combo: Combo

    lateinit var tClass: TClass


}