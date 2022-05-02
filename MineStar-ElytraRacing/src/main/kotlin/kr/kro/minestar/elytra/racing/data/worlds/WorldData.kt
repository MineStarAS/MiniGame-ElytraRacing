package kr.kro.minestar.elytra.racing.data.worlds

import kr.kro.minestar.elytra.racing.Main
import kr.kro.minestar.elytra.racing.data.locations.BoostLocation
import kr.kro.minestar.elytra.racing.data.locations.GoalLocation
import kr.kro.minestar.elytra.racing.data.locations.StartLocation
import kr.kro.minestar.utility.event.enable
import kr.kro.minestar.utility.inventory.hasSameItem
import kr.kro.minestar.utility.material.item
import kr.kro.minestar.utility.sound.PlaySound
import org.bukkit.*
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import java.io.File

abstract class WorldData(val world: World) : Listener {
    protected val folder = world.worldFolder
    protected val ymlFile = File(folder, "worldData.yml")
    protected val yml = YamlConfiguration.loadConfiguration(ymlFile)

    protected val startLoc: StartLocation = StartLocation(yml.getLocation("StartLocation") ?: world.spawnLocation)
    protected val goalLoc: GoalLocation = GoalLocation(yml.getLocation("GoalLocation") ?: world.spawnLocation)

    protected val boostLocSet = hashSetOf<BoostLocation>()
    protected val boostDelaySet = hashSetOf<Player>()

    init {
        val keys = yml.getKeys(true)

        for (key in keys) {
            val loc = yml.getLocation(key) ?: continue

            when (true) {
                key in "BoostLocation" -> boostLocSet.add(BoostLocation(loc))
                else -> continue
            }
        }

        enable(Main.pl)
    }

    protected val giveBoosterSound = PlaySound().apply {
        soundCategory = SoundCategory.RECORDS
        sound = Sound.ENTITY_PLAYER_LEVELUP
        pitch = 1.5F
    }

    @EventHandler
    protected fun move(e: PlayerMoveEvent) {
        if (e.player.world != world) return
        if (e.player.gameMode != GameMode.ADVENTURE) return

        val player = e.player
        val loc = player.location

        fun giveBooster() {
            val booster = Material.FIREWORK_ROCKET.item()

            if (player.inventory.hasSameItem(booster)) return
            if (boostDelaySet.contains(player)) return

            boostDelaySet.add(player)
            Bukkit.getScheduler().runTaskLater(Main.pl, Runnable { boostDelaySet.remove(player) }, 10)

            player.inventory.setItemInOffHand(booster)
            giveBoosterSound.play(player)
        }

        fun goalIn() {

        }

        when (true) {
            player.isGliding -> for (boostLoc in boostLocSet) {
                if (boostLoc.location.distance(loc) > 3) continue
                else return giveBooster()
            }
            player.isOnGround -> if (goalLoc.location.distance(loc) > 3) goalIn()
            else -> return
        }
    }
}