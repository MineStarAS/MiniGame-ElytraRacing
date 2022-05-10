package kr.kro.minestar.elytra.racing.funcions

import kr.kro.minestar.elytra.racing.Main.Companion.pl
import kr.kro.minestar.elytra.racing.data.player.DesignData
import kr.kro.minestar.utility.event.enable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object AlwaysEvent : Listener {
    init {
        enable(pl)
    }

    /**
     * DesignData class Management
     */
    @EventHandler
    private fun playerJoinEvent(e: PlayerJoinEvent) {
        val player = e.player

        if (!player.isOp) return
        WorldClass.getDesignWorld(player.world) ?: return

        val data = DesignData.getDesignData(player)
        if (DesignData.getDesignData(player) != null) return data!!.enable(pl)

        DesignData(player)
    }

    @EventHandler
    private fun playerQuitEvent(e: PlayerQuitEvent) = DesignData.getDesignData(e.player)?.disable()
}