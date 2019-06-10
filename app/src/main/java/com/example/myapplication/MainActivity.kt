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


    private fun chronometerQueueChangeListener(text: String) {
        val view = findViewById<TextView>(R.id.chronometerStatus)
        view.text = text
    }

    private fun chronometerComboStatus(text: String) {
        val view = findViewById<TextView>(R.id.combo)
        view.text = text
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
                if (settings.currentStatus == Settings.Status.TRAIN) {
                    startComboTimer()
                }
            }

            override fun initQueue(): Queue<Pair<Settings.Status, Int>> {
                val queue: Queue<Pair<Settings.Status, Int>> = ArrayDeque<Pair<Settings.Status, Int>>()
                for (i in 1..settings.tClass.rounds) {
                    var p = Pair(Settings.Status.REST, settings.tClass.breaksInMSec)
                    if (i == 1) {
                        p = Pair(Settings.Status.START, 3000)
                    }
                    queue.add(p)
                    queue.add(Pair(Settings.Status.TRAIN, settings.tClass.roundsInMSec))
                }
                return queue
            }

            override fun onEnd() {
                settings.currentStatus = Settings.Status.STOP
                chronometerQueueChangeListener("Haste fein Gemacht!")
                chronometerComboStatus("")
            }

            override fun nextCombo(): Long {
                if (settings.currentStatus == Settings.Status.TRAIN) {
                    val i = chooseOnWeight(settings.combo)
                    chronometerComboStatus(i.toString())
                    val l: Int = i * settings.tClass.strikeDelateTime
                    return l.toLong()
                }
                return 100
            }

            override fun onPoll(status: Pair<Settings.Status, Int>) {
                settings.currentStatus = status.first
                if (status.first == Settings.Status.START) {
                    chronometerComboStatus("")
                    chronometerQueueChangeListener("GET READY!!")
                }

                if (status.first == Settings.Status.REST) {
                    chronometerComboStatus("")
                    chronometerQueueChangeListener("REST")
                }

                if (status.first == Settings.Status.TRAIN) {
                    settings.currentRound += 1
                    chronometerQueueChangeListener("Round " + settings.currentRound)
                    startComboTimer()
                }
            }

            override fun onTickRound(millis: Long) {
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

                );
            }


        }
        timer.start()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun chooseOnWeight(combo: Combo): Int {
        var completeWeight = combo.weights.stream().mapToInt(Int::toInt).sum()
        val r = Math.random() * completeWeight
        var countWeight = 0.0
        for (i in 0..combo.strikes.size) {
            countWeight += combo.weights[i]
            if (countWeight >= r)
                return i + 1
        }
        throw RuntimeException("Should never be shown.")
    }
}