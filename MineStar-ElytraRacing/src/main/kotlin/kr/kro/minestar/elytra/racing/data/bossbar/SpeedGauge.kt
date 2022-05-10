package kr.kro.minestar.elytra.racing.data.bossbar

import kr.kro.minestar.elytra.racing.data.Speed
import kr.kro.minestar.elytra.racing.data.timer.GameTimer
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.Player

class SpeedGauge(val player: Player){
    private val bar = Bukkit.createBossBar("", BarColor.BLUE, BarStyle.SEGMENTED_10).apply {
        addPlayer(player)
        isVisible = true
    }

    internal fun display(gameTimer: GameTimer) {
        val speed = Speed(player)
        val limit = 180
        val percent = if (speed.kmForHour > limit) 1.0
        else speed.kmForHour / limit
        bar.progress = percent
        bar.setTitle("${gameTimer.display()} §e[ §a$speed §e]")
    }

    internal fun disable() = bar.removeAll()
}