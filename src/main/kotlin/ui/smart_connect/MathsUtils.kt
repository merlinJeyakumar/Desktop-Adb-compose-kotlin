package ui.smart_connect

import kotlin.math.roundToInt

fun Float.isFraction(): Boolean {
    return (this % 1) != 0f
}

fun Float.getDividends(threadCount: Int): MutableList<Pair<Int, Int>> {
    val dividendsList = mutableListOf<Pair<Int,Int>>()
    val part = (this/threadCount)
    var index = 0f
    for (i in 1..threadCount){
        //println("Rounded: ${Math.round(index+1)} ${Math.round(index+part)}")
        //println("Start:$index end:${index+part}")
        dividendsList.add((index + 1).roundToInt() to (index + part).roundToInt())
        index += part
    }
    return dividendsList
}