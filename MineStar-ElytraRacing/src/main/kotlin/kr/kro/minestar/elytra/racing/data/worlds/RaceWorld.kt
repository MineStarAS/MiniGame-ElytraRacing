package kr.kro.minestar.elytra.racing.data.worlds

import kr.kro.minestar.elytra.racing.Main.Companion.pl
import kr.kro.minestar.elytra.racing.data.locations.BoostLocation
import kr.kro.minestar.elytra.racing.data.locations.GoalLocation
import kr.kro.minestar.elytra.racing.data.locations.StartLocation
import kr.kro.minestar.elytra.racing.funcions.WorldClass
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

class RaceWorld(world: World) : WorldData(world) {
}