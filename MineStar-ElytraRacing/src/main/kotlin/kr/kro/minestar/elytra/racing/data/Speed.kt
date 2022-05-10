package kr.kro.minestar.elytra.racing.data

import kr.kro.minestar.utility.number.round
import org.bukkit.entity.Player

class Speed(player: Player) {
    private val value = player.velocity.length()
    internal val kmForHour = (value * 20 * 3.6).round(1)
    override fun toString() = "$kmForHour km/h"
}