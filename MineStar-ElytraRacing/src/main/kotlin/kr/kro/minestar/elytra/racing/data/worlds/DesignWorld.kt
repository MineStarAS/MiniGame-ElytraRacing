package kr.kro.minestar.elytra.racing.data.worlds

import kr.kro.minestar.utility.item.addLore
import kr.kro.minestar.utility.item.display
import kr.kro.minestar.utility.material.item
import org.bukkit.*
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent

class DesignWorld(world: World) : WorldData(world) {
    init {
        if (!ymlFile.exists()) {
            yml["Icon"] = Material.GRASS_BLOCK.item()
        }
    }

    val editStartGoalLocTool = Material.CLOCK.item().display("Start/Goal Location Edit Tool")
        .addLore("§8[Left Click] set Start Location")
        .addLore("§8[Right Click] set Goal Location")

    val editBoosterLocTool = Material.CLOCK.item().display("Booster Location Edit Tool")
        .addLore("§8[Left Click] add Booster Location")
        .addLore("§8[Right Click] remove Booster Location")

    @EventHandler
    fun useTool(e: PlayerInteractEvent) {
        if (!e.player.isOp) return

        val player = e.player
        val item = e.item ?: return

        when(item) {
            editStartGoalLocTool -> {}
            editBoosterLocTool -> {}

            else -> return
        }
    }
}