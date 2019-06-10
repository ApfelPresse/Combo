package com.example.myapplication

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.TextView
import com.example.myapplication.adapter.ComboAdapter
import com.example.myapplication.adapter.SettingsAdapter
import com.example.myapplication.countDown.Timer
import com.example.myapplication.gen.getCombos
import com.example.myapplication.gen.getTClasses
import com.example.myapplication.model.TClass
import com.example.myapplication.model.combo.Combo
import com.example.myapplication.model.combo.Strike
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.list
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * load settings view
     */
    @TargetApi(Build.VERSION_CODES.N)
    fun settings(view: View) {
        setContentView(R.layout.settings_layout)
        val list: List<TClass> = Json.parse(TClass.serializer().list, getTClasses())
        runOnUiThread {
            val settingsAdapter = SettingsAdapter(applicationContext, list) { tcl ->
                var st = Settings()
                st.tClass = tcl
                combo(st)
            }
            findViewById<ListView>(R.id.listView).adapter = settingsAdapter
        }
    }


    private fun updateRoundStatus(text: String) {
        val view = findViewById<TextView>(R.id.chronometerStatus)
        view.text = text
    }

    private fun updateComboStatus(text: String) {
        val view = findViewById<TextView>(R.id.combo)
        view.text = text
    }

    private fun updateTime(millis: Long) {
        findViewById<TextView>(R.id.time).text = String.format(
            "%d:%d:%d",
            TimeUnit.MILLISECONDS.toMinutes(millis),
            TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(
                    millis
                )
            ),
            TimeUnit.MILLISECONDS.toMillis(millis) - TimeUnit.SECONDS.toMillis(
                TimeUnit.MILLISECONDS.toSeconds(
                    millis
                )
            )

        )
    }

    /**
     * load combo view
     */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun combo(settings: Settings) {
        setContentView(R.layout.combo_layout)
        val list: List<Combo> = Json.parse(Combo.serializer().list, getCombos())
        runOnUiThread {
            val settingsAdapter = ComboAdapter(applicationContext, list) { combo ->
                settings.combo = combo
                countDown(settings)
            }
            findViewById<ListView>(R.id.listView).adapter = settingsAdapter
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun countDown(settings: Settings) {
        setContentView(R.layout.countdown_layout)
        val timer = object : Timer() {

            override fun onFinishCombo() {
                when (settings.currentStatus) {
                    Settings.Status.TRAIN -> startComboTimer()
                    Settings.Status.REST, Settings.Status.STOP, Settings.Status.START -> updateComboStatus("")
                }
            }

            override fun initQueue(): Queue<Pair<Settings.Status, Int>> {
                val queue: Queue<Pair<Settings.Status, Int>> = ArrayDeque<Pair<Settings.Status, Int>>()
                for (i in 1..settings.tClass.rounds) {
                    var element = Pair(Settings.Status.REST, settings.tClass.breaksInMSec)
                    if (i == 1) {
                        element = Pair(Settings.Status.START, 3000)
                    }
                    queue.add(element)
                    queue.add(Pair(Settings.Status.TRAIN, settings.tClass.roundsInMSec))
                }
                return queue
            }

            override fun onEnd() {
                settings.currentStatus = Settings.Status.STOP
                updateRoundStatus("Haste fein Gemacht!")
            }

            override fun nextCombo(): Long {
                if (settings.currentStatus == Settings.Status.TRAIN) {
                    var i = chooseOnWeight(settings.combo) + 1

                    var comboText = "$i"
                    if (settings.tClass.additionalStrikes.isNotEmpty() && Math.random() <= settings.tClass.additionalStrikesProb) {
                        val addition = settings.tClass.additionalStrikes[chooseOnWeight(settings.tClass)].abbreviation

                        // prefix suffix
                        comboText = if (Math.random() <= 0.5) {
                            "$i + $addition"
                        } else {
                            "$addition + $i"
                        }
                        i += 1
                    }
                    updateComboStatus(comboText)
                    val l: Int = i * settings.tClass.timePerStrike
                    return l.toLong()
                }
                return 100
            }

            override fun onPoll(status: Pair<Settings.Status, Int>) {
                settings.currentStatus = status.first
                if (status.first == Settings.Status.START) {
                    updateRoundStatus("GET READY!!")
                }

                if (status.first == Settings.Status.REST) {
                    updateRoundStatus("REST")
                }

                if (status.first == Settings.Status.TRAIN) {
                    settings.currentRound += 1
                    updateRoundStatus("Round " + settings.currentRound)
                    startComboTimer()
                }
            }

            override fun onTickRound(millis: Long) {
                updateTime(millis)
            }
        }
        timer.start()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun chooseOnWeight(combo: Combo): Int {
        return chooseOnWeight(combo.strikes, combo.weights)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun chooseOnWeight(t: TClass): Int {
        return chooseOnWeight(t.additionalStrikes, t.weights)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun chooseOnWeight(strikes: MutableList<Strike>, weights: MutableList<Int>): Int {
        var completeWeight = weights.stream().mapToInt(Int::toInt).sum()
        val r = Math.random() * completeWeight
        var countWeight = 0.0
        for (i in 0..strikes.size) {
            countWeight += weights[i]
            if (countWeight >= r)
                return i
        }
        throw RuntimeException("Should never be shown.")
    }

}