package kr.kro.minestar.elytra.racing.data.timer

import kr.kro.minestar.elytra.racing.Main.Companion.pl
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask

open class GameTimer {

    var task: BukkitTask? = null

    private var min = 0
    private fun addMin() {
        ++min
    }

    private var sec = 0
    private fun addSec() {
        if (sec >= 59) {
            sec = 0
            addMin()
        } else ++sec
    }

    private var tick = 0
    private fun addTick() {
        if (tick >= 19) {
            tick = 0
            addSec()
        } else ++tick
    }

    /**
     * Function
     */
    fun display(): String {
        val tickString = if (tick <= 1) "0${tick * 5}"
        else "${tick * 5}"
        val secString = if (sec <= 9) "0$sec"
        else "$sec"
        return "${min}:$secString.$tickString"
    }

    internal fun start() {
        finish()

        min = 0
        sec = 0
        tick = 0

        task = Bukkit.getScheduler().runTaskTimer(pl, Runnable { addTick() }, 0, 1)
    }

    internal fun finish() {
        task?.cancel()
        task = null
    }

    internal fun createTimestamp() = Timestamp(display())
}