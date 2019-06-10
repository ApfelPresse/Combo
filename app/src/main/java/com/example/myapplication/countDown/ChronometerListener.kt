package com.example.myapplication.countDown

import android.os.SystemClock
import android.widget.Chronometer
import com.example.myapplication.Settings
import java.util.*

abstract class ChronometerListener : Chronometer.OnChronometerTickListener {

    val queue: Queue<Pair<Settings.Status, Int>> = ArrayDeque<Pair<Settings.Status, Int>>()
    var comboTime = 0L

    override fun onChronometerTick(chronometer: Chronometer?) {
        run {
            val systemCurrTime = SystemClock.elapsedRealtime()

            // show next combo
            val deltaComboTime = systemCurrTime - comboTime
            if (deltaComboTime > 0) {
                println(deltaComboTime)
                comboTime = systemCurrTime + tickCombo()
            }

            // round time
            val chronometerBaseTime = chronometer?.base
            val deltaTime = systemCurrTime - chronometerBaseTime!!
            if (deltaTime > 0) {
                if (queue.isEmpty()) {
                    end()
                    chronometer?.stop()
                } else {
                    var po = queue.poll()
                    pollListener(po)
                    chronometer?.base = SystemClock.elapsedRealtime() + po.second
                }
            }
        }
    }

    abstract fun tickCombo(): Int

    abstract fun pollListener(status: Pair<Settings.Status, Int>)

    abstract fun end()
}