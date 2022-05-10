package kr.kro.minestar.elytra.racing.funcions

import kr.kro.minestar.elytra.racing.Main.Companion.pl
import kr.kro.minestar.utility.event.enable
import kr.kro.minestar.utility.string.toPlayer
import kr.kro.minestar.utility.string.toServer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.scheduler.BukkitTask

object TestClass : Listener {

    init {
//        enable(pl)
    }

    val locationMap = hashMapOf<Player, Location>()
    val taskMap = hashMapOf<Player, BukkitTask>()

    @EventHandler
    fun playerJoin(e: PlayerJoinEvent) {
        test(e.player)
        "test Start".toServer()
    }

    fun test(player: Player) {
        taskMap[player]?.cancel()
        taskMap[player] = Bukkit.getScheduler().runTaskTimer(pl, Runnable {
            val prevLoc = locationMap[player] ?: player.location
            val d = prevLoc.distance(player.location)
            locationMap[player] = player.location
            d.toString().toPlayer(player)
        }, 0, 20)
    }
}