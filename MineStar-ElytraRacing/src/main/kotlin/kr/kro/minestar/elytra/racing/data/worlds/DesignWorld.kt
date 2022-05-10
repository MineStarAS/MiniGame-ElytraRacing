package kr.kro.minestar.elytra.racing.data.worlds

import kr.kro.minestar.elytra.racing.funcions.ItemClass
import kr.kro.minestar.elytra.racing.funcions.WorldClass
import kr.kro.minestar.utility.location.directionZero
import kr.kro.minestar.utility.location.toCenter
import kr.kro.minestar.utility.location.toFloorCenter
import kr.kro.minestar.utility.unit.setTrue
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerGameModeChangeEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.EquipmentSlot
import kotlin.math.absoluteValue

class DesignWorld(world: World) : WorldData(world) {

    init {
        init()
        WorldClass.addDesignWorld(this)
    }

    fun save() {
        world.save()
        WorldClass.fileCopy(world.worldFolder, folder)
    }

    /**
     * Event function
     */
    @EventHandler
    override fun playerMove(e: PlayerMoveEvent) {
        super.playerMove(e)
    }

    @EventHandler
    fun gameModeChange(e: PlayerGameModeChangeEvent) {
        if (e.player.world != world) return
        if (!e.player.isOp) return
        if (e.newGameMode != GameMode.ADVENTURE) return
        val inventory = e.player.inventory
        inventory.clear()
        inventory.chestplate = ItemClass.elytra
        inventory.setItemInOffHand(ItemClass.fireworkRocket)
        e.player.teleport(startLocation())
    }

    @EventHandler
    override fun damageFixed(e: EntityDamageEvent) {
        super.damageFixed(e)
    }

    /**
     * Edit function
     */
    fun setGoalMark(location: Location) {
        for (mark in marks()) if (isGoalMark(mark)) mark.remove()
        val mark = summonMark(location.toFloorCenter().directionZero())
        mark.customName = "goal"
    }

    fun addBoosterMark(location: Location): Boolean {
        val centerLocation = location.setUp()
        for (mark in nearMarks(centerLocation, 3.0)) if (isBoosterMark(mark)) return false
        val mark = summonMark(centerLocation)
        mark.customName = "booster"
        return true
    }

    fun removeBoosterMark(location: Location): Boolean {
        val marks = nearMarks(location, 3.0)
        if (marks.isEmpty()) return false
        val mark = marks.first()
        if (!isBoosterMark(mark)) return false
        return mark.remove().setTrue()
    }

    private fun summonMark(location: Location): ArmorStand {
        val mark = world.spawn(location, ArmorStand::class.java)
        mark.setGravity(false)
        mark.isInvisible = true
        mark.isSmall = true
        mark.isMarker = true
        mark.isCustomNameVisible = false
        mark.setBasePlate(false)
        return mark
    }

    /**
     * Utility function
     */
    private fun String.title(player: Player) = player.sendTitle(" ", this, 5, 10, 5)

    private fun Location.setUp(): Location {
        toCenter()
        val newYaw = when (yaw.toInt().absoluteValue) {
            in 0 until 8 -> 0
            in 8 until 23 -> 15
            in 23 until 38 -> 30
            in 38 until 53 -> 45
            in 53 until 68 -> 60
            in 68 until 83 -> 75
            in 83 until 98 -> 90
            in 98 until 113 -> 105
            in 113 until 128 -> 120
            in 128 until 143 -> 135
            in 143 until 158 -> 150
            in 158 until 173 -> 165
            in 173 until 180 -> 180
            else -> 0
        }
        yaw = if (yaw < 0) -newYaw.toFloat()
        else newYaw.toFloat()
        return this
    }
}