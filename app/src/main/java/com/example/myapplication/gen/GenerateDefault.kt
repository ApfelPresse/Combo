package com.example.myapplication.gen

import android.os.Build
import android.support.annotation.RequiresApi
import com.example.myapplication.model.TClass
import com.example.myapplication.model.combo.Combo
import com.example.myapplication.model.combo.Strike
import kotlinx.serialization.json.Json
import kotlinx.serialization.list

/**
 * Nur eine Hilfsklasse, der content könnte später dynmaisch geladen werden
 *
 */

public fun getTClasses(): String {
    val classD = TClass("D-Class")
    classD.rounds = 3
    classD.roundsInMSec = 20000
    classD.breaksInMSec = 5000

    val classC = TClass("C-Class")
    classC.rounds = 5
    classC.roundsInMSec = 120000

    val classB = TClass("B-Class")
    classB.rounds = 5
    classB.roundsInMSec = 180000

    val classA = TClass("A-Class")
    classA.rounds = 7
    classA.roundsInMSec = 180000

    val classes: MutableList<TClass> = ArrayList()
    classes.add(classA)
    classes.add(classB)
    classes.add(classC)
    classes.add(classD)

    return Json.stringify(TClass.serializer().list, classes)
}

@RequiresApi(Build.VERSION_CODES.N)
public fun getCombos(): String {
    val strikes = Json.parse(Strike.serializer().list, getStrikes())

    val jab = strikes.stream().filter { t -> t.name.equals("Jab") }.findFirst().orElse(null)
    val cross = strikes.stream().filter { t -> t.name.equals("Cross") }.findFirst().orElse(null)
    val hookLeft = strikes.stream().filter { t -> t.name.equals("Hook Left") }.findFirst().orElse(null)
    val hookRight = strikes.stream().filter { t -> t.name.equals("Hook Right") }.findFirst().orElse(null)
    val uppercut = strikes.stream().filter { t -> t.name.equals("Uppercut") }.findFirst().orElse(null)
    val bodyLeft = strikes.stream().filter { t -> t.name.equals("Body Left") }.findFirst().orElse(null)

    val combo1 = Combo("Combo1")
    combo1.strikes.addAll(listOf(jab, cross, hookLeft, hookRight, uppercut))
    combo1.weights = MutableList(combo1.strikes.size) {index -> 1}

    val combo2 = Combo("Combo2")
    combo2.strikes.addAll(listOf(jab, cross, hookLeft, bodyLeft))
    combo2.weights = MutableList(combo2.strikes.size) {index -> 1}

    val combos: MutableList<Combo> = ArrayList()
    combos.add(combo1)
    combos.add(combo2)

    return Json.stringify(Combo.serializer().list, combos)
}

private fun getStrikes(): String {
    val jab = Strike("Jab", "J")
    val cross = Strike("Cross", "C")
    val hookLeft = Strike("Hook Left", "HL")
    val hookRight = Strike("Hook Right", "HR")
    val bodyLeft = Strike("Body Left", "BL")
    val bodyRight = Strike("Body Right", "BR")
    val uppercut = Strike("Uppercut", "U")

    val lowKickLeft = Strike("Lowkick Left", "LL")
    val lowKickRight = Strike("Lowkick Right", "LR")
    val middleKickLeft = Strike("Middlekick Left", "ML")
    val middleKickRight = Strike("Middlekick Right", "MR")
    val highKickLeft = Strike("Highkick Left", "HIL")
    val highKickRight = Strike("Highkick Right", "HIR")

    val strikes: MutableList<Strike> = ArrayList()
    strikes.add(jab)
    strikes.add(cross)
    strikes.add(hookLeft)
    strikes.add(hookRight)
    strikes.add(bodyLeft)
    strikes.add(bodyRight)
    strikes.add(uppercut)
    strikes.add(lowKickLeft)
    strikes.add(lowKickRight)
    strikes.add(middleKickLeft)
    strikes.add(middleKickRight)
    strikes.add(highKickLeft)
    strikes.add(highKickRight)

    return Json.stringify(Strike.serializer().list, strikes)
}