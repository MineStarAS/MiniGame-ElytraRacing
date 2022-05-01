package kr.kro.minestar.elytra.racing.funcions.events

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object BoosterEvent : Listener {
    val playerBoosterDataMap = hashMapOf<Player, PlayerBoostData>()

    @EventHandler
    fun getBooster() {

    }

}