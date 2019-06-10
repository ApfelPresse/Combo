package com.example.myapplication.countDown

import android.os.CountDownTimer
import com.example.myapplication.Settings
import java.util.*

abstract class Timer {

    var roundTimer: CountDownTimer? = null
    val roundRefresh: Long = 50

    var comboTimer: CountDownTimer? = null
    val comboRefresh: Long = 50

    var queue: Queue<Pair<Settings.Status, Int>> = ArrayDeque<Pair<Settings.Status, Int>>()

    fun onTickCombo(millisUntilFinished: Long) {

    }

    open fun onTickRound(millisUntilFinished: Long) {

    }

    fun start() {
        queue = initQueue()
        startRoundTimer()
    }

    fun startComboTimer() {
        comboTimer = object : CountDownTimer(nextCombo(), comboRefresh) {
            override fun onTick(millisUntilFinished: Long) {
                onTickCombo(millisUntilFinished)
            }

            override fun onFinish() {
                onFinishCombo()
            }

        }.start()
    }

    fun startRoundTimer() {
        if (!queue.isEmpty()) {
            var po = queue.poll()
            onPoll(po)
            roundTimer = object : CountDownTimer(po.second.toLong(), roundRefresh) {
                override fun onFinish() {
                    startRoundTimer()
                }

                override fun onTick(millisUntilFinished: Long) {
                    onTickRound(millisUntilFinished)
                }
            }.start()
        } else {
            onEnd()
        }
    }

    /**
     * contains START TRAIN PAUSE TRAIN...
     */
    abstract fun initQueue(): Queue<Pair<Settings.Status, Int>>

    /**
     * called when queue is empty
     */
    abstract fun onEnd()

    /**
     * returns next combo in ms
     */
    abstract fun nextCombo(): Long

    /**
     * get called when queue is polled
     */
    abstract fun onPoll(status: Pair<Settings.Status, Int>)

    /**
     * get called when the current combo is finished
     */
    abstract fun onFinishCombo()
}